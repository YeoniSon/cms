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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

        if (!addAble(cart, product, form)) {
            throw new CustomException(ITEM_COUNT_NOT_ENOUGH);
        }

        return cartService.addCart(customerId, form);
    }

    /**
     * 엣지 케이스
     *
     * @param customerId
     * @param cart
     * @return
     */
    public Cart updateCart(Long customerId, Cart cart){
        // 실질적으로 변하는 데이터
        // 상품의 삭제, 수량 변경
        cartService.putCart(customerId, cart);
        return getCart(customerId);
    }

    // 1. 장바구니에 상품을 추가했다.
    // 2. 상품의 가격이나 수량이 변동 된다.
    public Cart getCart(Long customerId) {
        Cart cart = refreshCart(cartService.getCart(customerId));
        cartService.putCart(cart.getCustomerId(), cart);
        Cart returnCart = new Cart();
        returnCart.setCustomerId(customerId);
        returnCart.setProducts(cart.getProducts());
        returnCart.setMessages(cart.getMessages());
        cart.setMessages(new ArrayList<>());
        //메세지 없는 것
        cartService.putCart(customerId, cart);
        return returnCart;

        // 2. 메세지를 보고 난 다음에는, 이미 본 메세지는 스팸이 되기 때문에 제거한다.
    }

    public void clearCart(Long customerId) {
        cartService.putCart(customerId, null);
    }

    protected Cart refreshCart(Cart cart) {
        // 1. 상품이나 상품의 아이템의 정보, 가격, 수량이 변경되었는지 체크하고
        // 그에 맞는 알람을 제공해준다.
        // 2. 상품의 수량, 가격을 우리가 임의로 변경한다.

        Map<Long, Product> productMap = productSearchService.getListByProductIds(
                cart.getProducts()
                        .stream()
                        .map(Cart.Product::getId)
                        .collect(Collectors.toList())
        ).stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        for (int i = 0; i <cart.getProducts().size(); i++) {

            Cart.Product cartProduct = cart.getProducts().get(i);

            Product p = productMap.get(cartProduct.getId());

            if (p == null) {
                cart.getProducts().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + " 상품이 삭제되었습니다.");
                continue;
            }

            Map<Long, ProductItem> productItemMap = p.getProductItems().stream()
                    .collect(Collectors.toMap(ProductItem::getId, productItem -> productItem));

            // 각각 케이스 별로 에러를 쪼개고, 에러가 정상 출력 되야 하는지 체크해야하 한다.

            // 아이템 1, 2
            List<String> tmpMessages = new ArrayList<>();
            for (int j = 0; j < cartProduct.getItems().size(); j++) {

                Cart.ProductItem cartProductItem = cartProduct.getItems().get(j);

                ProductItem pi = productItemMap.get(cartProductItem.getId());

                if (pi == null) {
                    cartProduct.getItems().remove(cartProductItem);
                    j--;
                   tmpMessages.add(cartProductItem.getName() + " 옵션이 삭제되었습니다.");
                    continue;
                }

                boolean isPriceChanged = false, isCountNotEnough = false;
                if (!cartProductItem.getPrice()
                        .equals(productItemMap.get(cartProductItem.getId())
                                .getPrice())
                ) {
                    isPriceChanged = true;
                    cartProductItem.setPrice(pi.getPrice());
                }
                if (cartProductItem.getCount() >pi.getCount()
                ) {
                    isCountNotEnough = true;
                    cartProductItem.setCount(pi.getCount());
                }
                if (isPriceChanged && isCountNotEnough) {
                    // message 1
                    tmpMessages.add(cartProductItem.getName() + " 가격변동, 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
                } else if (isPriceChanged) {
                    // message2
                    tmpMessages.add(cartProductItem.getName() + " 가격이 변동되었습니다.");
                } else if (isCountNotEnough) {
                    // message 3
                    tmpMessages.add(cartProductItem.getName() + " 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
                }
            }
            if (cartProduct.getItems().isEmpty()) {
                cart.getProducts().remove(cartProduct);
                i--;
                tmpMessages.add(cartProduct.getName() + " 상품의 옵션이 모두 없어져 구매가 불가능합니다.");
                continue;
            } else if (!tmpMessages.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                builder.append(cartProduct.getName() + " 상품의 변동 사항 : ");
                for (String message : tmpMessages) {
                    builder.append(message);
                    builder.append(", ");
                }
                cart.addMessage(builder.toString());
            }
        }

        return cart;
    }

    private boolean addAble(Cart cart, Product product, AddProductCartForm form) {
        Cart.Product cartProduct = cart.getProducts()
                .stream()
                .filter(p -> p.getId().equals(form.getId()))
                .findFirst()
                .orElse(Cart.Product.builder().id(product.getId())
                        .items(Collections.emptyList()).build());

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
                            if (cartCount == null) {
                                cartCount = 0;
                            }
                            Integer currentCount = currentItemCountMap.get(formItem.getId());
                            return formItem.getCount() + cartCount > currentCount;
                        });
    }
}
