package com.ruby.bookverse.controller;

import com.ruby.bookverse.entity.User;
import com.ruby.bookverse.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;


    public AuthController(UserService userService) {
        this.userService = userService;
    }


    // =========================
    // REGISTER
    // =========================

    @GetMapping("/register")
    public String showRegisterPage() {

        return "register";
    }


    @PostMapping("/register")
    public String registerUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password) {

        userService.registerUser(
                name,
                email,
                password
        );

        return "redirect:/login";
    }


    // =========================
    // LOGIN
    // =========================

    @GetMapping("/login")
    public String showLoginPage() {

        return "login";
    }


    @PostMapping("/login")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session) {

        User user =
                userService.loginUser(
                        email,
                        password
                );


        if (user != null) {

            session.setAttribute(
                    "loggedInUser",
                    user
            );

            return "redirect:/";
        }


        return "redirect:/login";
    }


    // =========================
    // LOGOUT
    // =========================

    @GetMapping("/logout")
    public String logout(
            HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }
}