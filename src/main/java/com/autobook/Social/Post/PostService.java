package com.autobook.Social.Post;

import com.autobook.Enum.PostType;
import com.autobook.Exception.EmptyPostContentException;
import com.autobook.Exception.PostNotFoundException;
import com.autobook.Factory.PostFactory;
import com.autobook.Social.Post.DTO.Request.CreatePostRequest;
import com.autobook.Social.Post.DTO.Request.UpdatePostRequest;
import com.autobook.Social.Post.DTO.Response.PostDetailsResponse;
import com.autobook.Social.Post.DTO.Response.PostResponse;
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
    private final PostMapper postMapper;

    @Transactional
    public PostDetailsResponse createPost(CreatePostRequest request, User author) {
        validateContent(request.content());

        Post post = postFactory.create(
                request.content(),
                author,
                request.postType()
        );

        Post savedPost = postRepository.save(post);
        return postMapper.toDetailsResponse(savedPost);
    }

    public PostDetailsResponse getPostById(Long postId) {
        Post post = findPostById(postId);
        return postMapper.toDetailsResponse(post);
    }

    public List<PostResponse> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(postMapper::toResponse)
                .toList();
    }

    public List<PostResponse> getFeedPosts() {
        return postRepository.findByPostTypeOrderByCreatedAtDesc(PostType.FEED)
                .stream()
                .map(postMapper::toResponse)
                .toList();
    }

    public List<PostResponse> getProfilePosts(User author) {
        return postRepository.findByAuthorAndPostTypeOrderByCreatedAtDesc(author, PostType.PROFILE)
                .stream()
                .map(postMapper::toResponse)
                .toList();
    }

    public List<PostResponse> getPostsByAuthor(User author) {
        return postRepository.findByAuthorOrderByCreatedAtDesc(author)
                .stream()
                .map(postMapper::toResponse)
                .toList();
    }

    public List<PostResponse> getFeedPostsByAuthors(List<User> authors) {
        return postRepository.findByAuthorInAndPostTypeOrderByCreatedAtDesc(authors, PostType.FEED)
                .stream()
                .map(postMapper::toResponse)
                .toList();
    }

    public Long countProfilePostsByAuthor(User author) {
        return postRepository.countByAuthorAndPostType(author, PostType.PROFILE);
    }

    @Transactional
    public PostDetailsResponse updatePostContent(Long postId, UpdatePostRequest request) {
        validateContent(request.content());

        Post post = findPostById(postId);
        post.setContent(request.content());

        Post savedPost = postRepository.save(post);
        return postMapper.toDetailsResponse(savedPost);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = findPostById(postId);
        postRepository.delete(post);
    }

    @Transactional
    public void incrementLikeCount(Long postId) {
        findPostById(postId);
        postRepository.incrementLikeCount(postId);
    }

    @Transactional
    public void decrementLikeCount(Long postId) {
        findPostById(postId);
        postRepository.decrementLikeCount(postId);
    }

    @Transactional
    public void incrementCommentCount(Long postId) {
        findPostById(postId);
        postRepository.incrementCommentCount(postId);
    }

    @Transactional
    public void decrementCommentCount(Long postId) {
        findPostById(postId);
        postRepository.decrementCommentCount(postId);
    }

    @Transactional
    public void incrementRepostCount(Long postId) {
        findPostById(postId);
        postRepository.incrementRepostCount(postId);
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new EmptyPostContentException();
        }
    }
}