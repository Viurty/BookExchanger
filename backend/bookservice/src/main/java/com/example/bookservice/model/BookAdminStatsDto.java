package com.example.bookservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class BookAdminStatsDto {
  private long countBooks;
  private long countReadyBooks;
  private int percentReady;
}
