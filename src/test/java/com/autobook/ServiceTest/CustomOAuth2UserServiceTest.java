package com.autobook.ServiceTest;

import com.autobook.Social.User.UserRepository;
import com.autobook.config.CustomOAuth2UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OAuth2UserRequest request;

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    @Test
    void generateUniqueUsername_ok() throws Exception {
        Method method = CustomOAuth2UserService.class.getDeclaredMethod("generateUniqueUsername", String.class);
        method.setAccessible(true);

        when(userRepository.existsByUsername("anton")).thenReturn(false);
        String name = (String) method.invoke(customOAuth2UserService, "Anton");
        assertEquals("anton", name);

        when(userRepository.existsByUsername("test_user")).thenReturn(true);
        when(userRepository.existsByUsername("test_user_1")).thenReturn(true);
        when(userRepository.existsByUsername("test_user_2")).thenReturn(false);
        
        String name2 = (String) method.invoke(customOAuth2UserService, "Test User");
        assertEquals("test_user_2", name2);
    }
}
