package br.com.danielbrai.ssmdemo.services;

import br.com.danielbrai.ssmdemo.domain.Payment;
import br.com.danielbrai.ssmdemo.domain.PaymentEvent;
import br.com.danielbrai.ssmdemo.domain.PaymentState;
import br.com.danielbrai.ssmdemo.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class PaymentStateInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {

    private final PaymentRepository paymentRepository;

    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message,
                               Transition<PaymentState, PaymentEvent> transition, StateMachine<PaymentState,
            PaymentEvent> stateMachine, StateMachine<PaymentState, PaymentEvent> rootStateMachine) {


        Optional.ofNullable(message).ifPresent(msg -> {
            Long paymentId = Long.class.cast(msg.getHeaders().getOrDefault(PaymentServiceImpl.PAYMENT_ID, -1L));
            Optional.ofNullable(paymentId).ifPresent(id -> {
                Optional<Payment> paymentOptional = paymentRepository.findById(id);
                paymentOptional.ifPresent(payment -> {
                    payment.setState(state.getId());
                    paymentRepository.save(payment);
                });
            });
        });

    }
}
