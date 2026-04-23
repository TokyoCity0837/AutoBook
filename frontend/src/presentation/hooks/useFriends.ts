import { useState, useEffect } from 'react';
import { followRepository } from '../../data/repositories';
import type { UserCard } from '../../domain/models';

export function useFriends() {
  const [friends, setFriends] = useState<UserCard[]>([]);
  const [following, setFollowing] = useState<UserCard[]>([]);
  const [followers, setFollowers] = useState<UserCard[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
        followRepository.getFriends(),
        followRepository.getFollowing(),
        followRepository.getFollowers()
    ]).then(([friendsRes, followingRes, followersRes]) => {
        const friendUsers = friendsRes.map((f: any) => f.follower);
        const friendIds = new Set(friendUsers.map((u: any) => u.id));

        const followingUsers = followingRes.map((f: any) => f.following);
        const pureFollowing = followingUsers.filter((u: any) => !friendIds.has(u.id));

        const followerUsers = followersRes.map((f: any) => f.follower); // перейменував
        const pureFollowers = followerUsers.filter((u: any) => !friendIds.has(u.id)); // теж фільтруємо друзів

        setFriends(friendUsers);
        setFollowing(pureFollowing);
        setFollowers(pureFollowers);
        setLoading(false);
    }).catch(err => {
        console.error('Failed to load friends', err);
        setFriends([]);
        setFollowing([]);
        setFollowers([]);
        setLoading(false);
    });
  }, []);

  return { friends, following, loading, followers };
}
