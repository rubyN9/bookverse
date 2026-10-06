package com.ruby.bookverse.controller;

import com.ruby.bookverse.exception.GlobalExceptionHandler;
import com.ruby.bookverse.service.BookSearchService;
import com.ruby.bookverse.service.SavedBookService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookRestController.class)
@Import(GlobalExceptionHandler.class)
class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookSearchService bookSearchService;

    @MockitoBean
    private SavedBookService savedBookService;

    @Test
    void searchBooks_shouldReturnBadRequestWhenQueryIsEmpty()
            throws Exception {

        mockMvc.perform(
                        get("/api/books/search")
                                .param("q", "")
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.status").value(400)
                )
                .andExpect(
                        jsonPath("$.error").value("Bad Request")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("Search query cannot be empty")
                );
    }
}