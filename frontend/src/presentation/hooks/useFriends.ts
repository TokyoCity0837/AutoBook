import { useState, useEffect } from 'react';
import { followRepository } from '../../data/repositories';
import type { UserCard } from '../../domain/models';

export function useFriends() {
  const [friends, setFriends] = useState<UserCard[]>([]);
  const [following, setFollowing] = useState<UserCard[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      followRepository.getFriends(),
      followRepository.getFollowing()
    ]).then(([friendsRes, followingRes]) => {
      const friendUsers = friendsRes.map((f: any) => f.follower);
      const friendIds = new Set(friendUsers.map((u: any) => u.id));
      const followingUsers = followingRes.map((f: any) => f.following);
      const pureFollowing = followingUsers.filter((u: any) => !friendIds.has(u.id));

      setFriends(friendUsers);
      setFollowing(pureFollowing);
      setLoading(false);
    }).catch(err => {
      console.error('Failed to load friends', err);
      setFriends([]);
      setFollowing([]);
      setLoading(false);
    });
  }, []);

  return { friends, following, loading };
}
