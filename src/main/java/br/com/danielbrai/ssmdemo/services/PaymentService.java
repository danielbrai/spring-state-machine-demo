package br.com.danielbrai.ssmdemo.services;

import br.com.danielbrai.ssmdemo.domain.Payment;
import br.com.danielbrai.ssmdemo.domain.PaymentEvent;
import br.com.danielbrai.ssmdemo.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {

    Payment newPayment(Payment payment);

    StateMachine<PaymentState, PaymentEvent> preAuthorize(Long id);

    StateMachine<PaymentState, PaymentEvent> authorize(Long id);

    StateMachine<PaymentState, PaymentEvent> decline(Long id);
}
