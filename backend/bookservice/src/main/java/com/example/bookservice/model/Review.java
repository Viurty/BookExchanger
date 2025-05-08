// package com.example.bookservice.model;
//
// import jakarta.persistence.*;
// import jakarta.validation.constraints.*;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;
//
// import java.time.LocalDateTime;
//
// @Entity
// @Table(name = "reviews")
// @Setter
// @Getter
// @AllArgsConstructor
// @NoArgsConstructor
// public class Review {
//
//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  private Long id;
//
//  @Column(nullable = false)
//  private String author;
//
//  @Column(nullable = false, name = "book_name")
//  private String bookName;
//
//  @Column(nullable = false)
//  @Min(value = 1, message = "Rating must be at least 1")
//  @Max(value = 5, message = "Rating cannot be more than 5")
//  private int rating;
//
//  @Column() private String comment;
//
//  @Column(name = "created_at")
//  private LocalDateTime createdAt;
// }
