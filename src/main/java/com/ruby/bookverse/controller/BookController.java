package com.ruby.bookverse.controller;

import com.ruby.bookverse.dto.BookDto;
import com.ruby.bookverse.entity.User;
import com.ruby.bookverse.service.BookSearchService;
import com.ruby.bookverse.service.FavoriteBookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BookController {

    private final BookSearchService bookSearchService;
    private final FavoriteBookService favoriteBookService;

    public BookController(
            BookSearchService bookSearchService,
            FavoriteBookService favoriteBookService) {

        this.bookSearchService = bookSearchService;
        this.favoriteBookService = favoriteBookService;
    }


    // Book Details
    @GetMapping("/book/{id}")
    public String bookDetails(
            @PathVariable String id,
            Model model,
            HttpSession session) {

        BookDto book =
                bookSearchService.getBookById(id);

        model.addAttribute(
                "book",
                book
        );

        User user =
                (User) session.getAttribute(
                        "loggedInUser"
                );

        boolean isFavorite = false;

        if (user != null) {

            isFavorite =
                    favoriteBookService.isFavorite(
                            id,
                            user
                    );
        }

        model.addAttribute(
                "isFavorite",
                isFavorite
        );

        return "book-details";
    }


    // Add Book to Favorites
    @PostMapping("/book/{id}/favorite")
    public String addToFavorites(
            @PathVariable String id,
            HttpSession session) {

        User user =
                (User) session.getAttribute(
                        "loggedInUser"
                );

        if (user == null) {
            return "redirect:/login";
        }

        BookDto book =
                bookSearchService.getBookById(id);

        favoriteBookService.saveFavorite(
                book,
                user
        );

        return "redirect:/book/" + id;
    }


    // Remove Book from Favorites
    @PostMapping("/book/{id}/remove-favorite")
    public String removeFromFavorites(
            @PathVariable String id,
            HttpSession session) {

        User user =
                (User) session.getAttribute(
                        "loggedInUser"
                );

        if (user == null) {
            return "redirect:/login";
        }

        favoriteBookService.removeFavorite(
                id,
                user
        );

        return "redirect:/favorites";
    }


    // Favorites Page
    @GetMapping("/favorites")
    public String favorites(
            HttpSession session,
            Model model) {

        User user =
                (User) session.getAttribute(
                        "loggedInUser"
                );

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "favorites",
                favoriteBookService.getUserFavorites(user)
        );

        return "favorites";
    }
}