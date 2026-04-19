package com.autobook.Social.User;

import com.autobook.Enum.PrivacyType;
import com.autobook.Enum.UserRole;
import com.autobook.Social.User.DTO.Request.UserRegisterRequest;
import com.autobook.Social.User.DTO.Request.UserUpdateRequest;
import com.autobook.Social.User.DTO.Response.UserCardResponse;
import com.autobook.Social.User.DTO.Response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserProfileResponse register(@RequestBody UserRegisterRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/profile/me")
    public UserProfileResponse getMyProfile(Principal principal) {
        if (principal == null) throw new com.autobook.Exception.UserNotFoundException("Not authenticated");
        return userService.getUserProfileByUsername(principal.getName());
    }

    @GetMapping("/profile/{id}")
    public UserProfileResponse getProfile(@PathVariable Long id) {
        return userService.getUserProfileById(id);
    }

    @GetMapping("/profile/username/{username}")
    public UserProfileResponse getProfileByUsername(@PathVariable String username) {
        return userService.getUserProfileByUsername(username);
    }

    @GetMapping
    public List<UserCardResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/role/{role}")
    public List<UserCardResponse> getUsersByRole(@PathVariable UserRole role) {
        return userService.getUsersByRole(role);
    }

    @GetMapping("/search")
    public List<UserCardResponse> searchUsers(@RequestParam String username) {
        return userService.searchUsersByUsername(username);
    }

    @GetMapping("/privacy/{privacy}")
    public List<UserCardResponse> getUsersByPrivacy(@PathVariable PrivacyType privacy) {
        return userService.getUsersByPrivacy(privacy);
    }

    @GetMapping("/privacy/{privacy}/count")
    public Long countUsersByPrivacy(@PathVariable PrivacyType privacy) {
        return userService.countUsersByPrivacy(privacy);
    }

    @PutMapping("/{id}")
    public UserProfileResponse updateProfile(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return userService.updateProfile(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }
}
