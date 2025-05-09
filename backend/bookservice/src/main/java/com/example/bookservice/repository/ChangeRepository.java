package com.example.bookservice.repository;

import com.example.bookservice.model.Change;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangeRepository extends JpaRepository<Change, Long> {
  List<Change> findByInitiator(String initiator);

  List<Change> findByRecipient(String recipient);
}
