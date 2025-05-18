package com.example.bookservice.service;

import com.example.bookservice.exception.HttpStatusException;
import com.example.bookservice.model.Book;
import com.example.bookservice.model.ReviewBookStatsDto;
import com.example.bookservice.model.Review;
import com.example.bookservice.model.ReviewAdminStatsDto;
import com.example.bookservice.repository.BookRepository;
import com.example.bookservice.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {
  private final ReviewRepository reviewRepository;
  private final BookRepository bookRepository;

  @Autowired
  public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository) {
    this.reviewRepository = reviewRepository;
    this.bookRepository = bookRepository;
  }

  @Transactional
  public void addReview(Review review) {
    String bookName = review.getBookName();
    String author = review.getAuthor();
    boolean isExist = reviewRepository.existsByAuthorAndBookName(author, bookName);
    if (isExist) {
      throw new HttpStatusException(
          HttpStatus.CONFLICT, "Вы уже оставили отзыв на книгу " + bookName);
    }
    if (!bookRepository.existsByNameIgnoreCase(bookName)) {
      throw new HttpStatusException(
          HttpStatus.CONFLICT, "Книга с названием " + bookName + " не существует!");
    }
    List<String> owners = bookRepository.findOwnersByBookName(bookName);
    if (!owners.contains(author)) {
      throw new HttpStatusException(
          HttpStatus.BAD_REQUEST, "Вы не обладаете книгой с названием " + bookName);
    }

    LocalDateTime now = LocalDateTime.now();
    review.setCreatedAt(now);
    try {
      reviewRepository.save(review);
    } catch (DataIntegrityViolationException e) {
      throw new HttpStatusException(HttpStatus.CONFLICT, "Не удалось сохранить отзыв!");
    }
  }

  @Transactional
  public void deleteReview(Long id) {
    try {
      reviewRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      throw new HttpStatusException(HttpStatus.NOT_FOUND, "Отзыв с id  " + id + " не найден!");
    }
  }

  public ReviewBookStatsDto getReviewStatsAboutBook(Long bookId) {
    Book book =
        bookRepository
            .findById(bookId)
            .orElseThrow(
                () ->
                    new HttpStatusException(
                        HttpStatus.NOT_FOUND, "Книга с id =   " + bookId + " не найдена!"));
    ;
    List<Review> reviews = reviewRepository.findByBookName(book.getName());
    if (reviews == null || reviews.isEmpty()) {
      return new ReviewBookStatsDto(0.0, 0);
    }

    int countRating = reviews.size();
    double sumRating = 0.0;

    for (Review review : reviews) {
      sumRating += review.getRating();
    }

    double averageRating = sumRating / countRating;
    return new ReviewBookStatsDto(averageRating, countRating);
  }

  public List<Review> getReviewsById(Long bookId) {
    Book book =
        bookRepository
            .findById(bookId)
            .orElseThrow(
                () ->
                    new HttpStatusException(
                        HttpStatus.NOT_FOUND, "Книга с id =   " + bookId + " не найдена!"));
    return reviewRepository.findByBookName(book.getName());
  }

  public List<Review> getReviewsByAuthor(String login) {
    return reviewRepository.findByAuthor(login);
  }

  public List<Review> getReviewsByDescCreatedAt() {
    return reviewRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
  }

  public ReviewAdminStatsDto getReviewStatsForAdmin() {
    long countReviews = reviewRepository.count();
    long countRate5 = reviewRepository.countByRating(5);
    long countRate1 = reviewRepository.countByRating(1);
    return new ReviewAdminStatsDto(countReviews, countRate5, countRate1);
  }
}
