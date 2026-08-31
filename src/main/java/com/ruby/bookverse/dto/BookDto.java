package com.ruby.bookverse.dto;

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


    public BookDto() {
    }


    public BookDto(String id,
                   String title,
                   String author,
                   String category,
                   Double rating,
                   Integer ratingsCount,
                   String publishedDate,
                   Integer pageCount,
                   String description,
                   String image) {

        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.rating = rating;
        this.ratingsCount = ratingsCount;
        this.publishedDate = publishedDate;
        this.pageCount = pageCount;
        this.description = description;
        this.image = image;
    }


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public Double getRating() {
        return rating;
    }

    public Integer getRatingsCount() {
        return ratingsCount;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setRatingsCount(Integer ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }
}