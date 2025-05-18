package com.example.bookservice.controller;

import com.example.bookservice.model.Exchange;
import com.example.bookservice.model.ExchangeAdminStatsDto;
import com.example.bookservice.service.ExchangeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exchanges")
@CrossOrigin(origins = "http://localhost:5173")
public class ExchangeController {
  private final ExchangeService exchangeService;

  @Autowired
  public ExchangeController(ExchangeService exchangeService) {
    this.exchangeService = exchangeService;
  }

  @PostMapping()
  public ResponseEntity<String> createExchange(@Valid @RequestBody Exchange exchange) {
    exchangeService.addExchange(exchange);
    return ResponseEntity.status(HttpStatus.CREATED).body("Обмен успешно создан!");
  }

  @GetMapping("/initiator/{login}")
  public List<Exchange> getExchangesByInitiator(@PathVariable String login) {
    return exchangeService.getExchangesByInitiator(login);
  }

  @GetMapping("/recipient/{login}")
  public List<Exchange> getExchangesByBook(@PathVariable String login) {
    return exchangeService.getExchangesByRecipient(login);
  }

  @GetMapping("/last")
  public List<Exchange> getLastExchanges() {
    return exchangeService.getExchangesByDescCreatedAt();
  }

  @GetMapping("/stats")
  public ExchangeAdminStatsDto getExchangeAdminStats() {
    return exchangeService.getExchangeStatsForAdmin();
  }

  @DeleteMapping("/{exchange_id}")
  public ResponseEntity<String> cancelExchange(@PathVariable Long exchange_id) {
    exchangeService.deleteExchange(exchange_id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Обмен успешно отменен!");
  }

  @PatchMapping("/{exchange_id}/status")
  public void updateStatus(@PathVariable Long exchange_id, @RequestParam String status) {
    exchangeService.changeStatus(exchange_id, status);
  }
}
