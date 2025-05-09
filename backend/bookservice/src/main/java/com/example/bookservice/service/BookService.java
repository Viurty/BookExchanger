package com.example.bookservice.service;

import com.example.bookservice.model.Book;
import com.example.bookservice.model.BookStatsDto;
import com.example.bookservice.model.UserDto;
import com.example.bookservice.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class BookService {
  private final BookRepository bookRepository;

  @Autowired
  public BookService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @Transactional
  public void addBook(Book book) {
    bookRepository.save(book);
  }

  public List<Book> getBooksByOwner(String login) {
    return bookRepository.findBooksByOwner(login);
  }

  public List<Book> getBooksByReady(boolean status) {
    return bookRepository.findBooksByReadyForExchange(status);
  }

  public List<String> getOwnersLoginByBookName(String name) {
    return new ArrayList<String>(bookRepository.findOwnersByBookName(name).keySet());
  }

  public List<String> getOwnersLoginByBookNameAndReadyForExchange(String name) {
    List<String> ownersLogin = new ArrayList<String>();
    Map<String, Boolean> owners = bookRepository.findOwnersByBookName(name);
    Set<String> setKeys = owners.keySet();
    for (String k : setKeys) {
      if (owners.get(k)) {
        ownersLogin.add(k);
      }
    }
    return ownersLogin;
  }

  private int getCountBooks() {
    List<Book> books = bookRepository.findAll();
    return books.size();
  }

  private int getCountReadyBooks() {
    List<Book> books = bookRepository.findBooksByReadyForExchange(true);
    return books.size();
  }

  public BookStatsDto getStatsByExchange() {
    int countBooks = getCountBooks();
    int countReadyBooks = getCountReadyBooks();
    int percentSuccess = 0;
    if (countBooks > 0) {
      percentSuccess = 100 * countReadyBooks / countBooks;
    }
    return new BookStatsDto(countBooks, countReadyBooks, percentSuccess);
  }
}
