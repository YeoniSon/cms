package com.example.cms.order.domain.product;

import com.example.cms.order.domain.model.Product;
import com.example.cms.order.domain.model.ProductItem;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private List<ProductItemDto> items;

    public static ProductDto from(Product product) {
        List<ProductItemDto> items = product.getProductItems()
                .stream()
                .map(ProductItemDto::from)
                .collect(Collectors.toList());

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .items(items)
                .build();
    }

    public static ProductDto withoutItemsfrom(Product product) {
//        List<ProductItemDto> items = product.getProductItems()
//                .stream()
//                .map(ProductItemDto::from)
//                .collect(Collectors.toList());

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
//                .items(items)
                .build();
    }
}
