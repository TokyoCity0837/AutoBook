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
        
        if (request.parentId() != null) {
            Comment parent = commentRepository.findById(request.parentId())
                .orElseThrow(() -> new CommentNotFoundException(request.parentId()));
            comment.setParentComment(parent);
        }

        Comment savedComment = commentRepository.save(comment);

        postRepository.incrementCommentCount(post.getId());

        return commentMapper.toResponse(savedComment);
    }

    public CommentResponse getCommentById(Long commentId) {
        Comment comment = findCommentById(commentId);
        return commentMapper.toResponse(comment);
    }

    public List<CommentResponse> getCommentsByPost(Post post) {
        List<Comment> allComments = commentRepository.findByPostOrderByCreatedAtDesc(post);
        return buildCommentTree(allComments);
    }

    private List<CommentResponse> buildCommentTree(List<Comment> allComments) {
        java.util.Map<Long, CommentResponse> dtoMap = new java.util.HashMap<>();
        List<CommentResponse> rootComments = new java.util.ArrayList<>();
        
        // Map all entities to DTOs first
        for (Comment c : allComments) {
            CommentResponse cr = commentMapper.toResponseLevel(c, new java.util.ArrayList<>());
            if (cr != null) dtoMap.put(cr.id(), cr);
        }
        
        // Link children to parents
        for (CommentResponse cr : dtoMap.values()) {
            if (cr.parentId() != null) {
                CommentResponse parent = dtoMap.get(cr.parentId());
                if (parent != null) {
                    parent.replies().add(cr);
                } else {
                    rootComments.add(cr); // Parent not in this subset
                }
            } else {
                rootComments.add(cr);
            }
        }
        
        // Sort root comments by creation time descending
        rootComments.sort((a, b) -> b.createdAt().compareTo(a.createdAt()));
        
        // Sort replies ascending (oldest first)
        for (CommentResponse cr : dtoMap.values()) {
            cr.replies().sort((a, b) -> a.createdAt().compareTo(b.createdAt()));
        }
        
        return rootComments;
    }

    public List<CommentResponse> getCommentsByAuthor(User author) {
        List<Comment> allComments = commentRepository.findByAuthorOrderByCreatedAtDesc(author);
        return buildCommentTree(allComments);
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

    @Transactional
    public void incrementLikeCount(Long commentId) {
        findCommentById(commentId);
        commentRepository.incrementLikeCount(commentId);
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