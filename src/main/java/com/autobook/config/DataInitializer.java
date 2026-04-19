package com.autobook.config;

import com.autobook.Enum.PostType;
import com.autobook.Enum.PrivacyType;
import com.autobook.Enum.UserRole;
import com.autobook.Library.Book.Book;
import com.autobook.Library.Book.BookRepository;
import com.autobook.Library.Chapter.Chapter;
import com.autobook.Library.Chapter.ChapterRepository;
import com.autobook.Social.Post.Post;
import com.autobook.Social.Post.PostRepository;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final BookRepository bookRepository;
    private final ChapterRepository chapterRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println("Initializing Mock Data for AutoBook...");

            // 1. Create Users
            User u1 = createUser("andrii_dosyn", "Andrii Dosyn", "andrii@example.com", "password123");
            User u2 = createUser("jane_austen", "Jane Austen", "jane@example.com", "password123");
            User u3 = createUser("george_martin", "George R.R. Martin", "george@example.com", "password123");

            userRepository.saveAll(List.of(u1, u2, u3));

            // 2. Create Posts
            createPost(u1, "Just completely refactored the UI for AutoBook, feels amazing! React is super fast.", 120, 15);
            createPost(u2, "It is a truth universally acknowledged, that a single man in possession of a good fortune, must be in want of a wife.", 2400, 150);
            createPost(u3, "Still working on the next book... Please don't rush me. #writing", 5000, 1500);

            // 3. Create Books
            Book b1 = createBook(u1, "My First Novel", "A story about writing code.", "Programming", PrivacyType.PUBLIC);
            Book b2 = createBook(u2, "Pride and Prejudice", "A classic romance.", "Romance", PrivacyType.PUBLIC);
            bookRepository.saveAll(List.of(b1, b2));

            // 4. Create Chapters
            createChapter(b1, "Chapter 1: The Beginning", "<p>It was a dark and stormy night when the server crashed...</p>");
            createChapter(b2, "Chapter 1: Arrival", "<p>Mr. Bingley has arrived at Netherfield.</p>");

            System.out.println("Mock Data Initialization Complete.");
        }
    }

    private User createUser(String username, String visibleName, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setVisibleName(visibleName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.USER);
        user.setPrivacy(PrivacyType.PUBLIC);
        user.setBio("Hello, I'm new here!");
        return user;
    }

    private void createPost(User author, String content, int likes, int comments) {
        Post post = new Post();
        post.setAuthor(author);
        post.setContent(content);
        post.setPostType(PostType.FEED);
        post.setLikeCount(likes);
        post.setCommentCount(comments);
        postRepository.save(post);
    }

    private Book createBook(User author, String title, String description, String genre, PrivacyType privacy) {
        Book book = new Book();
        book.setAuthor(author);
        book.setTitle(title);
        book.setDescription(description);
        book.setGenre(genre);
        book.setPrivacy(privacy);
        return book;
    }

    private void createChapter(Book book, String title, String content) {
        Chapter chapter = new Chapter();
        chapter.setBook(book);
        chapter.setTitle(title);
        chapter.setContent(content);
        chapterRepository.save(chapter);
    }
}
