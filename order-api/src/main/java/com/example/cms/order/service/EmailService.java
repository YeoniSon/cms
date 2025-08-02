package com.example.cms.order.service;

import com.example.cms.order.client.UserClient;
import com.example.cms.order.client.user.CustomerDto;
import com.example.cms.order.client.user.SendMailForm;
import com.example.cms.order.domain.redis.Cart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final UserClient userClient;

    // 결제 내역 이메일 발송
    public void sendOrderConfirmationEmail(CustomerDto customer, Cart cart, int totalPrice) {
        try{
            String emailBody = createOrderEmailBody(customer, cart, totalPrice);

            SendMailForm sendMailForm = SendMailForm.builder()
                    .from("tester@dannymytester.com")
                    .to(customer.getEmail())
                    .subject("주문 확인 메일 ")
                    .text(emailBody)
                    .build();

            String response = userClient.sendOrderEmail(sendMailForm).getBody();
            log.info("Order confirmation email sent to {}: {}", customer.getEmail(), response);
        } catch (Exception e) {
            log.error("Failed to send order confirmation email to {}: {}", customer.getEmail(), e.getMessage());
        }
    }

    // 메일 내용
    private String createOrderEmailBody(CustomerDto customer, Cart cart, int totalPrice) {
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("안녕하세요, ").append(customer.getName()).append("님!\n\n");
        emailBody.append("주문이 성공적으로 완료되었습니다.\n\n");
        emailBody.append("=== 주문 내역 ===\n");

        for (Cart.Product product : cart.getProducts()) {
            emailBody.append("\n상품: ").append(product.getName()).append("\n");
            emailBody.append("설명: ").append(product.getDescription()).append("\n");

            for (Cart.ProductItem item : product.getItems()) {
                emailBody.append("  - ").append(item.getName())
                        .append(" x ").append(item.getCount())
                        .append(" = ").append(String.format("%,d원", item.getPrice() * item.getCount()))
                        .append("\n");
            }
        }

        emailBody.append("\n=== 총 결제 금액 ===\n");
        emailBody.append(String.format("%,d원", totalPrice)).append("\n\n");
        emailBody.append("주문 시간: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
        emailBody.append("감사합니다!");

        return emailBody.toString();
    }
}
