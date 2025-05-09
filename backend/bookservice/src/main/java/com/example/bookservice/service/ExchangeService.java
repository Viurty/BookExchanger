package com.example.bookservice.service;

import com.example.bookservice.exception.HttpStatusException;
import com.example.bookservice.model.Exchange;
import com.example.bookservice.repository.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.util.List;

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
    List<Exchange> changes = exchangeRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    if (changes.size() < 5) {
      throw new HttpStatusException(HttpStatus.NO_CONTENT, "Недостаточно данных!");
    }
    return changes;
  }

  private int getCountExchanges() {
    List<Exchange> exchanges = exchangeRepository.findAll();
    return exchanges.size();
  }

  private int getCountSuccesExchanges() {
    List<Exchange> exchanges = exchangeRepository.findByStatus(true);
    return exchanges.size();
  }

  public String getStatisticByExchange() {
    int cntChange = getCountExchanges();
    int cntSucChange = getCountSuccesExchanges();
    double percent = 0;
    try {
      percent = 100 * cntChange / cntSucChange;
    } catch (Exception e) {
      percent = 0;
    }
    String res =
        String.format(
            "Всего созданных обменов: %d, Из них успешных: %d, Процент успешных сделок: %d%",
            cntChange, cntSucChange, percent);
    return res;
  }
}
