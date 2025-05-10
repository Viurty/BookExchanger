package com.example.bookservice.service;

import com.example.bookservice.exception.HttpStatusException;
import com.example.bookservice.model.Book;
import com.example.bookservice.model.BookAdminStatsDto;
import com.example.bookservice.repository.BookRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
  private final BookRepository bookRepository;

  @Autowired
  public BookService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @Transactional
  public void addBook(Book book) {
    String name = book.getName();
    boolean isExist = bookRepository.existsByNameIgnoreCase(name);
    if (isExist) {
      Book existingBook =
          bookRepository
              .findByNameIgnoreCase(name)
              .orElseThrow(
                  () ->
                      new HttpStatusException(
                          HttpStatus.NOT_FOUND, "Книга с названием " + name + " не найдена!"));
      ;
      for (String owner : book.getOwners()) {
        if (!existingBook.getOwners().contains(owner)) {
          existingBook.getOwners().add(owner);
        }
      }
      bookRepository.save(existingBook);
    } else {
      try {
        bookRepository.save(book);
      } catch (DataIntegrityViolationException ex) {
        throw new HttpStatusException(
            HttpStatus.CONFLICT, "Книга с названием " + name + " уже существует!");
      }
    }
  }

  @Transactional
  public void deleteOwner(Long id, String ownerLogin) {
    Book book =
        bookRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new HttpStatusException(
                        HttpStatus.NOT_FOUND, "Книга с id  " + id + " не найдена!"));
    ;
    boolean isRemoved = book.getOwners().remove(ownerLogin);
    if (!isRemoved) {
      throw new HttpStatusException(
          HttpStatus.NOT_FOUND, "Владелец " + ownerLogin + " не найден у книги id=" + id);
    }
    bookRepository.save(book);
  }

  public List<Book> getBooksByOwner(String login) {
    return bookRepository.findBooksByOwner(login);
  }

  public List<Book> getBooksByReady() {
    List<Book> readyBooks = bookRepository.findByOwnersIsNotEmpty();
    return readyBooks;
  }

  public List<String> getOwnersLoginByBookName(Long id) {
    return bookRepository.findOwnersById(id);
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
