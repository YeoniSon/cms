package com.example.cms.user.controller;

import com.example.cms.user.application.SignInApplication;
import com.example.cms.user.domain.SignInForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/signin")
@RequiredArgsConstructor
public class SignInController {

    private final SignInApplication signInApplication;

    @PostMapping("/customer")
    public ResponseEntity<String> signInCustomer(@RequestBody SignInForm form) {
        return ResponseEntity.ok(signInApplication.customerLoginToken(form));
    }

    @PostMapping("/seller")
    public ResponseEntity<String> signInSeller(@RequestBody SignInForm form) {
        return ResponseEntity.ok(signInApplication.sellerLoginToken(form));
    }
}
