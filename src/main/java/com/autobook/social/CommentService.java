package com.autobook.social;

import com.autobook.book.Book;
import com.autobook.user.User;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository){
        this.commentRepository = commentRepository;
    }

    public Comment createComment(Book book, User user, String content){
        Comment comment = new Comment();
        comment.setBook(book);
        comment.setUser(user);
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    public Comment getCommentById(Long id){
        return commentRepository.findById(id)
            .orElseThrow(() ->  new RuntimeException("Comment is not found"));
    }

    public List<Comment> findAllCommentsByBook(Book book){
        return commentRepository.findByBookOrderByCreatedAtDesc(book);
    }

    public List<Comment> findAllCommentsByUser(User user){
        return commentRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Long getCommentCountByBook(Book book){
        return commentRepository.countByBook(book);
    }

    public Comment updateComment(Long id, String newContent) {
        Comment comment = getCommentById(id);
        comment.setContent(newContent);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long id){
        commentRepository.deleteById(id);
    }

    public List<Comment> searchComments(String searchText) {
        return commentRepository.findByContentContainingIgnoreCase(searchText);
    }

    public List<Comment> searchCommentsInBook(Book book, String searchText) {
        return commentRepository.findByBookAndContentContainingIgnoreCase(book, searchText);
    }
}