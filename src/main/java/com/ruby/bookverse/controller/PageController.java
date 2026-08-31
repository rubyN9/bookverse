package com.ruby.bookverse.controller;

import com.ruby.bookverse.service.BookSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    private final BookSearchService service;

    public PageController(BookSearchService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) String q,
            Model model) {

        if (q != null && !q.isBlank()) {
            model.addAttribute("books", service.searchBooks(q));
            model.addAttribute("query", q);
        }

        return "home";
    }
}