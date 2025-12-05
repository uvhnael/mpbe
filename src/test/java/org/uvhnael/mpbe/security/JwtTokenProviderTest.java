package org.uvhnael.mpbe.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {
    
    private JwtTokenProvider jwtTokenProvider;
    
    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        // 512 bits = 64 bytes minimum for HS512
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "testSecretKeyForJWTTokenGenerationAndValidation1234567890ABCDEFGH");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", 3600000L);
    }
    
    @Test
    void generateToken_Success() {
        // Act
        String token = jwtTokenProvider.generateToken("test@example.com");
        
        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }
    
    @Test
    void getEmailFromToken_Success() {
        // Arrange
        String token = jwtTokenProvider.generateToken("test@example.com");
        
        // Act
        String email = jwtTokenProvider.getEmailFromToken(token);
        
        // Assert
        assertEquals("test@example.com", email);
    }
    
    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        // Arrange
        String token = jwtTokenProvider.generateToken("test@example.com");
        
        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);
        
        // Assert
        assertTrue(isValid);
    }
    
    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        // Arrange
        String invalidToken = "invalid.token.here";
        
        // Act
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);
        
        // Assert
        assertFalse(isValid);
    }
    
    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        // Arrange - Create provider with immediate expiration
        JwtTokenProvider expiredProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(expiredProvider, "jwtSecret", "testSecretKeyForJWTTokenGenerationAndValidation1234567890ABCDEFGH");
        ReflectionTestUtils.setField(expiredProvider, "jwtExpirationMs", -1L);
        
        String expiredToken = expiredProvider.generateToken("test@example.com");
        
        // Act
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);
        
        // Assert
        assertFalse(isValid);
    }
}
