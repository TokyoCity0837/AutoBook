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
        if (principal == null) throw new UserNotFoundException("Principal is null. User must be authenticated.");
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostDetailsResponse createPost(@RequestBody CreatePostRequest request, Principal principal) {
        User author = getAuthenticatedUser(principal);
        return postService.createPost(request, author);
    }

    @GetMapping("/{id}")
    public PostDetailsResponse getPost(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @GetMapping
    public List<PostResponse> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/feed")
    public List<PostResponse> getFeedPosts() {
        return postService.getFeedPosts();
    }

    @GetMapping("/profile")
    public List<PostResponse> getProfilePosts(Principal principal) {
        User author = getAuthenticatedUser(principal);
        return postService.getProfilePosts(author);
    }
    
    @GetMapping("/user/{userId}")
    public List<PostResponse> getPostsByUser(@PathVariable Long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return postService.getPostsByAuthor(author);
    }

    @PutMapping("/{id}")
    public PostDetailsResponse updatePost(@PathVariable Long id, @RequestBody UpdatePostRequest request) {
        return postService.updatePostContent(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }

    @PutMapping("/{id}/like")
    public void incrementLike(@PathVariable Long id) {
        postService.incrementLikeCount(id);
    }

    @DeleteMapping("/{id}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void decrementLike(@PathVariable Long id) {
        postService.decrementLikeCount(id);
    }

    @PutMapping("/{id}/repost")
    public void incrementRepost(@PathVariable Long id) {
        postService.incrementRepostCount(id);
    }
}
