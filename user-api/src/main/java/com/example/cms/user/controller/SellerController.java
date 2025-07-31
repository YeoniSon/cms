package com.example.cms.user.controller;

import com.example.cms.user.domain.model.Seller;
import com.example.cms.user.domain.seller.SellerDto;
import com.example.cms.user.exception.CustomException;
import com.example.cms.user.exception.ErrorCode;
import com.example.cms.user.service.seller.SellerService;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import com.zerobase.domain.domain.common.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.cms.user.exception.ErrorCode.NOT_FOUND_USER;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    private final JwtAuthenticationProvider provider;

    @GetMapping("/getInfo")
    public ResponseEntity<SellerDto> getInfo(@RequestHeader(name = "X-AUTH-TOKEN") String token) {
        UserVo vo = provider.getUserVo(token);

        Seller s = sellerService.findByIdAndEmail(vo.getId(), vo.getEmail())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        return ResponseEntity.ok(SellerDto.from(s));
    }

}
