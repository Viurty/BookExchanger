package com.example.bookservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
  @NotBlank(message = "Login cannot be empty")
  private String owner;

  @NotBlank(message = "Name cannot be empty")
  @Size(max = 100, message = "Name must not exceed 100 characters")
  private String name;

  @NotBlank(message = "Author cannot be empty")
  @Size(max = 100, message = "Author must not exceed 100 characters")
  private String author;

  @NotBlank(message = "Genre cannot be empty")
  @Size(max = 100, message = "Genre must not exceed 100 characters")
  private String genre;

  @NotBlank(message = "Description cannot be empty or invalid")
  @Size(max = 1000, message = "Description must not exceed 1000 characters")
  private String description;
}
