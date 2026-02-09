package com.autobook.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(User entity) {
        return userRepository.save(entity);
    }

    public User createUser(User user){
        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("Email is been already in use");
        }

        if(userRepository.existsByUsername(user.getUsername())){
            throw new RuntimeException("Username already exists");
        }
        return userRepository.save(user);
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User is not found"));
    }

    public User getUserByUsername(String username){
        return userRepository.findByEmail(username)
        .orElseThrow(() -> new RuntimeException("User is not found"));
}

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User is not found"));
}

    public List <User> getAllUsers(){
        return userRepository.findAll();
    }

    public User updateUser(Long id, User updatedUser){
        User existing = getUserById(id);
        existing.setBio(updatedUser.getBio());
        existing.setProfileImage(updatedUser.getProfileImage());
        existing.setPrivacy(updatedUser.getPrivacy());
        return userRepository.save(existing);
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

    public List<User> searchUsersByUsername(String usernamePart){
        return userRepository.findByUsernameContainingIgnoreCase(usernamePart); 
    }

    public void changePassword(Long userId, String password){
        User user = getUserById(userId);
        user.setPassword(password);
        userRepository.save(user);
    }

}
