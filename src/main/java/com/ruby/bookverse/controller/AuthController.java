package com.ruby.bookverse.controller;

import com.ruby.bookverse.entity.User;
import com.ruby.bookverse.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterPage(
            HttpSession session) {

        if (getLoggedInUser(session) != null) {
            return "redirect:/";
        }

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        try {
            User user = userService.registerUser(
                    name,
                    email,
                    password
            );

            session.setAttribute(
                    "loggedInUser",
                    user
            );

            return "redirect:/";

        } catch (IllegalArgumentException e) {
            model.addAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            model.addAttribute("name", name);
            model.addAttribute("email", email);

            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginPage(
            HttpSession session) {

        if (getLoggedInUser(session) != null) {
            return "redirect:/";
        }

        return "login";
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        User user = userService.loginUser(
                email,
                password
        );

        if (user == null) {
            model.addAttribute(
                    "errorMessage",
                    "Invalid email or password."
            );

            model.addAttribute("email", email);

            return "login";
        }

        session.setAttribute(
                "loggedInUser",
                user
        );

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(
            HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }

    private User getLoggedInUser(
            HttpSession session) {

        return (User) session.getAttribute(
                "loggedInUser"
        );
    }
}