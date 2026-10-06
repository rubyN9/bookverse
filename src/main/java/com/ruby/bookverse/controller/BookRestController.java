package com.ruby.bookverse.controller;

import com.ruby.bookverse.dto.BookDto;
import com.ruby.bookverse.dto.LibraryBookDto;
import com.ruby.bookverse.entity.SavedBook;
import com.ruby.bookverse.entity.User;
import com.ruby.bookverse.exception.BookNotFoundException;
import com.ruby.bookverse.exception.InvalidRequestException;
import com.ruby.bookverse.exception.UnauthorizedException;
import com.ruby.bookverse.service.BookSearchService;
import com.ruby.bookverse.service.SavedBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookRestController {

    private final BookSearchService bookSearchService;
    private final SavedBookService savedBookService;

    public BookRestController(
            BookSearchService bookSearchService,
            SavedBookService savedBookService) {

        this.bookSearchService = bookSearchService;
        this.savedBookService = savedBookService;
    }

    @Operation(
            summary = "Search books",
            description = "Search for books by title, author or keyword."
    )
    @ApiResponse(responseCode = "200", description = "Books found")
    @ApiResponse(
            responseCode = "400",
            description = "Search query is empty"
    )
    @GetMapping("/books/search")
    public ResponseEntity<List<BookDto>> searchBooks(
            @RequestParam String q) {

        if (q == null || q.isBlank()) {
            throw new InvalidRequestException(
                    "Search query cannot be empty"
            );
        }

        List<BookDto> books =
                bookSearchService.searchBooks(q.trim());

        return ResponseEntity.ok(books);
    }

    @Operation(
            summary = "Get book details",
            description = "Get a book using its Google Books ID."
    )
    @ApiResponse(responseCode = "200", description = "Book found")
    @ApiResponse(
            responseCode = "404",
            description = "Book not found"
    )
    @GetMapping("/books/{id}")
    public ResponseEntity<BookDto> getBookById(
            @PathVariable String id) {

        BookDto book =
                bookSearchService.getBookById(id);

        if (book == null) {
            throw new BookNotFoundException(
                    "Book not found with id: " + id
            );
        }

        return ResponseEntity.ok(book);
    }

    @Operation(
            summary = "Get user library",
            description = "Get the logged-in user's saved books."
    )
    @ApiResponse(responseCode = "200", description = "Library retrieved")
    @ApiResponse(
            responseCode = "401",
            description = "Login required"
    )
    @GetMapping("/library")
    public ResponseEntity<List<LibraryBookDto>> getLibrary(
            HttpSession session) {

        User user = getLoggedInUser(session);

        if (user == null) {
            throw new UnauthorizedException(
                    "Login required"
            );
        }

        List<LibraryBookDto> library =
                savedBookService.getUserLibrary(user)
                        .stream()
                        .map(this::toLibraryBookDto)
                        .toList();

        return ResponseEntity.ok(library);
    }

    private LibraryBookDto toLibraryBookDto(
            SavedBook savedBook) {

        return new LibraryBookDto(
                savedBook.getGoogleBookId(),
                savedBook.getTitle(),
                savedBook.getAuthor(),
                savedBook.getCategory(),
                savedBook.getPages(),
                savedBook.getImage(),
                savedBook.getReadingStatus(),
                savedBook.isFavorite()
        );
    }

    private User getLoggedInUser(
            HttpSession session) {

        return (User) session.getAttribute(
                "loggedInUser"
        );
    }
}