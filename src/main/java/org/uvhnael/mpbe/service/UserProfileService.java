package org.uvhnael.mpbe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uvhnael.mpbe.model.UserProfile;
import org.uvhnael.mpbe.repository.UserProfileRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    
    private final UserProfileRepository userProfileRepository;
    
    public Optional<UserProfile> getProfileByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId);
    }
    
    public UserProfile saveProfile(UserProfile profile) {
        return userProfileRepository.save(profile);
    }
    
    public UserProfile updateProfile(Long userId, UserProfile profile) {
        UserProfile existingProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        existingProfile.setAge(profile.getAge());
        existingProfile.setGender(profile.getGender());
        existingProfile.setHeight(profile.getHeight());
        existingProfile.setWeight(profile.getWeight());
        existingProfile.setGoal(profile.getGoal());
        existingProfile.setActivityLevel(profile.getActivityLevel());
        existingProfile.setDietaryPreference(profile.getDietaryPreference());
        existingProfile.setAllergies(profile.getAllergies());
        existingProfile.setBudgetPerMeal(profile.getBudgetPerMeal());
        
        return userProfileRepository.save(existingProfile);
    }
}
