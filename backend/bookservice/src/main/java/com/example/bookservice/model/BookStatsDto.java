package com.example.bookservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class BookStatsDto {
  private int countBooks;
  private int countReadyBooks;
  private int percentReady;
}
