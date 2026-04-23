package com.autobook.Social.Post;

import com.autobook.Social.Post.DTO.Request.CreatePostRequest;
import com.autobook.Social.Post.DTO.Request.UpdatePostRequest;
import com.autobook.Social.Post.DTO.Response.PostDetailsResponse;
import com.autobook.Social.Post.DTO.Response.PostResponse;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserRepository;
import com.autobook.Exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserRepository userRepository;

    private User getAuthenticatedUser(Principal principal) {
        if (principal == null) throw new UserNotFoundException("Principal is null.");
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostDetailsResponse createPost(@RequestBody CreatePostRequest request, Principal principal) {
        return postService.createPost(request, getAuthenticatedUser(principal));
    }

    @GetMapping("/{id}")
    public PostDetailsResponse getPost(@PathVariable Long id, Principal principal) {
        return postService.getPostById(id, getAuthenticatedUser(principal));
    }

    @GetMapping
    public List<PostResponse> getAllPosts(Principal principal) {
        return postService.getAllPosts(getAuthenticatedUser(principal));
    }

    @GetMapping("/feed")
    public List<PostResponse> getFeedPosts(Principal principal) {
        return postService.getFeedPosts(getAuthenticatedUser(principal));
    }

    @GetMapping("/profile")
    public List<PostResponse> getProfilePosts(Principal principal) {
        User user = getAuthenticatedUser(principal);
        return postService.getProfilePosts(user, user);
    }

    @GetMapping("/user/{userId}")
    public List<PostResponse> getPostsByUser(@PathVariable Long userId, Principal principal) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return postService.getPostsByAuthor(author, getAuthenticatedUser(principal));
    }

    @PutMapping("/{id}")
    public PostDetailsResponse updatePost(@PathVariable Long id, @RequestBody UpdatePostRequest request, Principal principal) {
        return postService.updatePostContent(id, request, getAuthenticatedUser(principal));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }

    @PostMapping("/{id}/like")
    public boolean toggleLike(@PathVariable Long id, Principal principal) {
        return postService.toggleLike(id, getAuthenticatedUser(principal));
    }

    @PostMapping("/{id}/repost")
    public boolean toggleRepost(@PathVariable Long id, Principal principal) {
        return postService.toggleRepost(id, getAuthenticatedUser(principal));
    }
}