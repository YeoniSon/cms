package com.example.cms.order.application;

import com.example.cms.order.domain.model.Product;
import com.example.cms.order.domain.model.ProductItem;
import com.example.cms.order.domain.product.AddProductCartForm;
import com.example.cms.order.domain.redis.Cart;
import com.example.cms.order.exception.CustomException;
import com.example.cms.order.service.CartService;
import com.example.cms.order.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

import static com.example.cms.order.exception.ErrorCode.ITEM_COUNT_NOT_ENOUGH;
import static com.example.cms.order.exception.ErrorCode.NOT_FOUND_PRODUCT;

@Service
@RequiredArgsConstructor
public class CartApplication {
    private final ProductSearchService productSearchService;
    private final CartService cartService;

    public Cart addCart(Long customerId, AddProductCartForm form) {
        Product product = productSearchService.getByProductId(form.getId());
        if (product == null) {
            throw new CustomException(NOT_FOUND_PRODUCT);
        }
        Cart cart = cartService.getCart(customerId);

        if (cart != null && !addAble(cart, product, form)) {
            throw new CustomException(ITEM_COUNT_NOT_ENOUGH);
        }

        return cartService.addCart(customerId, form);
    }

    private boolean addAble(Cart cart, Product product, AddProductCartForm form) {
        Cart.Product cartProduct = cart.getProducts()
                .stream()
                .filter(p -> p.getId().equals(form.getId()))
                .findFirst()
                .orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

        //검색 효율을 위해
        Map<Long, Integer> cartItemCountMap = cartProduct.getItems()
                .stream()
                .collect(Collectors.toMap(Cart.ProductItem::getId,
                        Cart.ProductItem::getCount));

        Map<Long, Integer> currentItemCountMap = product.getProductItems()
                .stream()
                .collect(Collectors.toMap(ProductItem::getId,
                        ProductItem::getCount));

        return form.getItems().stream()
                .noneMatch(
                        formItem -> {
                            Integer cartCount = cartItemCountMap.get(cartProduct.getId());
                            Integer currentCount = currentItemCountMap.get(formItem.getId());
                            return formItem.getCount() + cartCount > currentCount;
                        });
    }
}
