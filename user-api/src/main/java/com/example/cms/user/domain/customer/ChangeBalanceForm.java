package com.example.cms.user.domain.customer;

import io.swagger.models.auth.In;
import lombok.Getter;

@Getter
public class ChangeBalanceForm {
    private String from;
    private String message;
    private Integer money;
}
