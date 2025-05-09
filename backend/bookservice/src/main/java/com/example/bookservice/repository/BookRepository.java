package com.example.bookservice.repository;

import com.example.bookservice.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
  @Query("SELECT b FROM Book b JOIN b.owners o WHERE o.key = :ownerLogin")
  List<Book> findBooksByOwner(String ownerLogin);

  @Query("SELECT b FROM Book b JOIN b.owners o WHERE o.value = :status")
  List<Book> findBooksByReadyForExchange(boolean status);

  @Query("SELECT b.owners FROM Book b WHERE b.name = :name")
  Map<String, Boolean> findOwnersByBookName(String name);
}
