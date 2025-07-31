package com.example.cms.user.controller;

import com.example.cms.user.domain.customer.ChangeBalanceForm;
import com.example.cms.user.domain.customer.CustomerDto;
import com.example.cms.user.domain.model.Customer;
import com.example.cms.user.exception.CustomException;
import com.example.cms.user.service.CustomerBalanceService;
import com.example.cms.user.service.customer.CustomerService;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import com.zerobase.domain.domain.common.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.cms.user.exception.ErrorCode.NOT_FOUND_USER;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final JwtAuthenticationProvider provider;
    private final CustomerService customerService;
    private final CustomerBalanceService customerBalanceService;

    @GetMapping("/getInfo")
    public ResponseEntity<CustomerDto> getInfo(
            @RequestHeader(name = "X-AUTH-TOKEN"
            ) String token
    ) {
        UserVo vo = provider.getUserVo(token);

        Customer c = customerService.findByIdAndEmail(vo.getId(), vo.getEmail())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        return ResponseEntity.ok(CustomerDto.from(c));
    }

    @PostMapping("/balance")
    public ResponseEntity<Integer> changeBalance(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody ChangeBalanceForm form
    ) {
        UserVo vo = provider.getUserVo(token);

        return ResponseEntity.ok(customerBalanceService.changeBalance(vo.getId(), form).getCurrentMoney());
    }
}
