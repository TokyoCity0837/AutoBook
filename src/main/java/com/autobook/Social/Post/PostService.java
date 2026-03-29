package com.autobook.Social.Post;

import com.autobook.Enum.PostType;
import com.autobook.Exception.EmptyPostContentException;
import com.autobook.Exception.PostNotFoundException;
import com.autobook.Factory.PostFactory;
import com.autobook.Social.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostFactory postFactory;

    @Transactional
    public Post createPost(String content, User author, PostType postType) {
        validateContent(content);

        Post post = postFactory.create(content, author, postType);
        return postRepository.save(post);
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Post> getFeedPosts() {
        return postRepository.findByPostTypeOrderByCreatedAtDesc(PostType.FEED);
    }

    public List<Post> getProfilePosts(User author) {
        return postRepository.findByAuthorAndPostTypeOrderByCreatedAtDesc(author, PostType.PROFILE);
    }

    public List<Post> getPostsByAuthor(User author) {
        return postRepository.findByAuthorOrderByCreatedAtDesc(author);
    }

    public List<Post> getFeedPostsByAuthors(List<User> authors) {
        return postRepository.findByAuthorInAndPostTypeOrderByCreatedAtDesc(authors, PostType.FEED);
    }

    public Long countProfilePostsByAuthor(User author) {
        return postRepository.countByAuthorAndPostType(author, PostType.PROFILE);
    }

    @Transactional
    public Post updatePostContent(Long postId, String content) {
        validateContent(content);

        Post post = getPostById(postId);
        post.setContent(content);
        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = getPostById(postId);
        postRepository.delete(post);
    }

    @Transactional
    public void incrementLikeCount(Long postId) {
        getPostById(postId);
        postRepository.incrementLikeCount(postId);
    }

    @Transactional
    public void decrementLikeCount(Long postId) {
        getPostById(postId);
        postRepository.decrementLikeCount(postId);
    }

    @Transactional
    public void incrementCommentCount(Long postId) {
        getPostById(postId);
        postRepository.incrementCommentCount(postId);
    }

    @Transactional
    public void decrementCommentCount(Long postId) {
        getPostById(postId);
        postRepository.decrementCommentCount(postId);
    }

    @Transactional
    public void incrementRepostCount(Long postId) {
        getPostById(postId);
        postRepository.incrementRepostCount(postId);
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new EmptyPostContentException();
        }
    }
}