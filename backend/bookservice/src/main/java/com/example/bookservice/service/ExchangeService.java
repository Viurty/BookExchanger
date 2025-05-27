package com.example.bookservice.service;

import com.example.bookservice.exception.HttpStatusException;
import com.example.bookservice.model.Book;
import com.example.bookservice.model.Exchange;
import com.example.bookservice.model.ExchangeAdminStatsDto;
import com.example.bookservice.repository.BookRepository;
import com.example.bookservice.repository.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ExchangeService {
  private final ExchangeRepository exchangeRepository;
  private final BookRepository bookRepository;

  @Autowired
  public ExchangeService(ExchangeRepository exchangeRepository, BookRepository bookRepository) {
    this.exchangeRepository = exchangeRepository;
    this.bookRepository = bookRepository;
  }

  @Transactional
  public void addExchange(Exchange ex) {
    if (exchangeRepository.existsByInitiatorAndRecipientAndBookInitiatorAndBookRecipientAndStatus(
        ex.getInitiator(),
        ex.getRecipient(),
        ex.getBookInitiator(),
        ex.getBookRecipient(),
        "wait")) {
      throw new HttpStatusException(HttpStatus.CONFLICT, "Данный обмен уже существует!");
    }

    if (!bookRepository.existsByNameIgnoreCase(ex.getBookInitiator())) {
      throw new HttpStatusException(
          HttpStatus.NOT_FOUND, "Книга с названием " + ex.getBookInitiator() + " не существует");
    }
    if (!bookRepository.existsByNameIgnoreCase(ex.getBookRecipient())) {
      throw new HttpStatusException(
          HttpStatus.NOT_FOUND, "Книга с названием " + ex.getBookRecipient() + " не существует");
    }
    if (!bookRepository.findOwnersByBookName(ex.getBookRecipient()).contains(ex.getRecipient())) {
      throw new HttpStatusException(HttpStatus.CONFLICT, "У данного пользователя нет данной книги");
    }
    LocalDateTime now = LocalDateTime.now();
    ex.setCreatedAt(now);
    ex.setStatus("wait");
    try {
      exchangeRepository.save(ex);
    } catch (DataIntegrityViolationException e) {
      throw new HttpStatusException(HttpStatus.CONFLICT, "Не удалось сохранить обмен!");
    }
  }

  @Transactional
  public void deleteExchange(Long id) {
    try {
      exchangeRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      throw new HttpStatusException(HttpStatus.NOT_FOUND, "Обмен с id  " + id + " не найден!");
    }
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

  public ExchangeAdminStatsDto getExchangeStatsForAdmin() {
    long countExchanges = exchangeRepository.count();
    long countSuccesExchanges = exchangeRepository.countByStatus("done");
    int percentSuccess = 0;
    if (countExchanges > 0) {
      percentSuccess = (int) (100 * countSuccesExchanges / countExchanges);
    }
    return new ExchangeAdminStatsDto(countExchanges, countSuccesExchanges, percentSuccess);
  }

  private static final Set<String> allowed_status = Set.of("wait", "cancel", "done");

  @Transactional
  public void changeStatus(Long exchangeId, String newStatus) {
    Exchange exchange =
        exchangeRepository
            .findById(exchangeId)
            .orElseThrow(
                () ->
                    new HttpStatusException(
                        HttpStatus.NOT_FOUND, "Обмен с id=" + exchangeId + " не найден"));

    if (!allowed_status.contains(newStatus)) {
      throw new HttpStatusException(
          HttpStatus.BAD_REQUEST,
          "Неверный статус: " + newStatus + ". Допустимые: " + allowed_status);
    }
    exchange.setStatus(newStatus);
    if (newStatus.equals("done")) {
      String bookNameInitiator = exchange.getBookInitiator();
      String bookNameRecipient = exchange.getBookRecipient();

      Book bookInitiator =
          bookRepository
              .findByNameIgnoreCase(bookNameInitiator)
              .orElseThrow(
                  () ->
                      new HttpStatusException(
                          HttpStatus.NOT_FOUND,
                          "Книга с названием  " + bookNameInitiator + " не найдена!"));
      ;
      Book bookRecipient =
          bookRepository
              .findByNameIgnoreCase(bookNameRecipient)
              .orElseThrow(
                  () ->
                      new HttpStatusException(
                          HttpStatus.NOT_FOUND,
                          "Книга с названием  " + bookNameRecipient + " не найдена!"));
      ;

      List<String> initiatorOwners = bookInitiator.getOwners();
      List<String> recipientOwners = bookRecipient.getOwners();
      if (!initiatorOwners.remove(exchange.getInitiator())) {
        throw new HttpStatusException(
            HttpStatus.NOT_FOUND,
            "Владелец "
                + exchange.getInitiator()
                + " не привязан к книге с названием "
                + bookNameInitiator);
      }
      if (!recipientOwners.remove(exchange.getRecipient())) {
        throw new HttpStatusException(
            HttpStatus.NOT_FOUND,
            "Владелец "
                + exchange.getRecipient()
                + " не привязан к книге с названием "
                + bookNameRecipient);
      }
      if (!initiatorOwners.contains(exchange.getRecipient())) {
        initiatorOwners.add(exchange.getRecipient());
      }
      if (!recipientOwners.contains(exchange.getInitiator())) {
        recipientOwners.add(exchange.getInitiator());
      }
      bookRepository.save(bookInitiator);
      bookRepository.save(bookRecipient);
    }
  }
}
