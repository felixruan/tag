package org.etocrm.gateway.entity;

import lombok.Data;

@Data
public class Authentication {

    private Integer id;

    private String username;

    private String account;

    private String accessToken;
}
