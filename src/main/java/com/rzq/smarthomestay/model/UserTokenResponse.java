package com.rzq.smarthomestay.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserTokenResponse {
    private String token;

    @Column(name = "token_expired_at")
    private Long tokenExpiredAt;
}
