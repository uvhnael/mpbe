package org.uvhnael.mpbe.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.uvhnael.mpbe.model.User;
import org.uvhnael.mpbe.model.UserProfile;
import org.uvhnael.mpbe.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private AuthService authService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFullName("Test User");
        
        UserProfile profile = new UserProfile();
        profile.setId(1L);
        profile.setUser(testUser);
        testUser.setUserProfile(profile);
    }
    
    @Test
    void register_Success() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // Act
        User result = authService.register("test@example.com", "password123", "Test User");
        
        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void register_EmailAlreadyExists_ThrowsException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            authService.register("test@example.com", "password123", "Test User")
        );
        
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void authenticate_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        
        // Act
        User result = authService.authenticate("test@example.com", "password123");
        
        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }
    
    @Test
    void authenticate_InvalidCredentials_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            authService.authenticate("test@example.com", "wrongPassword")
        );
    }
    
    @Test
    void authenticate_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            authService.authenticate("notfound@example.com", "password123")
        );
    }
}
