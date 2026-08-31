package com.ruby.bookverse.repository;

import com.ruby.bookverse.entity.FavoriteBook;
import com.ruby.bookverse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteBookRepository
        extends JpaRepository<FavoriteBook, Long> {

    List<FavoriteBook> findByUser(User user);

    boolean existsByGoogleBookIdAndUser(
            String googleBookId,
            User user
    );

    FavoriteBook findByGoogleBookIdAndUser(
            String googleBookId,
            User user
    );
}