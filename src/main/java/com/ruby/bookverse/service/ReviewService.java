package com.ruby.bookverse.service;

import com.ruby.bookverse.dto.BookDto;
import com.ruby.bookverse.entity.Review;
import com.ruby.bookverse.entity.User;
import com.ruby.bookverse.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private static final int MAX_REVIEW_LENGTH = 3000;

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review saveReview(
            BookDto book,
            User user,
            Integer rating,
            String reviewText) {

        validateRating(rating);

        String cleanReviewText =
                cleanReviewText(reviewText);

        Optional<Review> existingReview =
                reviewRepository.findByGoogleBookIdAndUser(
                        book.getId(),
                        user
                );

        Review review;

        if (existingReview.isPresent()) {
            review = existingReview.get();

        } else {
            review = new Review();

            review.setGoogleBookId(
                    book.getId()
            );

            review.setUser(user);
        }

        review.setBookTitle(
                book.getTitle()
        );

        review.setRating(rating);

        review.setReviewText(
                cleanReviewText
        );

        return reviewRepository.save(review);
    }

    public List<Review> getReviewsForBook(
            String googleBookId) {

        return reviewRepository.findByGoogleBookId(
                googleBookId
        );
    }

    public Review getUserReview(
            String googleBookId,
            User user) {

        if (user == null) {
            return null;
        }

        Optional<Review> review =
                reviewRepository.findByGoogleBookIdAndUser(
                        googleBookId,
                        user
                );

        if (review.isEmpty()) {
            return null;
        }

        return review.get();
    }

    public void deleteReview(
            Long reviewId,
            User user) {

        Optional<Review> optionalReview =
                reviewRepository.findById(
                        reviewId
                );

        if (optionalReview.isEmpty()) {
            return;
        }

        Review review =
                optionalReview.get();

        boolean belongsToUser =
                review.getUser()
                        .getId()
                        .equals(user.getId());

        if (!belongsToUser) {
            throw new IllegalArgumentException(
                    "You cannot delete this review."
            );
        }

        reviewRepository.delete(review);
    }

    public Double getAverageRating(
            String googleBookId) {

        List<Review> reviews =
                reviewRepository.findByGoogleBookId(
                        googleBookId
                );

        if (reviews.isEmpty()) {
            return 0.0;
        }

        double total =
                reviews.stream()
                        .mapToInt(Review::getRating)
                        .sum();

        double average =
                total / reviews.size();

        return Math.round(
                average * 10.0
        ) / 10.0;
    }

    public long getRatingCount(
            String googleBookId) {

        return reviewRepository.countByGoogleBookId(
                googleBookId
        );
    }

    private String cleanReviewText(
            String reviewText) {

        if (reviewText == null) {
            return null;
        }

        String cleanText =
                reviewText.trim();

        if (cleanText.length() > MAX_REVIEW_LENGTH) {
            throw new IllegalArgumentException(
                    "Review must not exceed "
                            + MAX_REVIEW_LENGTH
                            + " characters."
            );
        }

        if (cleanText.isBlank()) {
            return null;
        }

        return cleanText;
    }

    private void validateRating(
            Integer rating) {

        if (rating == null
                || rating < 1
                || rating > 5) {

            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5."
            );
        }
    }
}