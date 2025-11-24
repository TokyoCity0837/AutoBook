package com.autobook.user;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import com.autobook.entity.PrivacyType;


public interface UserRepository extends JpaRepository<User, Long>{

Optional<User> findByUsername(String username);
    
Optional<User> findByEmail(String email);

Boolean existsByUsername(String username);

Boolean existsByEmail(String email);

List<User> findByUsernameContainingIgnoreCase(String username);

List<User> findByPrivacy(PrivacyType privacy);

Long countByPrivacy(PrivacyType privacy);

} 
    

