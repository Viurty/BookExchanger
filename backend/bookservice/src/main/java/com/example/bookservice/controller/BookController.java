package com.example.bookservice.controller;

import com.example.bookservice.model.Book;
import com.example.bookservice.model.BookAdminStatsDto;
import com.example.bookservice.model.BookDto;
import com.example.bookservice.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "${frontend.address:http://localhost:5173}")
@Tag(name = "Books", description = "API для управления книгами")
public class BookController {
  private final BookService bookService;

  @Autowired
  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @Operation(summary = "Добавить книгу на сайт")
  @PostMapping
  public ResponseEntity<String> createBook(@Valid @RequestBody BookDto bookDto) {
    bookService.addBookOnSite(bookDto);
    return ResponseEntity.status(HttpStatus.CREATED).body("Книга добавлена на сайт!");
  }

  @Operation(summary = "Добавить книгу в профиль пользователя")
  @PostMapping("/append")
  public ResponseEntity<String> addBookForUser(@RequestBody BookDto bookDto) {
    bookService.addBookOnProfile(bookDto);
    return ResponseEntity.status(HttpStatus.CREATED).body("Книга добавлена на ваш аккаунт!");
  }

  @Operation(summary = "Получить все книги, готовые к обмену")
  @GetMapping("/top")
  public List<Book> getBooksByLogin() {
    return bookService.getBooksByReady();
  }

  @Operation(summary = "Получить книгу по ID")
  @GetMapping("/{bookId}")
  public Book getBooksByOwner(@PathVariable Long bookId) {
    return bookService.getBooksById(bookId);
  }

  @Operation(summary = "Получить все книги пользователя")
  @GetMapping("/user/{login}")
  public List<Book> getBooksByOwner(@PathVariable String login) {
    return bookService.getBooksByOwner(login);
  }

  @Operation(summary = "Получить логины владельцев книги")
  @GetMapping("/owners/{bookId}")
  public List<String> getOwnersByBook(@PathVariable Long bookId) {
    return bookService.getOwnersLoginById(bookId);
  }

  @Operation(summary = "Статистика по книгам для админа")
  @GetMapping("/stats")
  public BookAdminStatsDto getBookAdminStats() {
    return bookService.getBookStatsForAdmin();
  }

  @Operation(summary = "Удалить книгу из профиля пользователя")
  @PutMapping("/delete")
  public ResponseEntity<String> deleteBooks(@RequestBody BookDto bookDto) {
    bookService.deleteOwner(bookDto);
    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .body("Книга была удалена с вашего аккаунта!");
  }
}
