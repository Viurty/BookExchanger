package com.example.bookservice.controller;

import com.example.bookservice.model.Review;
import com.example.bookservice.model.ReviewAdminStatsDto;
import com.example.bookservice.model.ReviewBookStatsDto;
import com.example.bookservice.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "${frontend.address:http://localhost:5173}")
public class ReviewController {
  private final ReviewService reviewService;

  @Autowired
  public ReviewController(ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @PostMapping()
  public ResponseEntity<String> createReview(@Valid @RequestBody Review review) {
    reviewService.addReview(review);
    return ResponseEntity.status(HttpStatus.CREATED).body("Отзыв успешно создан!");
  }

  @GetMapping("/user/{login}")
  public List<Review> getReviewsByLogin(@PathVariable String login) {
    return reviewService.getReviewsByAuthor(login);
  }

  @GetMapping("/book/{bookId}")
  public List<Review> getReviewsByBook(@PathVariable Long bookId) {
    return reviewService.getReviewsById(bookId);
  }

  @GetMapping("/last")
  public List<Review> getLastReviews() {
    return reviewService.getReviewsByDescCreatedAt();
  }

  @GetMapping("/stats")
  public ReviewAdminStatsDto getReviewAdminStats() {
    return reviewService.getReviewStatsForAdmin();
  }

  @GetMapping("/stats/{bookId}")
  public ReviewBookStatsDto getReviewBookStats(@PathVariable Long bookId) {
    return reviewService.getReviewStatsAboutBook(bookId);
  }

  @DeleteMapping("/{review_id}")
  public ResponseEntity<String> deleteReview(@PathVariable Long review_id) {
    reviewService.deleteReview(review_id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Отзыв успешно удален!");
  }
}
