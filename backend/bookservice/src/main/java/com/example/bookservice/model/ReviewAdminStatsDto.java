package com.example.bookservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ReviewAdminStatsDto {
  private long countReviews;
  private long countRate5;
  private long countRate1;
}
