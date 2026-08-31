package com.ruby.bookverse.service;

import com.ruby.bookverse.entity.User;
import com.ruby.bookverse.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // Register
    public User registerUser(
            String name,
            String email,
            String password) {

        User user = new User();

        user.setName(name);
        user.setEmail(email);

        String encodedPassword =
                passwordEncoder.encode(password);

        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }


    // Login
    public User loginUser(
            String email,
            String password) {

        Optional<User> optionalUser =
                userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        boolean passwordMatches =
                passwordEncoder.matches(
                        password,
                        user.getPassword()
                );

        if (passwordMatches) {
            return user;
        }

        return null;
    }
}