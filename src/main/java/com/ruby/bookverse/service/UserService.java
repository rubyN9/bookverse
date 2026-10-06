package com.ruby.bookverse.service;

import com.ruby.bookverse.entity.User;
import com.ruby.bookverse.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User registerUser(
            String name,
            String email,
            String password) {

        String cleanName =
                name == null
                        ? ""
                        : name.trim();

        String cleanEmail =
                email == null
                        ? ""
                        : email.trim().toLowerCase();

        validateName(cleanName);
        validateEmail(cleanEmail);
        validatePassword(password);

        if (userRepository.findByEmail(cleanEmail).isPresent()) {
            throw new IllegalArgumentException(
                    "An account with this email already exists."
            );
        }

        User user = new User();

        user.setName(cleanName);
        user.setEmail(cleanEmail);

        user.setPassword(
                passwordEncoder.encode(password)
        );

        return userRepository.save(user);
    }

    public User loginUser(
            String email,
            String password) {

        if (email == null || password == null) {
            return null;
        }

        String cleanEmail =
                email.trim().toLowerCase();

        Optional<User> optionalUser =
                userRepository.findByEmail(cleanEmail);

        if (optionalUser.isEmpty()) {
            return null;
        }

        User user =
                optionalUser.get();

        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            return null;
        }

        return user;
    }

    private void validateName(
            String name) {

        if (name.isBlank()) {
            throw new IllegalArgumentException(
                    "Name is required."
            );
        }

        if (name.length() < 2) {
            throw new IllegalArgumentException(
                    "Name must contain at least 2 characters."
            );
        }
    }

    private void validateEmail(
            String email) {

        if (email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required."
            );
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )) {
            throw new IllegalArgumentException(
                    "Please enter a valid email address."
            );
        }
    }

    private void validatePassword(
            String password) {

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Password is required."
            );
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters."
            );
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one uppercase letter."
            );
        }

        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one lowercase letter."
            );
        }

        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one number."
            );
        }
    }
}