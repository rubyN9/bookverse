package com.ruby.bookverse.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookDto {

    private String id;
    private String title;
    private String author;
    private String category;
    private Double rating;
    private Integer ratingsCount;
    private String publishedDate;
    private Integer pageCount;
    private String description;
    private String image;
}