package com.rzq.smarthomestay.service.impl;

import com.rzq.smarthomestay.entity.*;
import com.rzq.smarthomestay.exception.CustomException;
import com.rzq.smarthomestay.model.*;
import com.rzq.smarthomestay.repository.AdditionalFacilityRepository;
import com.rzq.smarthomestay.repository.AuditRepository;
import com.rzq.smarthomestay.repository.RoomRepository;
import com.rzq.smarthomestay.repository.TransactionRepository;
import com.rzq.smarthomestay.service.TransactionService;
import com.rzq.smarthomestay.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    ValidationService validationService;

    @Override
    public TransactionGetDetailsResponse getById(String token, String id) {
        return null;
    }

    @Override
    public List<TransactionGetResponse> getMyTransaction(String token) {
        return null;
    }

    @Override
    public List<TransactionGetResponse> getAllTransaction(String token) {
        return null;
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


        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setCreatedBy(user);
        transaction.setCreatedAt(now);
        transaction.setUpdatedAt(now);
        transaction.setStatus(status);
        transaction.setLastAction(lastAction);
        transaction.setLastActivity(lastActivity);
        transaction.setPendingUser(user);
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
        return null;
    }

    private Long sumAdditionalFacilitiesPrice(Set<AdditionalFacility> additionalFacilities){
        Long sum = new Long(0);
        for(AdditionalFacility additionalFacility: additionalFacilities){
            sum+=additionalFacility.getPrice();
        }
        return sum;
    }

    private TransactionOrderResponse toTransactionOrderResponse(Transaction transaction){
        System.out.println("DISINIIIIIIIIIIIII");
        UserDetailsResponse pendingUser = new UserDetailsResponse();
        pendingUser.setName(transaction.getPendingUser().getName());
        pendingUser.setUsername(transaction.getPendingUser().getUsername());


        System.out.println("DISINIIIIIIIIIIIII2");
        UserDetailsResponse createdBy = new UserDetailsResponse();
        createdBy.setName(transaction.getCreatedBy().getName());
        createdBy.setUsername(transaction.getCreatedBy().getUsername());


        System.out.println("DISINIIIIIIIIIIIII3");
        RoomDetailsResponse room = new RoomDetailsResponse();
        room.setId(transaction.getRoom().getId());

        System.out.println("DISINIIIIIIIIIIIII4");
        RoomCategoryCreateResponse roomCategory = new RoomCategoryCreateResponse();
        roomCategory.setId(transaction.getRoom().getRoomCategory().getId());
        roomCategory.setName(transaction.getRoom().getRoomCategory().getName());

        System.out.println("DISINIIIIIIIIIIIII5");
        room.setRoomCategory(roomCategory);
        room.setPrice(transaction.getRoom().getPrice());


        System.out.println("DISINIIIIIIIIIIIII6");
        Set<AdditionalFacilityCreateResponse> additionalFacilities = new HashSet<>();
        for (AdditionalFacility additionalFacility: transaction.getAdditionalFacilities()){
            AdditionalFacilityCreateResponse resp = new AdditionalFacilityCreateResponse();
            resp.setId(additionalFacility.getId());
            resp.setName(additionalFacility.getName());
            resp.setPrice(additionalFacility.getPrice());
            additionalFacilities.add(resp);
        }



        System.out.println("DISINIIIIIIIIIIIII10");
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
}
