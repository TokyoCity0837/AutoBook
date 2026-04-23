package com.autobook.Social.Post.PostReposts;

import com.autobook.Social.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepostRepository extends JpaRepository<PostRepost, PostRepostId> {

    boolean existsByIdUserIdAndIdPostId(Long userId, Long postId);

    List<PostRepost> findByUserOrderByCreatedAtDesc(User user);
}