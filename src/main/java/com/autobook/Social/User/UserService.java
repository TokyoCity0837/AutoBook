package com.autobook.Social.User;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.autobook.Enum.PrivacyType;
import com.autobook.Enum.UserRole;
import com.autobook.Exception.EmailAlreadyInUseException;
import com.autobook.Exception.UserNotFoundException;
import com.autobook.Exception.UsernameAlreadyExistsException;
import com.autobook.Factory.UserFactory;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserFactory userFactory;

    @Transactional
    public User createUser(User rqUser) {
        if (userRepository.existsByEmail(rqUser.getEmail())) {
            throw new EmailAlreadyInUseException(rqUser.getEmail());
        }

        if (userRepository.existsByUsername(rqUser.getUsername())) {
            throw new UsernameAlreadyExistsException(rqUser.getUsername());
        }

        String encodedPassword = passwordEncoder.encode(rqUser.getPassword());

        User user = userFactory.create(
                rqUser.getUsername(),
                rqUser.getVisibleName(),
                rqUser.getEmail(),
                encodedPassword
        );

        return userRepository.save(user);
    }

    public User getUserById(long rqId) {
        return userRepository.findById(rqId)
                .orElseThrow(() -> new UserNotFoundException("User with provided ID was not found"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with provided username was not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with provided email was not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    public List<User> searchUsersByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
    }

    public List<User> getUsersByPrivacy(PrivacyType privacy) {
        return userRepository.findByPrivacy(privacy);
    }

    public Long countUsersByPrivacy(PrivacyType privacy) {
        return userRepository.countByPrivacy(privacy);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User updateBio(long userId, String bio) {
        User user = getUserById(userId);
        user.setBio(bio);
        return userRepository.save(user);
    }

    @Transactional
    public User updateVisibleName(long userId, String visibleName) {
        User user = getUserById(userId);
        user.setVisibleName(visibleName);
        return userRepository.save(user);
    }

    @Transactional
    public User updatePrivacy(long userId, PrivacyType privacy) {
        User user = getUserById(userId);
        user.setPrivacy(privacy);
        return userRepository.save(user);
    }

    @Transactional
    public User updateProfileImage(long userId, String profileImage) {
        User user = getUserById(userId);
        user.setProfileImage(profileImage);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUserById(long userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }
}