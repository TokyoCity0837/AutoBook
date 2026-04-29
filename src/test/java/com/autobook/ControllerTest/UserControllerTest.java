package com.autobook.ControllerTest;

import com.autobook.Enum.PrivacyType;
import com.autobook.Enum.UserRole;
import com.autobook.Social.User.DTO.Request.UserRegisterRequest;
import com.autobook.Social.User.DTO.Request.UserUpdateRequest;
import com.autobook.Social.User.DTO.Response.UserCardResponse;
import com.autobook.Social.User.DTO.Response.UserProfileResponse;
import com.autobook.Social.User.UserController;
import com.autobook.Social.User.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Principal principal;

    @InjectMocks
    private UserController userController;

    @Test
    void register() {
        UserRegisterRequest request = new UserRegisterRequest("Test user", "testuser", "test@test.com", "password");
        UserProfileResponse response = new UserProfileResponse(1L, "testuser", null, null, null, PrivacyType.PUBLIC,
                null, UserRole.USER, 0, 0, null, null, false, false);
        when(userService.createUser(request)).thenReturn(response);

        UserProfileResponse result = userController.register(request);

        assertEquals(1L, result.id());
        verify(userService).createUser(request);
    }

    @Test
    void getMyProfile() {
        when(principal.getName()).thenReturn("testuser");
        UserProfileResponse response = new UserProfileResponse(1L, "testuser", null, null, null, PrivacyType.PUBLIC,
                null, UserRole.USER, 0, 0, null, null, false, false);
        when(userService.getUserProfileByUsername("testuser")).thenReturn(response);

        UserProfileResponse result = userController.getMyProfile(principal);

        assertEquals(1L, result.id());
    }

    @Test
    void getProfileByUsername() {
        UserProfileResponse response = new UserProfileResponse(1L, "testuser", null, null, null, PrivacyType.PUBLIC,
                null, UserRole.USER, 0, 0, null, null, false, false);
        when(userService.getUserProfileByUsername("testuser")).thenReturn(response);

        UserProfileResponse result = userController.getProfileByUsername("testuser");
        assertEquals(1L, result.id());
    }

    @Test
    void getAllUsers() {
        when(userService.getAllUsers()).thenReturn(List.of());
        List<UserCardResponse> list = userController.getAllUsers();
        assertEquals(0, list.size());
    }

    @Test
    void getUsersByRole() {
        when(userService.getUsersByRole(UserRole.USER)).thenReturn(List.of());
        List<UserCardResponse> list = userController.getUsersByRole(UserRole.USER);
        assertEquals(0, list.size());
    }

    @Test
    void updateProfile() {
        UserUpdateRequest request = new UserUpdateRequest("name", "bio", "new_image_url", PrivacyType.PUBLIC,
                "username");
        userController.updateProfile(1L, request);
        verify(userService).updateProfile(1L, request);
    }

    @Test
    void deleteUser() {
        userController.deleteUser(1L);
        verify(userService).deleteUserById(1L);
    }

    @Test
    void getProfile() {
        UserProfileResponse response = new UserProfileResponse(1L, "testuser", null, null, null, PrivacyType.PUBLIC,
                null, UserRole.USER, 0, 0, null, null, false, false);
        when(userService.getUserProfileById(1L)).thenReturn(response);

        UserProfileResponse result = userController.getProfile(1L);
        assertEquals(1L, result.id());
    }

    @Test
    void searchUsers() {
        when(userService.searchUsersByUsername("test")).thenReturn(List.of());
        List<UserCardResponse> result = userController.searchUsers("test");
        assertEquals(0, result.size());
    }

    @Test
    void getUsersByPrivacy() {
        when(userService.getUsersByPrivacy(PrivacyType.PUBLIC)).thenReturn(List.of());
        List<UserCardResponse> result = userController.getUsersByPrivacy(PrivacyType.PUBLIC);
        assertEquals(0, result.size());
    }

    @Test
    void countUsersByPrivacy() {
        when(userService.countUsersByPrivacy(PrivacyType.PUBLIC)).thenReturn(42L);
        long result = userController.countUsersByPrivacy(PrivacyType.PUBLIC);
        assertEquals(42L, result);
    }
}

