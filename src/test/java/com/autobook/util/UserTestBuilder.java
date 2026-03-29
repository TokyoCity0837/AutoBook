package com.autobook.util;

import com.autobook.Enum.PrivacyType;
import com.autobook.Enum.UserRole;
import com.autobook.Social.User.User;

public class UserTestBuilder {

    private Long id = 1L;
    private String username = "anton";
    private String visibleName = "Anton";
    private String email = "anton@gmail.com";
    private String password = "123456";
    private String bio = "Default bio";
    private String profileImage = "default.png";
    private UserRole role = UserRole.USER;
    private PrivacyType privacy = PrivacyType.PUBLIC;

    public UserTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public UserTestBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserTestBuilder withVisibleName(String visibleName) {
        this.visibleName = visibleName;
        return this;
    }

    public UserTestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserTestBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserTestBuilder withBio(String bio) {
        this.bio = bio;
        return this;
    }

    public UserTestBuilder withProfileImage(String profileImage) {
        this.profileImage = profileImage;
        return this;
    }

    public UserTestBuilder withRole(UserRole role) {
        this.role = role;
        return this;
    }

    public UserTestBuilder withPrivacy(PrivacyType privacy) {
        this.privacy = privacy;
        return this;
    }

    public User build() {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setVisibleName(visibleName);
        user.setEmail(email);
        user.setPassword(password);
        user.setBio(bio);
        user.setProfileImage(profileImage);
        user.setRole(role);
        user.setPrivacy(privacy);
        return user;
    }
}