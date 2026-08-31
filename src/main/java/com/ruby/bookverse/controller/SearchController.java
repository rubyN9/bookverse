package com.ruby.bookverse.controller;

import com.ruby.bookverse.service.BookSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    private final BookSearchService service;

    public SearchController(BookSearchService service) {
        this.service = service;
    }

    @GetMapping("/search")
    public String search(@RequestParam String q, Model model) {

        model.addAttribute("books", service.searchBooks(q));
        model.addAttribute("query", q);

        return "search";
    }

}