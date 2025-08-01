package com.example.cms.user.application;

import com.example.cms.user.client.MailgunClient;
import com.example.cms.user.client.mailgun.SendMailForm;
import com.example.cms.user.domain.SignUpForm;
import com.example.cms.user.domain.model.Customer;
import com.example.cms.user.domain.model.Seller;
import com.example.cms.user.exception.CustomException;
import com.example.cms.user.service.customer.SignUpCustomerService;
import com.example.cms.user.service.seller.SellerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.cms.user.exception.ErrorCode.ALREADY_REGISTER_USER;

@Service
@Slf4j
@RequiredArgsConstructor
public class SignUpApplication {
    private final MailgunClient mailgunClient;
    private final SignUpCustomerService signUpCustomerService;
    private final SellerService sellerService;

    public void customerVerify(String email, String code) {
        signUpCustomerService.verifyEmail(email, code);
    }

    public String customerSignUp(SignUpForm form) {
        if (signUpCustomerService.isEmailExist(form.getEmail())) {
            //Exception
            throw new CustomException(ALREADY_REGISTER_USER);
        } else {
            // 회원가입
            Customer c = signUpCustomerService.signUp(form);
            LocalDateTime now = LocalDateTime.now();

            String code = getRandomCode();

            SendMailForm sendMailForm = SendMailForm.builder()
                    .from("tester@dannymytester.com")
                    .to(form.getEmail())
                    .subject("Verification Email")
                    .text(getVerificationEmailBody(c.getEmail(), c.getName(),"customer", code))
                    .build();
            log.info("Send mail result : " + mailgunClient.sendEmail(sendMailForm).getBody());

            signUpCustomerService.changeCustomerValidateEmail(c.getId(), code);
            return "회원 가입에 성공하였습니다.";
        }
    }


    public void sellerVerify(String email, String code) {
        sellerService.verifyEmail(email, code);
    }

    public String sellerSignUp(SignUpForm form) {
        if (sellerService.isEmailExist(form.getEmail())) {
            //Exception
            throw new CustomException(ALREADY_REGISTER_USER);
        } else {
            // 회원가입
            Seller s = sellerService.signUp(form);
            LocalDateTime now = LocalDateTime.now();

            String code = getRandomCode();

            SendMailForm sendMailForm = SendMailForm.builder()
                    .from("tester@dannymytester.com")
                    .to(form.getEmail())
                    .subject("Verification Email")
                    .text(getVerificationEmailBody(s.getEmail(), s.getName(),"seller", code))
                    .build();
            log.info("Send mail result : " + mailgunClient.sendEmail(sendMailForm).getBody());

            sellerService.changeSellerValidateEmail(s.getId(), code);
            return "회원 가입에 성공하였습니다.";
        }
    }

    private String getRandomCode() {
        return RandomStringUtils.random(10, true, true);
    }

    private String getVerificationEmailBody(String email, String name,String type, String code) {
        StringBuilder builder = new StringBuilder();
        return builder.append("Hello ")
                .append(name)
                .append("! Please Click Link for verification\n\n")
                .append("http://localhost:8081/signup/")
                .append(type)
                .append("/verify/?email=")
                .append(email)
                .append("&code=")
                .append(code)
                .toString();
    }


}
