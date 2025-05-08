// package com.example.bookservice.model;
//
// import jakarta.persistence.*;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Size;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;
//
// import java.util.Map;
//
// @Entity
// @Table(name = "books")
// @Getter
// @Setter
// @AllArgsConstructor
// @NoArgsConstructor
// public class Book {
//
//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  private Long id;
//
//  @ElementCollection
//  @CollectionTable(name = "book_owners", joinColumns = @JoinColumn(name = "book_id"))
//  @MapKeyColumn(name = "owner_login")
//  @Column(name = "is_ready")
//  private Map<String, Boolean> owners;
//
//  @Column(nullable = false, length = 100)
//  @NotBlank(message = "Name cannot be empty")
//  @Size(max = 100, message = "Name must not exceed 100 characters")
//  private String name;
//
//  @Column(nullable = false)
//  private String author;
//
//  @Column(nullable = false, length = 100)
//  @NotBlank(message = "Genre cannot be empty")
//  @Size(max = 100, message = "Genre must not exceed 100 characters")
//  private String genre;
//
//  @Column(nullable = false)
//  @NotBlank(message = "Description cannot be empty or invalid")
//  private String description;
// }
