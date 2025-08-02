package com.example.cms.order.client.user;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Data
public class SendMailForm {
    private String from;
    private String to;
    private String subject;
    private String text;
}
