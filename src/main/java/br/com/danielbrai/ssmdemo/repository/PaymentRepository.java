package br.com.danielbrai.ssmdemo.repository;

import br.com.danielbrai.ssmdemo.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
