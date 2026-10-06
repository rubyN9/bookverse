package com.ruby.bookverse.service;

import com.ruby.bookverse.dto.BookDto;
import com.ruby.bookverse.entity.BookListStatus;
import com.ruby.bookverse.entity.SavedBook;
import com.ruby.bookverse.entity.User;
import com.ruby.bookverse.repository.SavedBookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SavedBookService {

    private final SavedBookRepository repository;

    public SavedBookService(
            SavedBookRepository repository) {

        this.repository = repository;
    }

    public void addFavorite(
            BookDto bookDto,
            User user) {

        SavedBook savedBook =
                getOrCreateSavedBook(
                        bookDto,
                        user
                );

        savedBook.setFavorite(true);

        repository.save(savedBook);
    }

    public void removeFavorite(
            String googleBookId,
            User user) {

        Optional<SavedBook> savedBookOptional =
                repository.findByGoogleBookIdAndUser(
                        googleBookId,
                        user
                );

        if (savedBookOptional.isEmpty()) {
            return;
        }

        SavedBook savedBook =
                savedBookOptional.get();

        savedBook.setFavorite(false);

        cleanUpIfUnused(savedBook);
    }

    public boolean isFavorite(
            String googleBookId,
            User user) {

        Optional<SavedBook> savedBookOptional =
                repository.findByGoogleBookIdAndUser(
                        googleBookId,
                        user
                );

        if (savedBookOptional.isEmpty()) {
            return false;
        }

        SavedBook savedBook =
                savedBookOptional.get();

        return savedBook.isFavorite();
    }

    public List<SavedBook> getFavorites(
            User user) {

        return repository
                .findByUserAndFavoriteTrue(user);
    }

    public void setReadingStatus(
            BookDto bookDto,
            User user,
            BookListStatus status) {

        SavedBook savedBook =
                getOrCreateSavedBook(
                        bookDto,
                        user
                );

        savedBook.setReadingStatus(status);

        repository.save(savedBook);
    }

    public void removeReadingStatus(
            String googleBookId,
            User user) {

        Optional<SavedBook> savedBookOptional =
                repository.findByGoogleBookIdAndUser(
                        googleBookId,
                        user
                );

        if (savedBookOptional.isEmpty()) {
            return;
        }

        SavedBook savedBook =
                savedBookOptional.get();

        savedBook.setReadingStatus(null);

        cleanUpIfUnused(savedBook);
    }

    public BookListStatus getReadingStatus(
            String googleBookId,
            User user) {

        Optional<SavedBook> savedBookOptional =
                repository.findByGoogleBookIdAndUser(
                        googleBookId,
                        user
                );

        if (savedBookOptional.isEmpty()) {
            return null;
        }

        SavedBook savedBook =
                savedBookOptional.get();

        return savedBook.getReadingStatus();
    }

    public List<SavedBook> getBooksByStatus(
            User user,
            BookListStatus status) {

        return repository
                .findByUserAndReadingStatus(
                        user,
                        status
                );
    }

    public List<SavedBook> getUserLibrary(
            User user) {

        return repository.findByUser(user);
    }

    private SavedBook getOrCreateSavedBook(
            BookDto bookDto,
            User user) {

        Optional<SavedBook> existingBook =
                repository.findByGoogleBookIdAndUser(
                        bookDto.getId(),
                        user
                );

        if (existingBook.isPresent()) {
            return existingBook.get();
        }

        SavedBook savedBook =
                new SavedBook();

        savedBook.setGoogleBookId(
                bookDto.getId()
        );

        savedBook.setTitle(
                bookDto.getTitle()
        );

        savedBook.setAuthor(
                bookDto.getAuthor()
        );

        savedBook.setCategory(
                bookDto.getCategory()
        );

        savedBook.setPages(
                bookDto.getPageCount()
        );

        savedBook.setRating(
                bookDto.getRating()
        );

        savedBook.setImage(
                bookDto.getImage()
        );

        savedBook.setFavorite(false);
        savedBook.setReadingStatus(null);
        savedBook.setUser(user);

        return savedBook;
    }

    private void cleanUpIfUnused(
            SavedBook savedBook) {

        boolean notFavorite =
                !savedBook.isFavorite();

        boolean noReadingStatus =
                savedBook.getReadingStatus() == null;

        // Keep the row only while the book is used by favorites or a reading list.
        if (notFavorite && noReadingStatus) {
            repository.delete(savedBook);
            return;
        }

        repository.save(savedBook);
    }
}