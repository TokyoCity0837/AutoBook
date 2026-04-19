import { useState, useEffect, useCallback } from 'react';
import { userRepository, followRepository } from '../../data/repositories';
import type { UserProfile } from '../../domain/models';

export function useProfile(id?: string) {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [isFollowing, setIsFollowing] = useState(false);
  const [followLoading, setFollowLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    const fetchId = id || 'me';
    const promise = fetchId === 'me'
      ? userRepository.getMe()
      : userRepository.getById(Number(fetchId));

    promise
      .then(data => { setProfile(data); setLoading(false); })
      .catch(() => { setProfile(null); setLoading(false); });

    if (id && id !== 'me') {
      followRepository.getStatus(Number(id))
        .then(setIsFollowing)
        .catch(() => setIsFollowing(false));
    }
  }, [id]);

  const toggleFollow = useCallback(async () => {
    if (!id || id === 'me' || followLoading) return;
    setFollowLoading(true);
    try {
      if (isFollowing) {
        await followRepository.unfollow(Number(id));
        setIsFollowing(false);
      } else {
        await followRepository.follow(Number(id));
        setIsFollowing(true);
      }
    } catch (err: any) {
      if (err.response?.status === 409) setIsFollowing(true);
      else console.error('Follow toggle failed', err);
    } finally {
      setFollowLoading(false);
    }
  }, [id, isFollowing, followLoading]);

  return { profile, loading, isFollowing, followLoading, toggleFollow };
}
