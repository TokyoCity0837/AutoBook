import { useState, useEffect, useCallback } from 'react';
import { postRepository, commentRepository } from '../../data/repositories';
import type { Post, Comment } from '../../domain/models';

export function usePostPage(id?: string) {
  const [post, setPost] = useState<Post | null>(null);
  const [comments, setComments] = useState<Comment[]>([]);
  const [loading, setLoading] = useState(true);

  const loadData = useCallback(async () => {
    if (!id) return;
    setLoading(true);
    try {
      const [postData, commentsData] = await Promise.all([
        postRepository.getById(Number(id)),
        commentRepository.getByPost(Number(id))
      ]);
      setPost(postData);
      setComments(commentsData);
    } catch (err) {
      console.error('Error loading post', err);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => { loadData(); }, [loadData]);

  const submitComment = async (parentId: number | null, content: string) => {
    if (!id) return;
    await commentRepository.createForPost(Number(id), { content, parentId });
    loadData();
  };

  const likeComment = (commentId: number) => {
    commentRepository.likePostComment(commentId).catch(console.error);
  };

  return { post, comments, loading, submitComment, likeComment };
}
