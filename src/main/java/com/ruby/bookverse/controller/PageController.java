package com.ruby.bookverse.controller;

import com.ruby.bookverse.service.BookSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    private final BookSearchService bookSearchService;

    public PageController(
            BookSearchService bookSearchService) {

        this.bookSearchService = bookSearchService;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) String q,
            Model model) {

        if (q != null && !q.isBlank()) {
            String query = q.trim();

            model.addAttribute(
                    "books",
                    bookSearchService.searchBooks(query)
            );

            model.addAttribute(
                    "query",
                    query
            );
        }
        return "home";
    }
}