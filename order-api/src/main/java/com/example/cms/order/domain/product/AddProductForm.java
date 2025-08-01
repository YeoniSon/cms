package com.example.cms.order.domain.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProductForm {

    private Long productId;
    private String name;
    private String description;
    private List<AddProductItemForm> items;
}
