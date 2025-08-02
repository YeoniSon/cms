package com.example.cms.order.controller;

import com.example.cms.order.application.CartApplication;
import com.example.cms.order.application.OrderApplication;
import com.example.cms.order.domain.product.AddProductCartForm;
import com.example.cms.order.domain.redis.Cart;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/cart")
@RequiredArgsConstructor
public class CustomerCartController {
    // 임시코드
    private final CartApplication cartApplication;
    private final OrderApplication orderApplication;
    private final JwtAuthenticationProvider provider;

    // 장바구니 추가
    @PostMapping
    public ResponseEntity<Cart> addCart(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody AddProductCartForm form
    ) {
        return ResponseEntity.ok(
                cartApplication.addCart(provider.getUserVo(token).getId(), form)
        );
    }

    // 장바구니 조회
    @GetMapping
    public ResponseEntity<Cart> showCart(
            @RequestHeader(name = "X-AUTH-TOKEN") String token
    ) {
        return ResponseEntity.ok(
                cartApplication.getCart(provider.getUserVo(token).getId())
        );
    }

    // 장바구리 변경
    @PutMapping
    public ResponseEntity<Cart> updateCart(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody Cart cart
    ) {
        return ResponseEntity.ok(
                cartApplication.updateCart(provider.getUserVo(token).getId(), cart)
        );
    }

    // 장바구니 구매
    @PostMapping("/order")
    public ResponseEntity<Cart> order(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody Cart cart
    ){
        orderApplication.order(token, cart);
        return ResponseEntity.ok().build();
    }

}
