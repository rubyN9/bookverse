package com.ruby.bookverse.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruby.bookverse.dto.BookDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;

@Service
public class BookSearchService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final Map<String, List<BookDto>> cache = new HashMap<>();

    @Value("${google.books.api.key}")
    private String apiKey;


    // =========================
    // Search for books
    // =========================

    public List<BookDto> searchBooks(String query) {

        if (cache.containsKey(query)) {
            return cache.get(query);
        }

        String encodedQuery =
                URLEncoder.encode(query, StandardCharsets.UTF_8);

        String url =
                "https://www.googleapis.com/books/v1/volumes?q="
                        + encodedQuery
                        + "&maxResults=10"
                        + "&key=" + apiKey;

        try {

            String response =
                    restTemplate.getForObject(url, String.class);

            ObjectMapper mapper = new ObjectMapper();

            JsonNode root =
                    mapper.readTree(response);

            List<BookDto> books = new ArrayList<>();

            JsonNode items =
                    root.path("items");


            for (JsonNode item : items) {

                JsonNode volumeInfo =
                        item.path("volumeInfo");

                BookDto dto = new BookDto();


                // ID
                dto.setId(
                        item.path("id")
                                .asText()
                );


                // Title
                dto.setTitle(
                        volumeInfo.path("title")
                                .asText("No Title")
                );


                // Author
                JsonNode authors =
                        volumeInfo.path("authors");

                if (authors.isArray() && authors.size() > 0) {

                    dto.setAuthor(
                            authors.get(0)
                                    .asText()
                    );

                } else {

                    dto.setAuthor("Unknown Author");
                }


                // Category
                JsonNode categories =
                        volumeInfo.path("categories");

                if (categories.isArray() && categories.size() > 0) {

                    dto.setCategory(
                            categories.get(0)
                                    .asText()
                    );

                } else {

                    dto.setCategory("Unknown");
                }


                // Rating
                dto.setRating(
                        volumeInfo.path("averageRating")
                                .asDouble(0.0)
                );


                // Description
                dto.setDescription(
                        volumeInfo.path("description")
                                .asText("No Description Available.")
                );


                // Image
                String image = "";

                if (volumeInfo.has("imageLinks")) {

                    image =
                            volumeInfo.path("imageLinks")
                                    .path("thumbnail")
                                    .asText("");

                    image =
                            image.replace(
                                    "http://",
                                    "https://"
                            );
                }


                if (image.isBlank()) {

                    image =
                            "https://placehold.co/300x450?text=No+Cover";
                }


                dto.setImage(image);

                books.add(dto);
            }


            // Sort by highest rating
            books.sort(
                    (b1, b2) ->
                            Double.compare(
                                    b2.getRating(),
                                    b1.getRating()
                            )
            );


            cache.put(query, books);

            return books;


        } catch (Exception e) {

            e.printStackTrace();

            return new ArrayList<>();
        }
    }



    // =========================
    // Get one book by ID
    // =========================

    public BookDto getBookById(String id) {

        String url =
                "https://www.googleapis.com/books/v1/volumes/"
                        + id
                        + "?key=" + apiKey;


        try {

            String response =
                    restTemplate.getForObject(
                            url,
                            String.class
                    );


            ObjectMapper mapper =
                    new ObjectMapper();


            JsonNode root =
                    mapper.readTree(response);


            JsonNode volumeInfo =
                    root.path("volumeInfo");


            BookDto dto =
                    new BookDto();


            // ID
            dto.setId(
                    root.path("id")
                            .asText()
            );


            // Title
            dto.setTitle(
                    volumeInfo.path("title")
                            .asText("No Title")
            );


            // Author
            JsonNode authors =
                    volumeInfo.path("authors");

            if (authors.isArray() && authors.size() > 0) {

                dto.setAuthor(
                        authors.get(0)
                                .asText()
                );

            } else {

                dto.setAuthor("Unknown Author");
            }


            // Category
            JsonNode categories =
                    volumeInfo.path("categories");

            if (categories.isArray() && categories.size() > 0) {

                dto.setCategory(
                        categories.get(0)
                                .asText()
                );

            } else {

                dto.setCategory("Unknown");
            }


            // Rating
            dto.setRating(
                    volumeInfo.path("averageRating")
                            .asDouble(0.0)
            );


            // Ratings Count
            dto.setRatingsCount(
                    volumeInfo.path("ratingsCount")
                            .asInt(0)
            );


            // Published Date
            dto.setPublishedDate(
                    volumeInfo.path("publishedDate")
                            .asText("Unknown")
            );


            // Page Count
            dto.setPageCount(
                    volumeInfo.path("pageCount")
                            .asInt(0)
            );


            // Description
            String description =
                    volumeInfo.path("description")
                            .asText("No Description Available.");

            String cleanDescription =
                    Jsoup.parse(description).text();

            dto.setDescription(cleanDescription);

            // Image
            String image = "";

            if (volumeInfo.has("imageLinks")) {

                image =
                        volumeInfo.path("imageLinks")
                                .path("thumbnail")
                                .asText("");

                image =
                        image.replace(
                                "http://",
                                "https://"
                        );
            }


            if (image.isBlank()) {

                image =
                        "https://placehold.co/300x450?text=No+Cover";
            }


            dto.setImage(image);


            return dto;


        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
}