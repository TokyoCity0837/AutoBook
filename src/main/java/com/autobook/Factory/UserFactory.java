package com.autobook.Factory;

import org.springframework.stereotype.Component;
import com.autobook.Social.User.User;


/**
 * Factory component responsible for constructing new {@link User} instances.
 * <p>
 * Centralises default field initialisation (e.g. empty bio, null profile image)
 * so that service classes do not contain object-construction logic.
 * Implements the Factory design pattern.
 */
@Component
public class UserFactory {

    public User create(
            String username,
            String visibleName,
            String email,
            String encodedPassword
        ) {
        User user = new User();
        user.setUsername(username);
        user.setVisibleName(visibleName);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setBio("");
        user.setProfileImage(null);
        return user;
    }
}
