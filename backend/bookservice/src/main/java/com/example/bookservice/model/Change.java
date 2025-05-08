// package com.example.bookservice.model;
//
// import jakarta.persistence.*;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;
//
// @Entity
// @Table(name = "books")
// @Setter
// @Getter
// @AllArgsConstructor
// @NoArgsConstructor
// public class Change {
//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  private Long id;
//
//  @Column(nullable = false)
//  private String initiator;
//
//  @Column(nullable = false)
//  private String recipient;
//
//  @Column(nullable = false, name = "book_initiator")
//  private String bookInitiator;
//
//  @Column(nullable = false, name = "book_recipient")
//  private String bookRecipient;
//
//  @Column(nullable = false)
//  private boolean status; // true = change success
// }
