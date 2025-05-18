package com.example.bookservice.controller;

import com.example.bookservice.model.Book;
import com.example.bookservice.model.BookAdminStatsDto;
import com.example.bookservice.model.BookDto;
import com.example.bookservice.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "http://localhost:5173")
public class BookController {
  private final BookService bookService;

  @Autowired
  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @PostMapping()
  public ResponseEntity<String> createBook(@Valid @RequestBody BookDto bookDto) {
    bookService.addBookOnSite(bookDto);
    return ResponseEntity.status(HttpStatus.CREATED).body("Книга добавлена на сайт!");
  }

  @PostMapping("/append")
  public ResponseEntity<String> addBookForUser(@RequestBody BookDto bookDto) {
    bookService.addBookOnProfile(bookDto);
    return ResponseEntity.status(HttpStatus.CREATED).body("Книга добавлена на ваш аккаунт!");
  }

  @GetMapping("/top")
  public List<Book> getBooksByLogin() {
    return bookService.getBooksByReady();
  }

  @GetMapping("/{bookId}")
  public Book getBooksByOwner(@PathVariable Long bookId) {
    return bookService.getBooksById(bookId);
  }

  @GetMapping("/user/{login}")
  public List<Book> getBooksByOwner(@PathVariable String login) {
    return bookService.getBooksByOwner(login);
  }

  @GetMapping("/owners/{bookId}")
  public List<String> getOwnersByBook(@PathVariable Long bookId) {
    return bookService.getOwnersLoginById(bookId);
  }

  @GetMapping("/stats")
  public BookAdminStatsDto getBookAdminStats() {
    return bookService.getBookStatsForAdmin();
  }

  @PutMapping("/delete")
  public ResponseEntity<String> deleteBooks(@RequestBody BookDto bookDto) {
    bookService.deleteOwner(bookDto);
    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .body("Книга была удалена с вашего аккаунта!");
  }
}
