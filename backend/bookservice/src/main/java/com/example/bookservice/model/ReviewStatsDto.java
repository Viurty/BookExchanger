package com.example.bookservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ReviewStatsDto {
  private int cntReviews;
  private int cntRate5;
  private int cntRate1;
}
