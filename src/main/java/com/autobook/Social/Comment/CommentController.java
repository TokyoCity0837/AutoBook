package com.autobook.Social.Comment;

import com.autobook.Social.Comment.DTO.Request.CreateCommentRequest;
import com.autobook.Social.Comment.DTO.Request.UpdateCommentRequest;
import com.autobook.Social.Comment.DTO.Response.CommentResponse;
import com.autobook.Social.Post.Post;
import com.autobook.Social.Post.PostRepository;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserRepository;
import com.autobook.Exception.PostNotFoundException;
import com.autobook.Exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private User getAuthenticatedUser(Principal principal) {
        if (principal == null) throw new UserNotFoundException("Principal is null");
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @PostMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(@PathVariable Long postId, @RequestBody CreateCommentRequest request, Principal principal) {
        User author = getAuthenticatedUser(principal);
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        return commentService.createComment(request, author, post);
    }

    @GetMapping("/{id}")
    public CommentResponse getCommentById(@PathVariable Long id) {
        return commentService.getCommentById(id);
    }

    @GetMapping("/post/{postId}")
    public List<CommentResponse> getCommentsByPost(@PathVariable Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        return commentService.getCommentsByPost(post);
    }

    @GetMapping("/user/{userId}")
    public List<CommentResponse> getCommentsByAuthor(@PathVariable Long userId) {
        User author = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return commentService.getCommentsByAuthor(author);
    }

    @PutMapping("/{id}")
    public CommentResponse updateComment(@PathVariable Long id, @RequestBody UpdateCommentRequest request) {
        return commentService.updateCommentContent(id, request);
    }

    @PutMapping("/{id}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void likeComment(@PathVariable Long id) {
        commentService.incrementLikeCount(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long id, Principal principal) {
        User author = getAuthenticatedUser(principal);
        commentService.deleteCommentByIdAndAuthor(id, author);
    }
}
