package com.autobook.Library.BookComment;

import com.autobook.Exception.CommentNotFoundException;
import com.autobook.Exception.EmptyCommentContentException;
import com.autobook.Library.Book.Book;
import com.autobook.Library.BookComment.DTO.Request.CreateBookCommentRequest;
import com.autobook.Library.BookComment.DTO.Response.BookCommentResponse;
import com.autobook.Social.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookCommentService {

    private final BookCommentRepository bookCommentRepository;
    private final BookCommentMapper bookCommentMapper;

    @Transactional
    public BookCommentResponse createComment(CreateBookCommentRequest request, User author, Book book) {
        validateContent(request.content());

        BookComment comment = new BookComment();
        comment.setContent(request.content());
        comment.setAuthor(author);
        comment.setBook(book);

        if (request.parentId() != null) {
            BookComment parent = bookCommentRepository.findById(request.parentId())
                    .orElseThrow(() -> new CommentNotFoundException(request.parentId()));
            comment.setParentComment(parent);
        }

        BookComment savedComment = bookCommentRepository.save(comment);
        return bookCommentMapper.toResponse(savedComment);
    }

    public List<BookCommentResponse> getCommentsByBook(Book book) {
        List<BookComment> allComments = bookCommentRepository.findByBookOrderByCreatedAtDesc(book);
        return buildCommentTree(allComments);
    }

    private List<BookCommentResponse> buildCommentTree(List<BookComment> allComments) {
        java.util.Map<Long, BookCommentResponse> dtoMap = new java.util.HashMap<>();
        List<BookCommentResponse> rootComments = new java.util.ArrayList<>();

        for (BookComment c : allComments) {
            BookCommentResponse cr = bookCommentMapper.toResponseLevel(c, new java.util.ArrayList<>());
            if (cr != null) dtoMap.put(cr.id(), cr);
        }

        for (BookCommentResponse cr : dtoMap.values()) {
            if (cr.parentId() != null) {
                BookCommentResponse parent = dtoMap.get(cr.parentId());
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
        for (BookCommentResponse cr : dtoMap.values()) {
            cr.replies().sort((a, b) -> a.createdAt().compareTo(b.createdAt()));
        }

        return rootComments;
    }

    @Transactional
    public void deleteCommentByIdAndAuthor(Long commentId, User author) {
        BookComment comment = bookCommentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        if (!comment.getAuthor().getId().equals(author.getId())) {
            throw new CommentNotFoundException(commentId);
        }

        bookCommentRepository.delete(comment);
    }

    @Transactional
    public void incrementLikeCount(Long commentId) {
        bookCommentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(commentId));
        bookCommentRepository.incrementLikeCount(commentId);
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new EmptyCommentContentException();
        }
    }
}
