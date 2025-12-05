package org.uvhnael.mpbe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uvhnael.mpbe.model.User;
import org.uvhnael.mpbe.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
    
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
