package com.autobook.config;

import com.autobook.Social.User.User;
import com.autobook.Social.User.UserRepository;
import com.autobook.Enum.PrivacyType;
import com.autobook.Enum.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oAuth2User = super.loadUser(request);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String googleId = oAuth2User.getAttribute("sub");

        userRepository.findByEmail(email).orElseGet(() -> {
            String username = generateUniqueUsername(name != null ? name : "user");
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(username);
            newUser.setVisibleName(name != null ? name : username);
            newUser.setPassword(null);
            newUser.setRole(UserRole.USER);
            newUser.setPrivacy(PrivacyType.PUBLIC);
            return userRepository.save(newUser);
        });

        return oAuth2User;
    }

    private String generateUniqueUsername(String displayName) {
        String base = displayName
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "_");

        if (!userRepository.existsByUsername(base)) return base;

        int i = 1;
        while (userRepository.existsByUsername(base + "_" + i)) {
            i++;
        }
        return base + "_" + i;
    }
}