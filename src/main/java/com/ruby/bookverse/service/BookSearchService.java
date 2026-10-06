package com.ruby.bookverse.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruby.bookverse.dto.BookDto;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookSearchService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, List<BookDto>> searchCache = new HashMap<>();
    private final Map<String, BookDto> bookCache = new HashMap<>();

    @Value("${google.books.api.key}")
    private String apiKey;

    public List<BookDto> searchBooks(String query) {

        String normalizedQuery = query.trim();

        if (searchCache.containsKey(normalizedQuery)) {
            return searchCache.get(normalizedQuery);
        }

        String encodedQuery = URLEncoder.encode(
                normalizedQuery,
                StandardCharsets.UTF_8
        );

        String url = "https://www.googleapis.com/books/v1/volumes?q="
                + encodedQuery
                + "&maxResults=20"
                + "&printType=books"
                + "&orderBy=relevance"
                + "&key=" + apiKey;

        try {

            String response = restTemplate.getForObject(
                    url,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.path("items");

            List<BookDto> books = new ArrayList<>();

            if (items.isArray()) {

                for (JsonNode item : items) {

                    String id = item.path("id").asText();

                    if (id.isBlank()) {
                        continue;
                    }

                    BookDto book = mapToBookDto(item);

                    books.add(book);
                    bookCache.put(id, book);
                }
            }

            sortBooks(books, normalizedQuery);

            List<BookDto> finalResults = books.stream()
                    .limit(10)
                    .toList();

            searchCache.put(normalizedQuery, finalResults);

            return finalResults;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public BookDto getBookById(String id) {

        if (id == null || id.isBlank()) {
            return null;
        }

        if (bookCache.containsKey(id)) {
            return bookCache.get(id);
        }

        String url = "https://www.googleapis.com/books/v1/volumes/"
                + id
                + "?key=" + apiKey;

        try {

            String response = restTemplate.getForObject(
                    url,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response);

            BookDto book = mapToBookDto(root);

            bookCache.put(id, book);

            return book;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sortBooks(List<BookDto> books, String query) {

        String searchText = query.toLowerCase();

        if (searchText.startsWith("subject:")) {
            searchText = searchText.substring("subject:".length());
        }

        String finalSearchText = searchText;

        Comparator<BookDto> comparator =
                Comparator.comparingInt(
                                (BookDto book) -> getTitleMatchScore(book, finalSearchText)
                        ).reversed()
                        .thenComparing(
                                Comparator.comparingInt(
                                        (BookDto book) -> hasImage(book) ? 1 : 0
                                ).reversed()
                        )
                        .thenComparing(
                                Comparator.comparingInt(
                                        (BookDto book) -> hasRating(book) ? 1 : 0
                                ).reversed()
                        )
                        .thenComparing(
                                Comparator.comparingInt(
                                        BookDto::getRatingsCount
                                ).reversed()
                        );

        books.sort(comparator);
    }

    private int getTitleMatchScore(BookDto book, String query) {

        if (book.getTitle() == null) {
            return 0;
        }

        String title = book.getTitle()
                .trim()
                .toLowerCase();

        if (title.equals(query)) {
            return 3;
        }

        if (title.startsWith(query)) {
            return 2;
        }

        if (title.contains(query)) {
            return 1;
        }

        return 0;
    }

    private boolean hasImage(BookDto book) {
        return book.getImage() != null
                && !book.getImage().isBlank();
    }

    private boolean hasRating(BookDto book) {
        return book.getRating() != null
                && book.getRating() > 0;
    }

    private BookDto mapToBookDto(JsonNode item) {

        JsonNode volumeInfo = item.path("volumeInfo");

        BookDto book = new BookDto();

        book.setId(
                item.path("id")
                        .asText()
        );

        book.setTitle(
                volumeInfo.path("title")
                        .asText("No Title")
        );

        book.setAuthor(
                getFirstValue(
                        volumeInfo.path("authors"),
                        "Unknown Author"
                )
        );

        book.setCategory(
                getFirstValue(
                        volumeInfo.path("categories"),
                        "Unknown"
                )
        );

        book.setRating(
                volumeInfo.path("averageRating")
                        .asDouble(0.0)
        );

        book.setRatingsCount(
                volumeInfo.path("ratingsCount")
                        .asInt(0)
        );

        book.setPublishedDate(
                volumeInfo.path("publishedDate")
                        .asText("Unknown")
        );

        book.setPageCount(
                volumeInfo.path("pageCount")
                        .asInt(0)
        );

        String description = volumeInfo.path("description")
                .asText("No Description Available.");

        book.setDescription(
                Jsoup.parse(description).text()
        );

        book.setImage(
                getBookImage(volumeInfo)
        );

        return book;
    }

    private String getFirstValue(
            JsonNode values,
            String defaultValue) {

        if (values.isArray() && !values.isEmpty()) {
            return values.get(0).asText();
        }

        return defaultValue;
    }

    private String getBookImage(JsonNode volumeInfo) {

        JsonNode imageLinks = volumeInfo.path("imageLinks");

        if (imageLinks.isMissingNode() || imageLinks.isEmpty()) {
            return null;
        }

        String image = getImageUrl(imageLinks, "extraLarge");

        if (image.isBlank()) {
            image = getImageUrl(imageLinks, "large");
        }

        if (image.isBlank()) {
            image = getImageUrl(imageLinks, "medium");
        }

        if (image.isBlank()) {
            image = getImageUrl(imageLinks, "small");
        }

        if (image.isBlank()) {
            image = getImageUrl(imageLinks, "thumbnail");
        }

        if (image.isBlank()) {
            image = getImageUrl(imageLinks, "smallThumbnail");
        }

        if (image.isBlank()) {
            return null;
        }

        return image.replace("http://", "https://");
    }

    private String getImageUrl(
            JsonNode imageLinks,
            String size) {

        return imageLinks.path(size).asText("");
    }
}