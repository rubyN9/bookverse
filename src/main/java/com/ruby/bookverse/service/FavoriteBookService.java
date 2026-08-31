package com.ruby.bookverse.service;

import com.ruby.bookverse.dto.BookDto;
import com.ruby.bookverse.entity.FavoriteBook;
import com.ruby.bookverse.entity.User;
import com.ruby.bookverse.repository.FavoriteBookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteBookService {

    private final FavoriteBookRepository repository;

    public FavoriteBookService(
            FavoriteBookRepository repository) {

        this.repository = repository;
    }


    // Save book to favorites
    public void saveFavorite(
            BookDto bookDto,
            User user) {

        boolean alreadyExists =
                repository.existsByGoogleBookIdAndUser(
                        bookDto.getId(),
                        user
                );

        if (alreadyExists) {
            return;
        }

        FavoriteBook favoriteBook =
                new FavoriteBook();

        favoriteBook.setGoogleBookId(
                bookDto.getId()
        );

        favoriteBook.setTitle(
                bookDto.getTitle()
        );

        favoriteBook.setAuthor(
                bookDto.getAuthor()
        );

        favoriteBook.setGenre(
                bookDto.getCategory()
        );

        favoriteBook.setPages(
                bookDto.getPageCount()
        );

        favoriteBook.setRating(
                bookDto.getRating()
        );

        favoriteBook.setImage(
                bookDto.getImage()
        );

        favoriteBook.setUser(user);

        repository.save(favoriteBook);
    }


    // Get favorites for one user
    public List<FavoriteBook> getUserFavorites(
            User user) {

        return repository.findByUser(user);
    }


    // Check if book is favorite
    public boolean isFavorite(
            String googleBookId,
            User user) {

        return repository.existsByGoogleBookIdAndUser(
                googleBookId,
                user
        );
    }


    // Remove book from favorites
    public void removeFavorite(
            String googleBookId,
            User user) {

        FavoriteBook favoriteBook =
                repository.findByGoogleBookIdAndUser(
                        googleBookId,
                        user
                );

        if (favoriteBook != null) {
            repository.delete(favoriteBook);
        }
    }
}