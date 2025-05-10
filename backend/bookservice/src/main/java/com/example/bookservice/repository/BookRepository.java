package com.example.bookservice.repository;

import com.example.bookservice.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

  boolean existsByNameIgnoreCase(String name);

  Optional<Book> findByNameIgnoreCase(String name);

  List<Book> findByOwnersIsNotEmpty();

  @Query("SELECT o FROM Book b JOIN b.owners o WHERE b.id = :id")
  List<String> findOwnersById(Long id);

  @Query("SELECT DISTINCT b FROM Book b JOIN b.owners o WHERE o = :ownerLogin")
  List<Book> findBooksByOwner(String ownerLogin);

  long countByOwnersIsNotEmpty();
}
