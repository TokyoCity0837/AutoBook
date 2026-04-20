package com.autobook.FactoryTest;

import com.autobook.Enum.PostType;
import com.autobook.Factory.PostFactory;
import com.autobook.util.UserTestBuilder;
import com.autobook.Social.User.User;
import com.autobook.Social.Post.Post;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PostFactoryTest {
    
    private PostFactory postFactory = new PostFactory();

    @Test
    void createPost_ok(){
        User author = new UserTestBuilder()
                                .withUsername("anton")
                                .build();

        Post post = postFactory.create("Hello guys", author, PostType.PROFILE, null);

        assertNotNull(post);
        assertEquals(author, post.getAuthor());
        assertEquals("Hello guys", post.getContent());
        assertEquals(PostType.PROFILE, post.getPostType());
    }

}
