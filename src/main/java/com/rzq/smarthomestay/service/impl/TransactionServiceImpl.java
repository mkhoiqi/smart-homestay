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
    RoomRepository roomRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ValidationService validationService;

    @Override
    public TransactionGetDetailsResponse getById(String token, String id) {
        User user = validationService.validateToken(token);
        if(!user.getIsEmployees()){
            Transaction transaction = transactionRepository.findByIdAndCreatedBy(id, user).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found")
            );
            return toTransactionGetDetailsResponse(transaction);
        } else{
            Transaction transaction = transactionRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found")
            );
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

        if(request.getCheckinDate().isAfter(request.getCheckoutDate())){
            throw new CustomException(HttpStatus.BAD_REQUEST, "checkin_date", "checkin date can't occur after checkout date");
        }

        Long dateRange = ChronoUnit.DAYS.between(request.getCheckinDate(), request.getCheckoutDate());
        String status = "Waiting Approval";
        String lastAction = "Submitted";
        String lastActivity = "Submit Order";
        Long sumPriceAdditionalFacilities = sumAdditionalFacilitiesPrice(additionalFacilities);
        Long amount = (request.getNumberOfRooms()*room.getPrice()*dateRange)+sumPriceAdditionalFacilities;
        LocalDateTime now = LocalDateTime.now();

        User pendingUser = new User();
        pendingUser.setUsername("admin");
        pendingUser.setName("Admin");

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setCreatedBy(user);
        transaction.setCreatedAt(now);
        transaction.setUpdatedAt(now);
        transaction.setStatus(status);
        transaction.setLastAction(lastAction);
        transaction.setLastActivity(lastActivity);
        transaction.setPendingUser(pendingUser);
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

        Transaction transaction = transactionRepository.findByIdAndPendingUserAndStatusNotIn(id, user, excludedStatus).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found")
        );

        String status = null;
        String lastActivity = null;
        User pendingUser = transaction.getPendingUser();

        if(transaction.getStatus().equalsIgnoreCase("Waiting Approval")){
            //Kemungkinan action: Rejected, Approved (Employees)
            lastActivity = "Approval Employees";
            if(!action.equalsIgnoreCase("Rejected") && !action.equalsIgnoreCase("Approved")){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Action");
            } else{
                if(action.equalsIgnoreCase("Rejected")){
                    status = "Rejected";
//                    pendingUser = null;
                } else{
                    status = "Waiting Payment";
                    pendingUser = transaction.getCreatedBy();
                }
            }
        } else if(transaction.getStatus().equalsIgnoreCase("Waiting Payment")){
            //Kemungkinan action: Cancelled, Paid (User)
            lastActivity = "Payment";
            if(!action.equalsIgnoreCase("Cancelled") && !action.equalsIgnoreCase("Paid")){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Action");
            } else {
                if(action.equalsIgnoreCase("Cancelled")){
                    status = "Cancelled";
//                    pendingUser = null;
                } else{
                    status = "Payment Success";
                    pendingUser = transaction.getCreatedBy();
                }
            }
        } else if(transaction.getStatus().equalsIgnoreCase("Payment Success")){
            //Kemungkinan action: Checkedin (User)
            lastActivity = "Check In";
            if(!action.equalsIgnoreCase("Checkedin")){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Action");
            } else{
                status = "Checked In";
                pendingUser = transaction.getCreatedBy();
            }
        } else if (transaction.getStatus().equalsIgnoreCase("Checked In")) {
            //Kemungkinan action: Checkout (User)
            lastActivity = "Check Out";
            if(!action.equalsIgnoreCase("Checkedout")){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Action");
            } else{
                status = "Checked Out";
//                pendingUser = null;
            }
        }

        System.out.println("Here");
        LocalDateTime now = LocalDateTime.now();

        System.out.println("Here2");
        transaction.setUpdatedAt(now);
        transaction.setStatus(status);
        transaction.setLastAction(action);
        transaction.setLastActivity(lastActivity);
        transaction.setPendingUser(pendingUser);
        transactionRepository.save(transaction);

        System.out.println("Here 3");
        Audit audit = new Audit();
        audit.setId(UUID.randomUUID().toString());
        audit.setTransaction(transaction);
        audit.setActivity(lastActivity);
        audit.setAction(action);
        audit.setCreatedBy(user);
        audit.setCreatedAt(now);

        System.out.println("Here 4");
        auditRepository.save(audit);
        System.out.println("Here 5");
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
        UserDetailsResponse pendingUser = new UserDetailsResponse();
        pendingUser.setName(transaction.getPendingUser().getName());
        pendingUser.setUsername(transaction.getPendingUser().getUsername());


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
                .pendingUser(pendingUser)
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
                .createdBy(createdBy)
                .room(room)
                .additionalFacilities(additionalFacilityCreateResponses)
                .audits(auditResponses).build();
    }
}
