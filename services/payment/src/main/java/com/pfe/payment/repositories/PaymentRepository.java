package com.pfe.payment.repositories;

import com.pfe.payment.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {}