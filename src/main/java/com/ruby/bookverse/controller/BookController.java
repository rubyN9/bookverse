package com.ruby.bookverse.controller;

import com.ruby.bookverse.dto.BookDto;
import com.ruby.bookverse.entity.BookListStatus;
import com.ruby.bookverse.entity.Review;
import com.ruby.bookverse.entity.User;
import com.ruby.bookverse.service.BookSearchService;
import com.ruby.bookverse.service.ReviewService;
import com.ruby.bookverse.service.SavedBookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookController {

    private final BookSearchService bookSearchService;
    private final SavedBookService savedBookService;
    private final ReviewService reviewService;

    public BookController(
            BookSearchService bookSearchService,
            SavedBookService savedBookService,
            ReviewService reviewService) {

        this.bookSearchService = bookSearchService;
        this.savedBookService = savedBookService;
        this.reviewService = reviewService;
    }

    @GetMapping("/book/{id}")
    public String bookDetails(
            @PathVariable String id,
            Model model,
            HttpSession session) {

        BookDto book = bookSearchService.getBookById(id);

        if (book == null) {
            return "redirect:/";
        }

        model.addAttribute("book", book);

        User user = getLoggedInUser(session);

        boolean isFavorite = false;
        BookListStatus readingStatus = null;
        Review userReview = null;

        if (user != null) {
            isFavorite =
                    savedBookService.isFavorite(id, user);

            readingStatus =
                    savedBookService.getReadingStatus(id, user);

            userReview =
                    reviewService.getUserReview(id, user);
        }

        model.addAttribute("isFavorite", isFavorite);
        model.addAttribute("readingStatus", readingStatus);

        model.addAttribute(
                "reviews",
                reviewService.getReviewsForBook(id)
        );

        model.addAttribute(
                "bookverseRating",
                reviewService.getAverageRating(id)
        );

        model.addAttribute(
                "ratingCount",
                reviewService.getRatingCount(id)
        );

        model.addAttribute("userReview", userReview);

        return "book-details";
    }

    @PostMapping("/book/{id}/favorite")
    public String addFavorite(
            @PathVariable String id,
            HttpSession session) {

        User user = getLoggedInUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        BookDto book = bookSearchService.getBookById(id);

        if (book != null) {
            savedBookService.addFavorite(book, user);
        }

        return "redirect:/book/" + id;
    }

    @PostMapping("/book/{id}/remove-favorite")
    public String removeFavorite(
            @PathVariable String id,
            HttpSession session,
            @RequestParam(
                    required = false,
                    defaultValue = "details"
            ) String from) {

        User user = getLoggedInUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        savedBookService.removeFavorite(id, user);

        if ("favorites".equals(from)) {
            return "redirect:/favorites";
        }

        return "redirect:/book/" + id;
    }

    @PostMapping("/book/{id}/status")
    public String setReadingStatus(
            @PathVariable String id,
            @RequestParam BookListStatus status,
            HttpSession session) {

        User user = getLoggedInUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        BookDto book = bookSearchService.getBookById(id);

        if (book != null) {
            savedBookService.setReadingStatus(
                    book,
                    user,
                    status
            );
        }

        return "redirect:/book/" + id;
    }

    @PostMapping("/book/{id}/remove-status")
    public String removeReadingStatus(
            @PathVariable String id,
            HttpSession session) {

        User user = getLoggedInUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        savedBookService.removeReadingStatus(id, user);

        return "redirect:/book/" + id;
    }

    @PostMapping("/book/{id}/review")
    public String saveReview(
            @PathVariable String id,
            @RequestParam Integer rating,
            @RequestParam(required = false) String reviewText,
            HttpSession session) {

        User user = getLoggedInUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        BookDto book = bookSearchService.getBookById(id);

        if (book == null) {
            return "redirect:/";
        }

        reviewService.saveReview(
                book,
                user,
                rating,
                reviewText
        );

        return "redirect:/book/" + id;
    }

    @PostMapping("/book/{bookId}/review/{reviewId}/delete")
    public String deleteReview(
            @PathVariable String bookId,
            @PathVariable Long reviewId,
            HttpSession session) {

        User user = getLoggedInUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        reviewService.deleteReview(reviewId, user);

        return "redirect:/book/" + bookId;
    }

    @GetMapping("/favorites")
    public String favorites(
            HttpSession session,
            Model model) {

        User user = getLoggedInUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "books",
                savedBookService.getFavorites(user)
        );

        model.addAttribute(
                "pageTitle",
                "Favorites"
        );

        return "favorites";
    }

    @GetMapping("/want-to-read")
    public String wantToRead(
            HttpSession session,
            Model model) {

        return showReadingList(
                session,
                model,
                BookListStatus.WANT_TO_READ,
                "Want to Read"
        );
    }

    @GetMapping("/reading")
    public String reading(
            HttpSession session,
            Model model) {

        return showReadingList(
                session,
                model,
                BookListStatus.READING,
                "Currently Reading"
        );
    }

    @GetMapping("/read")
    public String read(
            HttpSession session,
            Model model) {

        return showReadingList(
                session,
                model,
                BookListStatus.READ,
                "Read"
        );
    }

    @GetMapping("/library")
    public String library(
            HttpSession session,
            Model model) {

        User user = getLoggedInUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "books",
                savedBookService.getUserLibrary(user)
        );

        return "library";
    }

    private String showReadingList(
            HttpSession session,
            Model model,
            BookListStatus status,
            String pageTitle) {

        User user = getLoggedInUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "books",
                savedBookService.getBooksByStatus(
                        user,
                        status
                )
        );

        model.addAttribute(
                "pageTitle",
                pageTitle
        );

        model.addAttribute(
                "status",
                status
        );

        return "reading-list";
    }

    private User getLoggedInUser(
            HttpSession session) {

        return (User) session.getAttribute(
                "loggedInUser"
        );
    }
}