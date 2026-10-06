package com.ruby.bookverse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "saved_books",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "google_book_id",
                                "user_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class SavedBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "google_book_id",
            nullable = false
    )
    private String googleBookId;

    @Column(nullable = false)
    private String title;

    private String author;

    private String category;

    private Integer pages;

    private Double rating;

    @Column(length = 1000)
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(name = "reading_status")
    private BookListStatus readingStatus;

    @Column(nullable = false)
    private boolean favorite = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;
}