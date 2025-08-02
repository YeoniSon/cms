package com.example.cms.user.controller;

import com.example.cms.user.client.MailgunClient;
import com.example.cms.user.client.mailgun.SendMailForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final MailgunClient mailgunClient;

    @PostMapping("/order")
    public ResponseEntity<String> sendOrderEmail(
            @RequestBody SendMailForm form
    ){
        return ResponseEntity.ok(
                mailgunClient.sendEmail(form).getBody()
        );
    };
}
