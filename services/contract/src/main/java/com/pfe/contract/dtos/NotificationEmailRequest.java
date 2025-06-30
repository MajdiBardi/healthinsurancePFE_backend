package com.pfe.contract.dtos;

import lombok.Data;

@Data
public class NotificationEmailRequest {
    private String to;
    private String subject;
    private String body;
}
