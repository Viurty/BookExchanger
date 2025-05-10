package com.example.bookservice.controller;

import com.example.bookservice.model.Book;
import com.example.bookservice.model.BookAdminStatsDto;
import com.example.bookservice.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
  private final BookService bookService;

  @Autowired
  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @PostMapping()
  public ResponseEntity<String> createBook(@Valid @RequestBody Book Book) {
    bookService.addBook(Book);
    return ResponseEntity.status(HttpStatus.CREATED).body("Книга добавлена на ваш аккаунт!");
  }

  @GetMapping("/top")
  public List<Book> getBooksByLogin() {
    return bookService.getBooksByReady();
  }

  @GetMapping("/user/{login}")
  public List<Book> getBooksByBook(@PathVariable String login) {
    return bookService.getBooksByOwner(login);
  }

  @GetMapping("/owners/{book_id}")
  public List<String> getOwnersByBook(@PathVariable Long book_id) {
    return bookService.getOwnersLoginByBookName(book_id);
  }

  @GetMapping("/stats")
  public BookAdminStatsDto getBookAdminStats() {
    return bookService.getBookStatsForAdmin();
  }

  @DeleteMapping("/{book_id}")
  public ResponseEntity<String> deleteBooks(
      @PathVariable Long book_id, @RequestParam String login) {
    bookService.deleteOwner(book_id, login);
    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .body("Книга была удалена с вашего аккаунта!");
  }
}
