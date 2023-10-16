package br.com.danielbrai.ssmdemo.services;

import br.com.danielbrai.ssmdemo.domain.Payment;
import br.com.danielbrai.ssmdemo.domain.PaymentEvent;
import br.com.danielbrai.ssmdemo.domain.PaymentState;
import br.com.danielbrai.ssmdemo.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    public static final String PAYMENT_ID = "payment_id";
    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
    private final PaymentStateInterceptor paymentStateInterceptor;

    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);
        return paymentRepository.save(payment);
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuthorize(Long id) {
        StateMachine<PaymentState, PaymentEvent> sm = this.build(id);
        this.sendEvent(id, sm, PaymentEvent.PRE_AUTH_APPROVED);
        return sm;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> authorize(Long id) {
        StateMachine<PaymentState, PaymentEvent> sm = this.build(id);
        this.sendEvent(id, sm, PaymentEvent.AUTHORIZE_APPROVED);
        return sm;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> decline(Long id) {
        StateMachine<PaymentState, PaymentEvent> sm = this.build(id);
        this.sendEvent(id, sm, PaymentEvent.AUTHORIZE_DECLINED);
        return sm;
    }

    private void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> stateMachine, PaymentEvent event) {
        Message<PaymentEvent> msg = MessageBuilder.withPayload(event)
                .setHeader(PAYMENT_ID, paymentId)
                .build();
        stateMachine.sendEvent(msg);
    }

    private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);
        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            StateMachine<PaymentState, PaymentEvent> stateMachine = stateMachineFactory.getStateMachine(Long.toString(payment.getId()));
            stateMachine.stop();
            stateMachine.getStateMachineAccessor().doWithAllRegions(
                    sma -> {
                        sma.addStateMachineInterceptor(this.paymentStateInterceptor);
                        sma.resetStateMachine(new DefaultStateMachineContext<>(
                                payment.getState(),
                                null,
                                null,
                                null)
                        );
                    }
            );
            stateMachine.start();
            return stateMachine;
        }
        return null;
    }
}
