package com.autobook.Social.Comment;

import com.autobook.Exception.CommentNotFoundException;
import com.autobook.Exception.EmptyCommentContentException;
import com.autobook.Factory.CommentFactory;
import com.autobook.Social.Comment.DTO.Request.CreateCommentRequest;
import com.autobook.Social.Comment.DTO.Request.UpdateCommentRequest;
import com.autobook.Social.Comment.DTO.Response.CommentResponse;
import com.autobook.Social.Post.Post;
import com.autobook.Social.Post.PostRepository;
import com.autobook.Social.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentFactory commentFactory;
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;

    @Transactional
    public CommentResponse createComment(CreateCommentRequest request, User author, Post post) {
        validateContent(request.content());

        Comment comment = commentFactory.create(request.content(), author, post);
        Comment savedComment = commentRepository.save(comment);

        postRepository.incrementCommentCount(post.getId());

        return commentMapper.toResponse(savedComment);
    }

    public CommentResponse getCommentById(Long commentId) {
        Comment comment = findCommentById(commentId);
        return commentMapper.toResponse(comment);
    }

    public List<CommentResponse> getCommentsByPost(Post post) {
        return commentRepository.findByPostOrderByCreatedAtDesc(post)
                .stream()
                .map(commentMapper::toResponse)
                .toList();
    }

    public List<CommentResponse> getCommentsByAuthor(User author) {
        return commentRepository.findByAuthorOrderByCreatedAtDesc(author)
                .stream()
                .map(commentMapper::toResponse)
                .toList();
    }

    @Transactional
    public CommentResponse updateCommentContent(Long commentId, UpdateCommentRequest request) {
        validateContent(request.content());

        Comment comment = findCommentById(commentId);
        comment.setContent(request.content());

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toResponse(savedComment);
    }

    @Transactional
    public void deleteCommentById(Long commentId) {
        Comment comment = findCommentById(commentId);

        postRepository.decrementCommentCount(comment.getPost().getId());
        commentRepository.delete(comment);
    }

    @Transactional
    public void deleteCommentByIdAndAuthor(Long commentId, User author) {
        Comment comment = findCommentById(commentId);

        if (!comment.getAuthor().getId().equals(author.getId())) {
            throw new CommentNotFoundException(commentId);
        }

        postRepository.decrementCommentCount(comment.getPost().getId());
        commentRepository.delete(comment);
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new EmptyCommentContentException();
        }
    }
}