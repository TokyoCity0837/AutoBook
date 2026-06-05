package com.autobook.Social.Post;

import com.autobook.Enum.PostType;
import com.autobook.Exception.EmptyPostContentException;
import com.autobook.Exception.PostNotFoundException;
import com.autobook.Factory.PostFactory;
import com.autobook.Social.Post.DTO.Request.CreatePostRequest;
import com.autobook.Social.Post.DTO.Request.UpdatePostRequest;
import com.autobook.Social.Post.DTO.Response.PostDetailsResponse;
import com.autobook.Social.Post.DTO.Response.PostResponse;
import com.autobook.Social.Post.PostLikes.PostLike;
import com.autobook.Social.Post.PostLikes.PostLikeId;
import com.autobook.Social.Post.PostLikes.PostLikeRepository;
import com.autobook.Social.Post.PostReposts.PostRepost;
import com.autobook.Social.Post.PostReposts.PostRepostId;
import com.autobook.Social.Post.PostReposts.PostRepostRepository;
import com.autobook.Social.User.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing social {@link Post} entities.
 *
 * @see PostRepository
 * @see Post
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostFactory postFactory;
    private final PostMapper postMapper;

    private final PostLikeRepository postLikeRepository;
    private final PostRepostRepository postRepostRepository;

    /**
     * Creates a new social post.
     *
     * @param request the request detailing the post content and type
     * @param author  the user creating the post
     * @return the detailed response representation of the created post
     * @throws EmptyPostContentException if the post content is missing
     */
    @Transactional
    public PostDetailsResponse createPost(CreatePostRequest request, User author) {
        log.info("Creating new post for author: {}", author.getUsername());
        validateContent(request.content());
        Post post = postFactory.create(request.content(), author, request.postType(), request.imageUrl());
        Post savedPost = postRepository.save(post);
        log.debug("Post successfully created with ID: {}", savedPost.getId());
        return postMapper.toDetailsResponse(savedPost, false, false);
    }

    public PostDetailsResponse getPostById(Long postId, User currentUser) {
        log.debug("Retrieving post details by ID: {}", postId);
        Post post = findPostById(postId);
        boolean liked = postLikeRepository.existsByUserIdAndPostId(currentUser.getId(), postId);
        boolean reposted = postRepostRepository.existsByIdUserIdAndIdPostId(currentUser.getId(), postId);
        return postMapper.toDetailsResponse(post, liked, reposted);
    }

    public List<PostResponse> getAllPosts(User currentUser) {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(post -> postMapper.toResponse(post,
                        postLikeRepository.existsByUserIdAndPostId(currentUser.getId(), post.getId()),
                        postRepostRepository.existsByIdUserIdAndIdPostId(currentUser.getId(), post.getId())))
                .toList();
    }

    public List<PostResponse> getFeedPosts(User currentUser) {
        return postRepository.findByPostTypeOrderByCreatedAtDesc(PostType.FEED)
                .stream()
                .map(post -> postMapper.toResponse(post,
                        postLikeRepository.existsByUserIdAndPostId(currentUser.getId(), post.getId()),
                        postRepostRepository.existsByIdUserIdAndIdPostId(currentUser.getId(), post.getId())))
                .toList();
    }

    public List<PostResponse> getProfilePosts(User author, User currentUser) {
        return postRepository.findByAuthorAndPostTypeOrderByCreatedAtDesc(author, PostType.PROFILE)
                .stream()
                .map(post -> postMapper.toResponse(post,
                        postLikeRepository.existsByUserIdAndPostId(currentUser.getId(), post.getId()),
                        postRepostRepository.existsByIdUserIdAndIdPostId(currentUser.getId(), post.getId())))
                .toList();
    }

    public List<PostResponse> getPostsByAuthor(User author, User currentUser) {
        return postRepository.findByAuthorOrderByCreatedAtDesc(author)
                .stream()
                .map(post -> postMapper.toResponse(post,
                        postLikeRepository.existsByUserIdAndPostId(currentUser.getId(), post.getId()),
                        postRepostRepository.existsByIdUserIdAndIdPostId(currentUser.getId(), post.getId())))
                .toList();
    }

    public List<PostResponse> getFeedPostsByAuthors(List<User> authors, User currentUser) {
        return postRepository.findByAuthorInAndPostTypeOrderByCreatedAtDesc(authors, PostType.FEED)
                .stream()
                .map(post -> postMapper.toResponse(post,
                        postLikeRepository.existsByUserIdAndPostId(currentUser.getId(), post.getId()),
                        postRepostRepository.existsByIdUserIdAndIdPostId(currentUser.getId(), post.getId())))
                .toList();
    }

    public Long countProfilePostsByAuthor(User author) {
        return postRepository.countByAuthorAndPostType(author, PostType.PROFILE);
    }

    /**
     * Updates an existing post context.
     *
     * @param postId      the ID of the post to update
     * @param request     the updated contents
     * @param currentUser the user triggering the update context (used for parsing
     *                    interaction flags)
     * @return the updated post detailed response
     */
    @Transactional
    public PostDetailsResponse updatePostContent(Long postId, UpdatePostRequest request, User currentUser) {
        log.info("Updating content for post ID: {}", postId);
        validateContent(request.content());
        Post post = findPostById(postId);
        post.setContent(request.content());
        Post savedPost = postRepository.save(post);
        boolean liked = postLikeRepository.existsByUserIdAndPostId(currentUser.getId(), postId);
        boolean reposted = postRepostRepository.existsByIdUserIdAndIdPostId(currentUser.getId(), postId);
        return postMapper.toDetailsResponse(savedPost, liked, reposted);
    }

    /**
     * Deletes a post entirely from the ecosystem.
     *
     * @param postId the ID of the post
     */
    @Transactional
    public void deletePost(Long postId) {
        log.info("Deleting post with ID: {}", postId);
        Post post = findPostById(postId);
        postRepository.delete(post);
        log.debug("Post deleted successfully");
    }

    /**
     * Toggles the like state of a post for a given user.
     *
     * @param postId the post ID to toggle like on
     * @param user   the user toggling the like
     * @return {@code true} if the post was liked, {@code false} if unliked
     */
    @Transactional
    public boolean toggleLike(Long postId, User user) {
        log.info("Toggling like on post ID: {} by user: {}", postId, user.getUsername());
        Post post = findPostById(postId);
        PostLikeId id = new PostLikeId(user.getId(), postId);

        if (postLikeRepository.existsById(id)) {
            postLikeRepository.deleteById(id);
            postRepository.decrementLikeCount(postId);
            return false;
        } else {
            postLikeRepository.save(new PostLike(user, post));
            postRepository.incrementLikeCount(postId);
            return true;
        }
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
                .orElseThrow(() -> {
                    log.error("Post not found for ID: {}", postId);
                    return new PostNotFoundException(postId);
                });
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            log.error("Attempted to set an empty content for a post");
            throw new EmptyPostContentException();
        }
    }

    @Transactional
    public boolean toggleRepost(Long postId, User user) {
        Post post = findPostById(postId);
        PostRepostId id = new PostRepostId(user.getId(), postId);

        if (postRepostRepository.existsById(id)) {
            postRepostRepository.deleteById(id);
            postRepository.decrementRepostCount(postId);
            return false;
        } else {
            postRepostRepository.save(new PostRepost(user, post));
            postRepository.incrementRepostCount(postId);
            return true;
        }
    }
}