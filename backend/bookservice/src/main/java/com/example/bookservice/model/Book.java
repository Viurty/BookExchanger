package com.example.bookservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "books")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ElementCollection
  @CollectionTable(name = "book_owners", joinColumns = @JoinColumn(name = "book_id"))
  @Column(name = "owner")
  private List<String> owners;

  @Column(nullable = false, length = 100, unique = true)
  @NotBlank(message = "Name cannot be empty")
  @Size(max = 100, message = "Name must not exceed 100 characters")
  private String name;

  @Column(nullable = false, length = 100)
  @NotBlank(message = "Author cannot be empty")
  @Size(max = 100, message = "Author must not exceed 100 characters")
  private String author;

  @Column(nullable = false, length = 100)
  @NotBlank(message = "Genre cannot be empty")
  @Size(max = 100, message = "Genre must not exceed 100 characters")
  private String genre;

  @Column(nullable = false, length = 1000)
  @NotBlank(message = "Description cannot be empty or invalid")
  @Size(max = 1000, message = "Description must not exceed 1000 characters")
  private String description;
}
