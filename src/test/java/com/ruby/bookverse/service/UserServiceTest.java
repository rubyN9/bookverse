package com.ruby.bookverse.service;

import com.ruby.bookverse.entity.User;
import com.ruby.bookverse.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {

        userRepository = mock(UserRepository.class);

        userService = new UserService(userRepository);
    }

    @Test
    void registerUser_shouldRejectWeakPassword() {

        when(userRepository.findByEmail("ruby@example.com"))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.registerUser(
                                "Ruby",
                                "ruby@example.com",
                                "abc"
                        )
                );

        assertEquals(
                "Password must contain at least 8 characters.",
                exception.getMessage()
        );
    }

    @Test
    void registerUser_shouldRejectDuplicateEmail() {

        User existingUser = new User();

        existingUser.setEmail("ruby@example.com");

        when(userRepository.findByEmail("ruby@example.com"))
                .thenReturn(Optional.of(existingUser));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.registerUser(
                                "Ruby",
                                "ruby@example.com",
                                "Password1"
                        )
                );

        assertEquals(
                "An account with this email already exists.",
                exception.getMessage()
        );
    }

    @Test
    void registerUser_shouldEncryptPasswordAndSaveUser() {

        when(userRepository.findByEmail("ruby@example.com"))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser =
                userService.registerUser(
                        "Ruby",
                        "ruby@example.com",
                        "Password1"
                );

        assertEquals(
                "Ruby",
                savedUser.getName()
        );

        assertEquals(
                "ruby@example.com",
                savedUser.getEmail()
        );

        assertNotEquals(
                "Password1",
                savedUser.getPassword()
        );

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        assertTrue(
                encoder.matches(
                        "Password1",
                        savedUser.getPassword()
                )
        );

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void loginUser_shouldReturnUserWhenCredentialsAreCorrect() {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        User user = new User();

        user.setEmail("ruby@example.com");

        user.setPassword(
                encoder.encode("Password1")
        );

        when(userRepository.findByEmail("ruby@example.com"))
                .thenReturn(Optional.of(user));

        User result =
                userService.loginUser(
                        "ruby@example.com",
                        "Password1"
                );

        assertSame(
                user,
                result
        );
    }

    @Test
    void loginUser_shouldReturnNullWhenPasswordIsIncorrect() {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        User user = new User();

        user.setEmail("ruby@example.com");

        user.setPassword(
                encoder.encode("Password1")
        );

        when(userRepository.findByEmail("ruby@example.com"))
                .thenReturn(Optional.of(user));

        User result =
                userService.loginUser(
                        "ruby@example.com",
                        "WrongPassword1"
                );

        assertNull(result);
    }
}