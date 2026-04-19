import { useState, useEffect, useCallback } from 'react';
import { postRepository } from '../../data/repositories';
import { userRepository } from '../../data/repositories';
import type { Post } from '../../domain/models';

export function useFeed() {
  const [posts, setPosts] = useState<Post[]>([]);
  const [loading, setLoading] = useState(true);

  const refresh = useCallback(async () => {
    setLoading(true);
    try {
      const data = await postRepository.getFeed();
      setPosts(data);
    } catch (err) {
      console.error('Failed to load feed', err);
      setPosts([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { refresh(); }, [refresh]);

  return { posts, loading, refresh };
}

export function useUserSearch() {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<any[]>([]);

  useEffect(() => {
    if (query.trim().length === 0) {
      setResults([]);
      return;
    }
    const delay = setTimeout(() => {
      userRepository.search(query)
        .then(setResults)
        .catch(console.error);
    }, 300);
    return () => clearTimeout(delay);
  }, [query]);

  return { query, setQuery, results };
}
