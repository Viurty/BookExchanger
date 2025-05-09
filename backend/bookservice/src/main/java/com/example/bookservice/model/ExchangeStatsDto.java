package com.example.bookservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ExchangeStatsDto {
  private int cntExchanges;
  private int cntSuccessExchanges;
  private int percentSuccess;
}
