package com.example.bookservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "exchanges")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Exchange {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Login initiator cannot be empty")
  @Column(nullable = false)
  private String initiator;

  @NotBlank(message = "Login recipient cannot be empty")
  @Column(nullable = false)
  private String recipient;

  @NotBlank(message = "Name book by initiator cannot be empty")
  @Column(nullable = false, name = "book_initiator")
  private String bookInitiator;

  @NotBlank(message = "Name book by recipient  cannot be empty")
  @Column(nullable = false, name = "book_recipient")
  private String bookRecipient;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private String status; // wait, cancel, done
}
