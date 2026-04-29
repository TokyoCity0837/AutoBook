package com.autobook.ConfigTest;

import com.autobook.Enum.UserRole;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserRepository;
import com.autobook.config.OAuth2SuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private OAuth2User oAuth2User;

    @Mock
    private HttpSession session;

    @InjectMocks
    private OAuth2SuccessHandler successHandler;

    @Test
    void testOnAuthenticationSuccess() throws Exception {
        User user = new User();
        user.setUsername("oauthuser");
        user.setRole(UserRole.USER);

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("test@test.com");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(request.getSession(true)).thenReturn(session);

        successHandler.onAuthenticationSuccess(request, response, authentication);

        verify(response).sendRedirect("http://localhost:5173/");
        assertEquals("oauthuser", SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
