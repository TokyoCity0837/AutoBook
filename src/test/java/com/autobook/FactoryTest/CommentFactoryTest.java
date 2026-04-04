package com.autobook.FactoryTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.autobook.Factory.CommentFactory;
import com.autobook.util.PostTestBuilder;
import com.autobook.util.UserTestBuilder;
import com.autobook.Social.Post.Post;
import com.autobook.Social.User.User;
import com.autobook.Social.Comment.Comment;


public class CommentFactoryTest {
    
    private CommentFactory commentFactory = new CommentFactory();

    @Test
    void createComment_ok(){
        User author = new UserTestBuilder().withId(5L).withUsername("anton").build();
        Post post = new PostTestBuilder().withAuthor(author).withContent("Hello!").build();

        Comment comment = commentFactory.create("Good Book!", author, post);

        assertNotNull(comment);
        assertEquals("anton", comment.getAuthor().getUsername());
        assertEquals("Hello!", comment.getPost().getContent());

    }

}
