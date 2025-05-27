package com.example.bookservice.controller;

import com.example.bookservice.model.Exchange;
import com.example.bookservice.model.ExchangeAdminStatsDto;
import com.example.bookservice.service.ExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exchanges")
@CrossOrigin(origins = "${frontend.address:http://localhost:5173}")
@Tag(name = "Exchanges", description = "API для управления обменами")
public class ExchangeController {

  private final ExchangeService exchangeService;

  @Autowired
  public ExchangeController(ExchangeService exchangeService) {
    this.exchangeService = exchangeService;
  }

  @Operation(summary = "Создать новый обмен")
  @PostMapping
  public ResponseEntity<String> createExchange(@Valid @RequestBody Exchange exchange) {
    exchangeService.addExchange(exchange);
    return ResponseEntity.status(HttpStatus.CREATED).body("Обмен успешно создан!");
  }

  @Operation(summary = "Получить обмены по логину инициатора")
  @GetMapping("/initiator/{login}")
  public List<Exchange> getExchangesByInitiator(@PathVariable String login) {
    return exchangeService.getExchangesByInitiator(login);
  }

  @Operation(summary = "Получить обмены по логину получателя")
  @GetMapping("/recipient/{login}")
  public List<Exchange> getExchangesByRecipient(@PathVariable String login) {
    return exchangeService.getExchangesByRecipient(login);
  }

  @Operation(summary = "Получить последние обмены")
  @GetMapping("/last")
  public List<Exchange> getLastExchanges() {
    return exchangeService.getExchangesByDescCreatedAt();
  }

  @Operation(summary = "Получить статистику обменов для админа")
  @GetMapping("/stats")
  public ExchangeAdminStatsDto getExchangeAdminStats() {
    return exchangeService.getExchangeStatsForAdmin();
  }

  @Operation(summary = "Отменить обмен по ID")
  @DeleteMapping("/{exchange_id}")
  public ResponseEntity<String> cancelExchange(@PathVariable Long exchange_id) {
    exchangeService.deleteExchange(exchange_id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Обмен успешно отменен!");
  }

  @Operation(summary = "Обновить статус обмена")
  @PatchMapping("/{exchange_id}/status")
  public void updateStatus(@PathVariable Long exchange_id, @RequestParam String status) {
    exchangeService.changeStatus(exchange_id, status);
  }
}
