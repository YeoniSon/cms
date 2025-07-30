package com.example.cms.user.client.service;

import com.example.cms.user.client.MailgunClient;
import com.example.cms.user.config.FeignConfig;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
class EmailSendServiceTest {

    @Autowired
    private MailgunClient mailgunClient;

    @Test
    public void EmailTest() {
        // need test code
        mailgunClient.sendEmail(null);
//        String response = emailSendService.sendEmail();
//        System.out.println(response);
    }
}