package com.autobook.ControllerTest;

import com.autobook.Social.User.AuthController;
import com.autobook.Social.User.DTO.Request.UserRegisterRequest;
import com.autobook.Social.User.DTO.Response.UserProfileResponse;
import com.autobook.Social.User.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_ok() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        Map<String, String> creds = Map.of("email", "test@test.com", "password", "pass");

        ResponseEntity<?> response = authController.login(creds, request);

        assertEquals(200, response.getStatusCode().value());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(session).setAttribute(anyString(), any());
    }

    @Test
    void register_ok() {
        UserRegisterRequest req = new UserRegisterRequest("username", "name", "email@e.com", "pass");
        UserProfileResponse profile = mock(UserProfileResponse.class);
        when(userService.createUser(req)).thenReturn(profile);

        UserProfileResponse response = authController.register(req);

        assertEquals(profile, response);
    }

    @Test
    void logout_ok() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);

        authController.logout(request);

        verify(session).invalidate();
    }
}
