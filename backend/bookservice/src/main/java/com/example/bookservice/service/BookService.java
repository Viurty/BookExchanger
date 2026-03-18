package com.example.bookservice.service;

import com.example.bookservice.exception.HttpStatusException;
import com.example.bookservice.model.Book;
import com.example.bookservice.model.BookAdminStatsDto;
import com.example.bookservice.model.BookDto;
import com.example.bookservice.repository.BookRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {
  private final BookRepository bookRepository;

  @Autowired
  public BookService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  public Book getBooksById(Long bookId) {
    return bookRepository
        .findById(bookId)
        .orElseThrow(
            () ->
                new HttpStatusException(
                    HttpStatus.NOT_FOUND, "Книга с id =  " + bookId + " не найдена!"));
  }

  @Transactional
  public void addBookOnSite(BookDto bookDto) {
    String name = bookDto.getName();
    String owner = bookDto.getOwner();

    List<String> owners = new ArrayList<>();
    owners.add(owner);
    Book book =
        new Book(
            owners,
            bookDto.getName(),
            bookDto.getAuthor(),
            bookDto.getGenre(),
            bookDto.getDescription());

    try {
      bookRepository.save(book);
    } catch (DataIntegrityViolationException ex) {
      throw new HttpStatusException(
          HttpStatus.CONFLICT, "Книга с названием " + name + " уже существует!");
    }
  }

  @Transactional
  public void addBookOnProfile(BookDto bookDto) {
    String name = bookDto.getName();
    String owner = bookDto.getOwner();

    if (owner == null || name == null || name.trim().isEmpty() || owner.trim().isEmpty()) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Недостаточно данных!");
    }
    Book book =
        bookRepository
            .findByNameIgnoreCase(name)
            .orElseThrow(
                () ->
                    new HttpStatusException(
                        HttpStatus.NOT_FOUND, "Книга с названием " + name + " не найдена!"));
    if (!book.getOwners().contains(owner)) {
      book.getOwners().add(owner);
    } else {
      throw new HttpStatusException(HttpStatus.CONFLICT, "Книга уже привязана к профилю!");
    }

    try {
      bookRepository.save(book);
    } catch (DataIntegrityViolationException ex) {
      throw new HttpStatusException(
          HttpStatus.CONFLICT, "Книга с названием " + name + " уже существует!");
    }
  }

  @Transactional
  public void deleteOwner(BookDto bookDto) {
    String name = bookDto.getName();
    String owner = bookDto.getOwner();

    if (owner == null || name == null || name.trim().isEmpty() || owner.trim().isEmpty()) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Недостаточно данных!");
    }
    Book book =
        bookRepository
            .findByNameIgnoreCase(name)
            .orElseThrow(
                () ->
                    new HttpStatusException(
                        HttpStatus.NOT_FOUND, "Книга с названием  " + name + " не найдена!"));
    ;
    boolean isRemoved = book.getOwners().remove(owner);
    if (!isRemoved) {
      throw new HttpStatusException(
          HttpStatus.NOT_FOUND, "Владелец " + owner + " не привязан к книге с названием " + name);
    }
    bookRepository.save(book);
  }

  public List<Book> getBooksByOwner(String login) {
    return bookRepository.findBooksByOwner(login);
  }

  public List<Book> getBooksByReady() {
    return bookRepository.findByOwnersIsNotEmpty();
  }

  public List<String> getOwnersLoginById(Long bookId) {
    return bookRepository.findOwnersById(bookId);
  }

  private long getCountBooks() {
    return bookRepository.count();
  }

  private long getCountReadyBooks() {
    return bookRepository.countByOwnersIsNotEmpty();
  }

  public BookAdminStatsDto getBookStatsForAdmin() {
    long countBooks = getCountBooks();
    long countReadyBooks = getCountReadyBooks();
    int percentReady = 0;
    if (countBooks > 0) {
      percentReady = (int) (100 * countReadyBooks / countBooks);
    }
    return new BookAdminStatsDto(countBooks, countReadyBooks, percentReady);
  }
}
