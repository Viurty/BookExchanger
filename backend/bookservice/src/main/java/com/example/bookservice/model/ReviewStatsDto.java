package com.example.bookservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ReviewStatsDto {
  private int countReviews;
  private int countRate5;
  private int countRate1;
}
