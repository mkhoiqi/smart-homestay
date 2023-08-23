package com.rzq.smarthomestay.service.impl;

import com.rzq.smarthomestay.entity.*;
import com.rzq.smarthomestay.exception.CustomException;
import com.rzq.smarthomestay.model.*;
import com.rzq.smarthomestay.repository.*;
import com.rzq.smarthomestay.service.TransactionService;
import com.rzq.smarthomestay.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    AuditRepository auditRepository;

    @Autowired
    AdditionalFacilityRepository additionalFacilityRepository;

    @Autowired
    AdditionalFacilityAuditRepository additionalFacilityAuditRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    RoomAuditRepository roomAuditRepository;

    @Autowired
    RoomCategoryAuditRepository roomCategoryAuditRepository;

    @Autowired
    FacilityAuditRepository facilityAuditRepository;

    @Autowired
    ValidationService validationService;

    @Override
    public TransactionGetDetailsResponse getById(String token, String id) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            Transaction transaction = transactionRepository.findByIdAndCreatedBy(id, user).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found")
            );

            transaction.setAdditionalFacilities(getRealAdditionalFacilities(transaction.getAdditionalFacilities(), transaction.getCreatedAt()));
//            Room room = getRealRoom(transaction.getRoom(), transaction.getCreatedAt());
            transaction.setRoom(getRealRoom(transaction.getRoom(), transaction.getCreatedAt()));

            return toTransactionGetDetailsResponse(transaction);
        } else{
            Transaction transaction = transactionRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found")
            );

            transaction.setAdditionalFacilities(getRealAdditionalFacilities(transaction.getAdditionalFacilities(), transaction.getCreatedAt()));
            transaction.setRoom(getRealRoom(transaction.getRoom(), transaction.getCreatedAt()));

            return toTransactionGetDetailsResponse(transaction);
        }
    }

    @Override
    public List<TransactionGetResponse> getMyTransaction(String token) {
        User user = validationService.validateToken(token);
        if(user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Specification<Transaction> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("createdBy"), user));
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
        List<Transaction> responses = new ArrayList<>();

        responses = transactionRepository.findAll(specification);

        if(responses.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found");
        }

        return responses.stream()
                .map(response -> toTransactionGetResponse(response))
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionGetResponse> getAllTransaction(String token) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Specification<Transaction> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
        List<Transaction> responses = new ArrayList<>();

        responses = transactionRepository.findAll(specification);

        if(responses.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found");
        }

        return responses.stream()
                .map(response -> toTransactionGetResponse(response))
                .collect(Collectors.toList());
    }

    @Override
    public TransactionOrderResponse order(String token, TransactionOrderRequest request) {
        User user = validationService.validateToken(token);

        if(user.getIsEmployees()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        validationService.validate(request);

        Room room = roomRepository.findByIdAndDeletedAtIsNull(request.getRoomId()).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "room_id", "Not Found")
        );

        Set<AdditionalFacility> additionalFacilities = new HashSet<>();
        for(String additionalFacilityId : request.getAdditionalFacilities()){
            AdditionalFacility additionalFacility = additionalFacilityRepository.findByIdAndDeletedAtIsNull(additionalFacilityId).orElseThrow(
                    () -> new CustomException(HttpStatus.NOT_FOUND, "additional_facilities", "not found")
            );
            additionalFacilities.add(additionalFacility);
        }

        if(request.getCheckinDate().isBefore(LocalDate.now())){
            throw new CustomException(HttpStatus.BAD_REQUEST, "checkin_date", "checkin date can't occur before today");
        }

        if(request.getCheckoutDate().isBefore(LocalDate.now())){
            throw new CustomException(HttpStatus.BAD_REQUEST, "checkout_date", "checkout date can't occur before today");
        }

        if(!request.getCheckoutDate().isAfter(request.getCheckinDate())){
            throw new CustomException(HttpStatus.BAD_REQUEST, "checkout_date", "checkout date must occur after checkin date");
        }

        if(!availabilityCheck(room, request)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The room is not available for the given date range");
        }


        Long dateRange = ChronoUnit.DAYS.between(request.getCheckinDate(), request.getCheckoutDate());
        String status = "Waiting Approval";
        String lastAction = "Submitted";
        String lastActivity = "Submit Order";
        Long sumPriceAdditionalFacilities = sumAdditionalFacilitiesPrice(additionalFacilities);
        Long amount = (request.getNumberOfRooms()*room.getPrice()*dateRange)+sumPriceAdditionalFacilities;
        LocalDateTime now = LocalDateTime.now();

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setCreatedBy(user);
        transaction.setCreatedAt(now);
        transaction.setUpdatedAt(now);
        transaction.setStatus(status);
        transaction.setLastAction(lastAction);
        transaction.setLastActivity(lastActivity);
        transaction.setRoom(room);
        transaction.setNumberOfRooms(request.getNumberOfRooms());
        transaction.setCheckinDate(request.getCheckinDate());
        transaction.setCheckoutDate(request.getCheckoutDate());
        transaction.setAmount(amount);
        transaction.setAdditionalFacilities(additionalFacilities);

        transactionRepository.save(transaction);

        Audit audit = new Audit();
        audit.setId(UUID.randomUUID().toString());
        audit.setTransaction(transaction);
        audit.setActivity(lastActivity);
        audit.setAction(lastAction);
        audit.setCreatedBy(user);
        audit.setCreatedAt(now);

        auditRepository.save(audit);
        return toTransactionOrderResponse(transaction);
    }

    @Override
    public TransactionOrderResponse approval(String token, String id, String action) {
        User user = validationService.validateToken(token);

        List<String> excludedStatus = new ArrayList<>();
        excludedStatus.add("Rejected");
        excludedStatus.add("Cancelled");
        excludedStatus.add("Checked Out");
        excludedStatus.add("Expired");

        Transaction transaction = transactionRepository.findByIdAndStatusNotIn(id, excludedStatus).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found")
        );

        String status = null;
        String lastActivity = null;

        /**
         * Kemungkinan Alur:
         * VOrder (U) (Submitted) ->  Approval (E) (Rejected)
         * VOrder (U) (Submitted) ->  Approval (E) (Approved) -> Payment (E) (Intervened) (Intervened bisa dilakukan bila VA expired)
         * VOrder (U) (Submitted) ->  Approval (E) (Approved) -> Payment (U) (Cancelled)
         * VOrder (U) (Submitted) ->  Approval (E) (Approved) -> Payment (U) (Paid) -> Checkin (E) (Intervened) (Intervened bisa dilakukan bila sudah melewati tanggal Checkout)
         * VOrder (U) (Submitted) ->  Approval (E) (Approved) -> Payment (U) (Paid) -> Checkin (U) (Checkedin) -> Checkout (E) (Intervened) (Intervened bisa dilakukan bila sudah melewati tanggal Checkout)
         * Order (U) (Submitted) ->  Approval (E) (Approved) -> Payment (U) (Paid) -> Checkin (U) (Checkedin) -> Checkout (U) (Checkedout)
         */

        if(transaction.getStatus().equalsIgnoreCase("Waiting Approval")){ //Approval
            //Kemungkinan action: Rejected, Approved (Employees)
            if(!user.getIsEmployees()){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
            }

            lastActivity = "Approval Employees";

            if(!action.equalsIgnoreCase("Rejected") && !action.equalsIgnoreCase("Approved")){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Action");
            } else{
                if(action.equalsIgnoreCase("Rejected")){
                    status = "Rejected";
                } else{
                    status = "Waiting Payment";
                }
            }
        } else if(transaction.getStatus().equalsIgnoreCase("Waiting Payment")){ //Payment
            //Kemungkinan action: Cancelled, Paid (User), Intervened (Employees)

            if(user.getIsEmployees()){ //Employees
                if(transaction.getVirtualAccountExpiredAt().isBefore(LocalDateTime.now())){ //udah lewat

                    if(!action.equalsIgnoreCase("Intervened")){
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Action");
                    }
                    lastActivity = "Intervention";

                    status = "Expired";
                } else{
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");

                }
            } else if(!user.equals(transaction.getCreatedBy())){ //User tapi bukan yang order

                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");

            } else{ //User yang order
                if(transaction.getVirtualAccountExpiredAt().isBefore(LocalDateTime.now())){ //udah lewat
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The payment virtual account has expired");
                }
                lastActivity = "Payment";
                if(!action.equalsIgnoreCase("Cancelled") && !action.equalsIgnoreCase("Paid")){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Action");
                } else {
                    if(action.equalsIgnoreCase("Cancelled")){
                        status = "Cancelled";
                    } else{
                        status = "Payment Success";
                    }
                }
            }
        } else if(transaction.getStatus().equalsIgnoreCase("Payment Success")){ //Checkin
            //Kemungkinan action: Checkedin (User), Intervened (Employees)
            if(user.getIsEmployees()){
                if(!transaction.getCheckoutDate().isBefore(LocalDate.now())){
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
                } else{
                    lastActivity = "Intervention";
                    if(!action.equalsIgnoreCase("Intervened")){
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Action");
                    } else{
                        status = "Expired";
                    }
                }
            } else if(!user.equals(transaction.getCreatedBy())){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
            } else{
                lastActivity = "Check In";
                if(!action.equalsIgnoreCase("Checkedin")){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Action");
                } else{
                    if(LocalDate.now().isBefore(transaction.getCheckinDate())){
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"It's not time to check in yet.");
                    }
                    status = "Checked In";
                }

            }
        } else if (transaction.getStatus().equalsIgnoreCase("Checked In")) { //Checkout
            //Kemungkinan action: Checkedout (User), Intervented (Employees)
            if(user.getIsEmployees()){
                if(!transaction.getCheckoutDate().isBefore(LocalDate.now())){
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
                } else{
                    lastActivity = "Intervention";
                    if(!action.equalsIgnoreCase("Intervened")){
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Action");
                    } else{
                        status = "Expired";
                    }
                }
            } else if(!user.equals(transaction.getCreatedBy())){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
            } else{
                lastActivity = "Check Out";
                if(!action.equalsIgnoreCase("Checkedout")){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Action");
                } else{
                    status = "Checked Out";
                }
            }
        }

        LocalDateTime now = LocalDateTime.now();

        if(transaction.getStatus().equalsIgnoreCase("Waiting Approval") && action.equalsIgnoreCase("Approved")){
            transaction.setVirtualAccount(UUID.randomUUID().toString());
//            transaction.setVirtualAccountExpiredAt(LocalDateTime.now().plusHours(1));
            transaction.setVirtualAccountExpiredAt(LocalDateTime.now().plusMinutes(3));
        }

        transaction.setUpdatedAt(now);
        transaction.setStatus(status);
        transaction.setLastAction(action);
        transaction.setLastActivity(lastActivity);
        transactionRepository.save(transaction);


        Audit audit = new Audit();
        audit.setId(UUID.randomUUID().toString());
        audit.setTransaction(transaction);
        audit.setActivity(lastActivity);
        audit.setAction(action);
        audit.setCreatedBy(user);
        audit.setCreatedAt(now);

        auditRepository.save(audit);
        return toTransactionOrderResponse(transaction);
    }

    private Long sumAdditionalFacilitiesPrice(Set<AdditionalFacility> additionalFacilities){
        Long sum = new Long(0);
        for(AdditionalFacility additionalFacility: additionalFacilities){
            sum+=additionalFacility.getPrice();
        }
        return sum;
    }

    private TransactionOrderResponse toTransactionOrderResponse(Transaction transaction){


        UserDetailsResponse createdBy = new UserDetailsResponse();
        createdBy.setName(transaction.getCreatedBy().getName());
        createdBy.setUsername(transaction.getCreatedBy().getUsername());


        RoomDetailsResponse room = new RoomDetailsResponse();
        room.setId(transaction.getRoom().getId());

        RoomCategoryCreateResponse roomCategory = new RoomCategoryCreateResponse();
        roomCategory.setId(transaction.getRoom().getRoomCategory().getId());
        roomCategory.setName(transaction.getRoom().getRoomCategory().getName());

        room.setRoomCategory(roomCategory);
        room.setPrice(transaction.getRoom().getPrice());


        Set<AdditionalFacilityCreateResponse> additionalFacilities = new HashSet<>();
        for (AdditionalFacility additionalFacility: transaction.getAdditionalFacilities()){
            AdditionalFacilityCreateResponse resp = new AdditionalFacilityCreateResponse();
            resp.setId(additionalFacility.getId());
            resp.setName(additionalFacility.getName());
            resp.setPrice(additionalFacility.getPrice());
            additionalFacilities.add(resp);
        }

        return TransactionOrderResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .checkinDate(transaction.getCheckinDate())
                .checkoutDate(transaction.getCheckoutDate())
                .createdAt(transaction.getCreatedAt())
                .lastAction(transaction.getLastAction())
                .lastActivity(transaction.getLastActivity())
                .numberOfRooms(transaction.getNumberOfRooms())
                .status(transaction.getStatus())
                .createdBy(createdBy)
                .room(room)
                .additionalFacilities(additionalFacilities).build();
    }

    private TransactionGetResponse toTransactionGetResponse(Transaction transaction){
        UserDetailsResponse createdBy = new UserDetailsResponse();
        createdBy.setUsername(transaction.getCreatedBy().getUsername());
        createdBy.setName(transaction.getCreatedBy().getName());


        RoomDetailsResponse room = new RoomDetailsResponse();
        room.setId(transaction.getRoom().getId());
        room.setPrice(transaction.getRoom().getPrice());

        RoomCategoryCreateResponse roomCategory = new RoomCategoryCreateResponse();
        roomCategory.setId(transaction.getRoom().getRoomCategory().getId());
        roomCategory.setName(transaction.getRoom().getRoomCategory().getName());
        room.setRoomCategory(roomCategory);

        return TransactionGetResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .checkinDate(transaction.getCheckinDate())
                .checkoutDate(transaction.getCheckoutDate())
                .createdAt(transaction.getCreatedAt())
                .numberOfRooms(transaction.getNumberOfRooms())
                .status(transaction.getStatus())
                .createdBy(createdBy)
                .room(room).build();
    }

    private TransactionGetDetailsResponse toTransactionGetDetailsResponse(Transaction transaction){
        UserDetailsResponse createdBy = new UserDetailsResponse();
        createdBy.setName(transaction.getCreatedBy().getName());
        createdBy.setUsername(transaction.getCreatedBy().getUsername());


        RoomCreateResponse room = new RoomCreateResponse();
        room.setId(transaction.getRoom().getId());
        room.setNumberOfRooms(transaction.getRoom().getNumberOfRooms());
        room.setPrice(transaction.getRoom().getPrice());

        Set<FacilityCreateResponse> facilityCreateResponses = new HashSet<>();
        for(Facility facility: transaction.getRoom().getFacilities()){
            FacilityCreateResponse resp = new FacilityCreateResponse();
            resp.setId(facility.getId());
            resp.setName(facility.getName());
            facilityCreateResponses.add(resp);
        }

        room.setFacilities(facilityCreateResponses);

        RoomCategoryCreateResponse roomCategoryCreateResponse = new RoomCategoryCreateResponse();
        roomCategoryCreateResponse.setId(transaction.getRoom().getRoomCategory().getId());
        roomCategoryCreateResponse.setName(transaction.getRoom().getRoomCategory().getName());

        room.setRoomCategory(roomCategoryCreateResponse);


        Set<AdditionalFacilityCreateResponse> additionalFacilityCreateResponses = new HashSet<>();
        for (AdditionalFacility additionalFacility: transaction.getAdditionalFacilities()){
            AdditionalFacilityCreateResponse resp = new AdditionalFacilityCreateResponse();
            resp.setId(additionalFacility.getId());
            resp.setName(additionalFacility.getName());
            resp.setPrice(additionalFacility.getPrice());
            additionalFacilityCreateResponses.add(resp);
        }


        Set<AuditResponse> auditResponses = new HashSet<>();
        for (Audit audit: transaction.getAudits()){
            AuditResponse auditResponse = new AuditResponse();
            auditResponse.setId(audit.getId());
            auditResponse.setAction(audit.getAction());
            auditResponse.setActivity(audit.getActivity());

            UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
            userDetailsResponse.setName(audit.getCreatedBy().getName());
            userDetailsResponse.setUsername(audit.getCreatedBy().getUsername());

            auditResponse.setCreatedBy(userDetailsResponse);
            auditResponse.setCreatedAt(audit.getCreatedAt());
            auditResponses.add(auditResponse);
        }

        return TransactionGetDetailsResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .checkinDate(transaction.getCheckinDate())
                .checkoutDate(transaction.getCheckoutDate())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .lastAction(transaction.getLastAction())
                .lastActivity(transaction.getLastActivity())
                .numberOfRooms(transaction.getNumberOfRooms())
                .status(transaction.getStatus())
                .virtualAccount(transaction.getVirtualAccount())
                .virtualAccountExpiredAt(transaction.getVirtualAccountExpiredAt())
                .createdBy(createdBy)
                .room(room)
                .additionalFacilities(additionalFacilityCreateResponses)
                .audits(auditResponses).build();
    }

    private boolean availabilityCheck(Room room, TransactionOrderRequest request){
        Integer numberOfRooms = room.getNumberOfRooms();

        List<String> excludedStatus = new ArrayList<>();
        excludedStatus.add("Rejected");
        excludedStatus.add("Cancelled");
        excludedStatus.add("Checked Out");
        excludedStatus.add("Expired");

        LocalDateTime dateTime = LocalDateTime.now();

        LocalDate currDate = request.getCheckinDate();
        while (currDate.isBefore(request.getCheckoutDate())){
            Integer bookedRoom = transactionRepository.countBookedRoom(excludedStatus, room, currDate, dateTime);
            System.out.println("Booked "+currDate+": "+bookedRoom);
            Integer availableRoom = numberOfRooms-bookedRoom;

            if(availableRoom<request.getNumberOfRooms()){
                return false;
            }
            currDate = currDate.plusDays(1);
        }

        return true;
    }

    public Set<AdditionalFacility> getRealAdditionalFacilities(Set<AdditionalFacility> currentAdditionalFacilities, LocalDateTime transactionDateTime){
        Set<AdditionalFacility> realAdditionalFacilities = new HashSet<>();
        for (AdditionalFacility additionalFacility: currentAdditionalFacilities){
            List<AdditionalFacilityAudit> additionalFacilityAudits = additionalFacilityAuditRepository.getAdditionalFacilityAuditBeforeTransaction(additionalFacility, transactionDateTime);
            if(!additionalFacilityAudits.isEmpty()){
                additionalFacility.setName(additionalFacilityAudits.get(0).getName());
                additionalFacility.setPrice(additionalFacilityAudits.get(0).getPrice());
            }
            realAdditionalFacilities.add(additionalFacility);
        }
        return realAdditionalFacilities;
    }

    public Room getRealRoom(Room currentRoom, LocalDateTime transactionCreatedAt){

        List<RoomAudit> roomAudits = roomAuditRepository.getRoomAuditBeforeTransaction(currentRoom, transactionCreatedAt);
        if(!roomAudits.isEmpty()){
            currentRoom.setPrice(roomAudits.get(0).getPrice());
            currentRoom.setNumberOfRooms(roomAudits.get(0).getNumberOfRooms());
            currentRoom.setRoomCategory(getRealRoomCategory(roomAudits.get(0).getRoomCategory(), transactionCreatedAt));
            currentRoom.setFacilities(getRealFacilities(roomAudits.get(0).getFacilities(), transactionCreatedAt));
        }

        return currentRoom;
    }

    public RoomCategory getRealRoomCategory(RoomCategory currentRoomCategory, LocalDateTime transactionCreatedAt){

        List<RoomCategoryAudit> roomCategoryAudits = roomCategoryAuditRepository.getRoomCategoryAuditBeforeTransaction(currentRoomCategory, transactionCreatedAt);
        if(!roomCategoryAudits.isEmpty()){
            currentRoomCategory.setName(roomCategoryAudits.get(0).getName());
        }

        return currentRoomCategory;
    }

    public Set<Facility> getRealFacilities(Set<Facility> currentFacilities, LocalDateTime transactionCreatedAt){

        Set<Facility> realFacilities = new HashSet<>();
        for(Facility currentFacility: currentFacilities){
            List<FacilityAudit> facilityAudits = facilityAuditRepository.getFacilityAuditBeforeTransaction(currentFacility, transactionCreatedAt);
            if(!facilityAudits.isEmpty()){
                currentFacility.setName(facilityAudits.get(0).getName());
            }
            realFacilities.add(currentFacility);
        }

        return realFacilities;
    }
}
