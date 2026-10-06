package com.ruby.bookverse.dto;

import com.ruby.bookverse.entity.BookListStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LibraryBookDto {

    private String id;
    private String title;
    private String author;
    private String category;
    private Integer pages;
    private String image;
    private BookListStatus readingStatus;
    private boolean favorite;
}