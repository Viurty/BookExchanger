package com.example.bookservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ReviewBookStatsDto {
  private double avgRating;
  private int countRating;
}
