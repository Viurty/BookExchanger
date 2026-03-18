package com.example.bookservice.repository;

import com.example.bookservice.model.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
  List<Exchange> findByInitiator(String initiator);

  List<Exchange> findByRecipient(String recipient);

  long countByStatus(String status);

  boolean existsByInitiatorAndRecipientAndBookInitiatorAndBookRecipientAndStatus(
      String initiator,
      String recipient,
      String bookInitiator,
      String bookRecipient,
      String Status);
}
