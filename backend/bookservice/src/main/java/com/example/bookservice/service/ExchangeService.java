package com.example.bookservice.service;

import com.example.bookservice.exception.HttpStatusException;
import com.example.bookservice.model.Exchange;
import com.example.bookservice.model.ExchangeStatsDto;
import com.example.bookservice.repository.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExchangeService {
  private final ExchangeRepository exchangeRepository;

  @Autowired
  public ExchangeService(ExchangeRepository exchangeRepository) {
    this.exchangeRepository = exchangeRepository;
  }

  public List<Exchange> getExchangesByInitiator(String login) {
    return exchangeRepository.findByInitiator(login);
  }

  public List<Exchange> getExchangesByRecipient(String login) {
    return exchangeRepository.findByRecipient(login);
  }

  public List<Exchange> getExchangesByDescCreatedAt() {
    return exchangeRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
  }

  private int getCountExchanges() {
    List<Exchange> exchanges = exchangeRepository.findAll();
    return exchanges.size();
  }

  private int getCountSuccessExchanges() {
    List<Exchange> exchanges = exchangeRepository.findByStatus(true);
    return exchanges.size();
  }

  public ExchangeStatsDto getStatsByExchange() {
    int countExchanges = getCountExchanges();
    int countSuccesExchanges = getCountSuccessExchanges();
    int percentSuccess = 0;
    if (countExchanges > 0) {
      percentSuccess = 100 * countSuccesExchanges / countExchanges;
    }
    return new ExchangeStatsDto(countExchanges, countSuccesExchanges, percentSuccess);
  }
}
