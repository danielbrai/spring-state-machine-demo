package br.com.danielbrai.ssmdemo.services;

import br.com.danielbrai.ssmdemo.domain.Payment;
import br.com.danielbrai.ssmdemo.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder().amount(BigDecimal.valueOf(12.99)).build();
    }

    @Test
    void preAuthorize() {
        Payment savedPayment = this.paymentService.newPayment(payment);
        System.out.println(savedPayment);

        this.paymentService.preAuthorize(savedPayment.getId());
        Payment preAuthPayment = this.paymentRepository.findById(savedPayment.getId()).get();
        System.out.println(preAuthPayment);
    }
}