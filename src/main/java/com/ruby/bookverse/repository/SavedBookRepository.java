package com.ruby.bookverse.repository;

import com.ruby.bookverse.entity.BookListStatus;
import com.ruby.bookverse.entity.SavedBook;
import com.ruby.bookverse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedBookRepository
        extends JpaRepository<SavedBook, Long> {

    List<SavedBook> findByUser(
            User user
    );

    List<SavedBook> findByUserAndFavoriteTrue(
            User user
    );

    List<SavedBook> findByUserAndReadingStatus(
            User user,
            BookListStatus readingStatus
    );

    Optional<SavedBook> findByGoogleBookIdAndUser(
            String googleBookId,
            User user
    );
}