package com.example.bookservice.repository;

import com.example.bookservice.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
  List<Review> findByAuthor(String author);

  List<Review> findByBookName(String bookName);
}
