package com.ruby.bookverse.repository;

import com.ruby.bookverse.entity.Review;
import com.ruby.bookverse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    List<Review> findByGoogleBookId(
            String googleBookId
    );

    Optional<Review> findByGoogleBookIdAndUser(
            String googleBookId,
            User user
    );

    long countByGoogleBookId(
            String googleBookId
    );
}