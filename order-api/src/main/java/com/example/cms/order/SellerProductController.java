package com.example.cms.order;

import com.example.cms.order.domain.model.Product;
import com.example.cms.order.domain.product.AddProductForm;
import com.example.cms.order.domain.product.AddProductItemForm;
import com.example.cms.order.domain.product.ProductDto;
import com.example.cms.order.domain.product.ProductItemDto;
import com.example.cms.order.service.ProductItemService;
import com.example.cms.order.service.ProductService;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller/product")
@RequiredArgsConstructor
public class SellerProductController {

    private final ProductService productService;
    private final ProductItemService productItemService;
    private final JwtAuthenticationProvider provider;

    @PostMapping
    public ResponseEntity<ProductDto> addProduct(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                           @RequestBody AddProductForm form) {


        return ResponseEntity.ok(ProductDto.from(
                productService.addProduct(
                        provider.getUserVo(token).getId(), form)
                )
        );
    }

    @PostMapping("/item")
    public ResponseEntity<ProductDto> addProductItem(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                                     @RequestBody AddProductItemForm form) {


        return ResponseEntity.ok(ProductDto.from(
                productItemService.addProductItem(
                                provider.getUserVo(token).getId(), form)
                )
        );
    }
}
