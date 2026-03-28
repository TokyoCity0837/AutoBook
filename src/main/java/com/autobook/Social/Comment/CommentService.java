package com.autobook.Social.Comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.autobook.Exception.CommentNotFoundException;
import com.autobook.Exception.EmptyCommentContentException;
import com.autobook.Factory.CommentFactory;
import com.autobook.Social.Post.Post;
import com.autobook.Social.Post.PostRepository;
import com.autobook.Social.User.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentFactory commentFactory;
    private final PostRepository postRepository;

    @Transactional
    public Comment createComment(String content, User author, Post post) {
        validateContent(content);

        Comment comment = commentFactory.create(content, author, post);
        Comment savedComment = commentRepository.save(comment);

        postRepository.incrementCommentCount(post.getId());

        return savedComment;
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    public List<Comment> getCommentsByPost(Post post) {
        return commentRepository.findByPostOrderByCreatedAtDesc(post);
    }

    public List<Comment> getCommentsByAuthor(User author) {
        return commentRepository.findByAuthorOrderByCreatedAtDesc(author);
    }

    @Transactional
    public Comment updateCommentContent(Long commentId, String content) {
        validateContent(content);

        Comment comment = getCommentById(commentId);
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteCommentById(Long commentId) {
        Comment comment = getCommentById(commentId);

        postRepository.decrementCommentCount(comment.getPost().getId());
        commentRepository.delete(comment);
    }

    @Transactional
    public void deleteCommentByIdAndAuthor(Long commentId, User author) {
        Comment comment = getCommentById(commentId);

        if (!comment.getAuthor().getId().equals(author.getId())) {
            throw new CommentNotFoundException(commentId);
        }

        postRepository.decrementCommentCount(comment.getPost().getId());
        commentRepository.delete(comment);
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new EmptyCommentContentException();
        }
    }
}