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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing post comments ({@link Comment}).
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentFactory commentFactory;
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;

    /**
     * Creates a new comment or a reply to an existing comment.
     *
     * @param request the request payload containing content and optional parent ID
     * @param author  the user who is writing the comment
     * @param post    the context post
     * @return the saved comment DTO
     * @throws CommentNotFoundException     if the parent comment ID is provided but
     *                                      invalid
     * @throws EmptyCommentContentException if the content is empty
     */
    @Transactional
    public CommentResponse createComment(CreateCommentRequest request, User author, Post post) {
        log.info("Creating new comment by user {} on post {}", author.getUsername(), post.getId());
        validateContent(request.content());

        Comment comment = commentFactory.create(request.content(), author, post);

        if (request.parentId() != null) {
            log.debug("Setting parent comment ID: {}", request.parentId());
            Comment parent = commentRepository.findById(request.parentId())
                    .orElseThrow(() -> {
                        log.error("Failed to find parent comment ID: {}", request.parentId());
                        return new CommentNotFoundException(request.parentId());
                    });
            comment.setParentComment(parent);
        }

        Comment savedComment = commentRepository.save(comment);

        log.debug("Incrementing comment count for post ID: {}", post.getId());
        postRepository.incrementCommentCount(post.getId());

        log.info("Comment successfully created with ID: {}", savedComment.getId());
        return commentMapper.toResponse(savedComment);
    }

    /**
     * Retrieves a comment by its primary identifier.
     *
     * @param commentId the ID of the comment
     * @return the comment response DTO
     */
    public CommentResponse getCommentById(Long commentId) {
        log.debug("Retrieving comment by ID: {}", commentId);
        Comment comment = findCommentById(commentId);
        return commentMapper.toResponse(comment);
    }

    /**
     * Retrieves and builds a nested comment tree for a specific post.
     *
     * @param post the target post
     * @return a list of root comments, each containing nested replies
     */
    public List<CommentResponse> getCommentsByPost(Post post) {
        log.debug("Fetching and building comment tree for post ID: {}", post.getId());
        List<Comment> allComments = commentRepository.findByPostOrderByCreatedAtDesc(post);
        return buildCommentTree(allComments);
    }

    private List<CommentResponse> buildCommentTree(List<Comment> allComments) {
        java.util.Map<Long, CommentResponse> dtoMap = new java.util.HashMap<>();
        List<CommentResponse> rootComments = new java.util.ArrayList<>();

        for (Comment c : allComments) {
            CommentResponse cr = commentMapper.toResponseLevel(c, new java.util.ArrayList<>());
            if (cr != null)
                dtoMap.put(cr.id(), cr);
        }

        for (CommentResponse cr : dtoMap.values()) {
            if (cr.parentId() != null) {
                CommentResponse parent = dtoMap.get(cr.parentId());
                if (parent != null) {
                    parent.replies().add(cr);
                } else {
                    rootComments.add(cr);
                }
            } else {
                rootComments.add(cr);
            }
        }

        rootComments.sort((a, b) -> b.createdAt().compareTo(a.createdAt()));

        for (CommentResponse cr : dtoMap.values()) {
            cr.replies().sort((a, b) -> a.createdAt().compareTo(b.createdAt()));
        }

        return rootComments;
    }

    public List<CommentResponse> getCommentsByAuthor(User author) {
        List<Comment> allComments = commentRepository.findByAuthorOrderByCreatedAtDesc(author);
        return buildCommentTree(allComments);
    }

    /**
     * Updates the content of an existing comment.
     *
     * @param commentId the ID of the comment to update
     * @param request   the payload containing the new content
     * @return the updated comment response DTO
     * @throws EmptyCommentContentException if the new content is invalid
     */
    @Transactional
    public CommentResponse updateCommentContent(Long commentId, UpdateCommentRequest request) {
        log.info("Updating content for comment ID: {}", commentId);
        validateContent(request.content());

        Comment comment = findCommentById(commentId);
        comment.setContent(request.content());

        Comment savedComment = commentRepository.save(comment);
        log.debug("Comment updated successfully");
        return commentMapper.toResponse(savedComment);
    }

    /**
     * Unconditionally deletes a comment from the database (e.g. by admin).
     *
     * @param commentId the ID of the comment
     */
    @Transactional
    public void deleteCommentById(Long commentId) {
        log.info("Admin deleting comment ID: {}", commentId);
        Comment comment = findCommentById(commentId);

        postRepository.decrementCommentCount(comment.getPost().getId());
        commentRepository.delete(comment);
        log.debug("Comment deleted successfully");
    }

    /**
     * Safely deletes a comment verifying the authorship beforehand.
     *
     * @param commentId the ID of the comment to delete
     * @param author    the user attempting deletion
     * @throws CommentNotFoundException if the comment doesn't belong to the given
     *                                  author
     */
    @Transactional
    public void deleteCommentByIdAndAuthor(Long commentId, User author) {
        log.info("Author {} attempting to delete comment ID {}", author.getUsername(), commentId);
        Comment comment = findCommentById(commentId);

        if (!comment.getAuthor().getId().equals(author.getId())) {
            log.error("Author verification failed: user {} does not own comment {}", author.getUsername(), commentId);
            throw new CommentNotFoundException(commentId);
        }

        postRepository.decrementCommentCount(comment.getPost().getId());
        commentRepository.delete(comment);
        log.debug("Comment successfully deleted by author");
    }

    @Transactional
    public void incrementLikeCount(Long commentId) {
        findCommentById(commentId);
        commentRepository.incrementLikeCount(commentId);
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("Comment not found for ID: {}", commentId);
                    return new CommentNotFoundException(commentId);
                });
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            log.error("Validation failed: Attempted to set empty comment content");
            throw new EmptyCommentContentException();
        }
    }
}