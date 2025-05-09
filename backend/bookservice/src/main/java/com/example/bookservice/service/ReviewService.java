package com.example.bookservice.service;

import com.example.bookservice.model.Review;
import com.example.bookservice.model.ReviewStatsDto;
import com.example.bookservice.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
  private final ReviewRepository reviewRepository;

  @Autowired
  public ReviewService(ReviewRepository reviewRepository) {
    this.reviewRepository = reviewRepository;
  }

  public void addReview(Review review) {
    reviewRepository.save(review);
  }

  public List<Review> getReviewsByBookName(String name) {
    return reviewRepository.findByBookName(name);
  }

  public List<Review> getReviewsByAuthor(String login) {
    return reviewRepository.findByAuthor(login);
  }

  public List<Review> getReviewsByDescCreatedAt() {
    return reviewRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
  }

  private int getCountReviews() {
    List<Review> reviews = reviewRepository.findAll();
    return reviews.size();
  }

  private int getCountRate5() {
    List<Review> reviews = reviewRepository.findByRate(5);
    return reviews.size();
  }

  private int getCountRate1() {
    List<Review> reviews = reviewRepository.findByRate(5);
    return reviews.size();
  }

  public ReviewStatsDto getStatsByReview() {
    int countReviews = getCountReviews();
    int countRate5 = getCountRate5();
    int countRate1 = getCountRate1();
    return new ReviewStatsDto(countReviews, countRate5, countRate1);
  }
}
