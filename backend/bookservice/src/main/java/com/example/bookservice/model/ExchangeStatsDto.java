package com.example.bookservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ExchangeStatsDto {
  private int countExchanges;
  private int countSuccessExchanges;
  private int percentSuccess;
}
