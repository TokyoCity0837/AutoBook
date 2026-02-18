package com.autobook.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable long id) {

        User foundUser = userService.getUserById(id);
        return ResponseEntity.ok(foundUser);

    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {

        User foundUser = userService.getUserByUsername(username);
        return ResponseEntity.ok(foundUser);

    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<User> updateUSer(@PathVariable long userId, @RequestBody User updatedUser) {

        User newUser = userService.updateUser(userId, updatedUser);
        return ResponseEntity.ok(newUser);

    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable long userId){

        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();

    }


    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {

    List<User> users = userService.searchUsersByUsername(query);
    return ResponseEntity.ok(users);

    }  

    @PutMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long userId,
            @RequestParam String newPassword) {
        userService.changePassword(userId, newPassword);
        return ResponseEntity.ok().build();
    }
}
