package com.example.bookservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ExchangeAdminStatsDto {
  private long countExchanges;
  private long countSuccessExchanges;
  private int percentSuccess;
}
