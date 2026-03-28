package com.autobook.Factory;

import com.autobook.Enum.PrivacyType;
import com.autobook.Enum.UserRole;
import org.springframework.stereotype.Component;
import com.autobook.Social.User.User;


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
        user.setRole(UserRole.USER);
        user.setPrivacy(PrivacyType.PUBLIC);
        user.setBio("");
        user.setProfileImage(null);
        return user;
    }
}
