package com.rzq.smarthomestay.service;

import com.rzq.smarthomestay.entity.User;

public interface ValidationService {
    public void validate(Object object);
    public User validateToken(String token);
    public void validateDuplicateUsername(String username);
    public void validateDuplicateRoomCategoryName(String name);
    public void validateDuplicateRoomCategoryName(String name, String id);
    public void validateDuplicateAdditionalFacilityName(String name);
    public void validateDuplicateAdditionalFacilityName(String name, String id);
    public void validateDuplicateFacilityName(String name);
    public void validateDuplicateFacilityName(String name, String id);
}
