package com.ruby.bookverse.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "favorite_books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String googleBookId;

    private String title;

    private String author;

    private String genre;

    private int pages;

    private Double rating;

    @Column(length = 1000)
    private String image;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}