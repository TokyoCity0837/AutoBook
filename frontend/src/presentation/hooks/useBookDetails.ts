import { useState, useEffect, useCallback } from 'react';
import { bookRepository, libraryRepository, commentRepository } from '../../data/repositories';

export function useBookDetails(id?: string) {
  const [book, setBook] = useState<any>(null);
  const [chapters, setChapters] = useState<any[]>([]);
  const [comments, setComments] = useState<any[]>([]);
  const [isOwner, setIsOwner] = useState(false);
  const [isSaved, setIsSaved] = useState(false);
  const [loading, setLoading] = useState(true);

  const loadData = useCallback(async () => {
    if (!id) return;
    setLoading(true);
    try {
      const { userRepository } = await import('../../data/repositories');
      const { chapterRepository } = await import('../../data/repositories');

      const [bk, me, chs, cmts, status] = await Promise.all([
        bookRepository.getById(Number(id)),
        userRepository.getMe().catch(() => null),
        chapterRepository.getByBook(Number(id)),
        commentRepository.getByBook(Number(id)),
        libraryRepository.getBookStatus(Number(id)).catch(() => false)
      ]);

      setBook({ ...bk, chaptersAllDecs: chs, comments: cmts });
      setChapters(chs);
      setComments(cmts);
      setIsSaved(status);
      if (me && bk.author && me.id === bk.author.id) setIsOwner(true);
    } catch (err) {
      console.error('Error loading book', err);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => { loadData(); }, [loadData]);

  const toggleSave = async () => {
    if (!id) return;
    try {
      await libraryRepository.toggleBook(Number(id));
      setIsSaved(prev => !prev);
    } catch (err) { console.error(err); }
  };

  const submitComment = async (parentId: number | null, content: string) => {
    if (!id) return;
    await commentRepository.createForBook(Number(id), { content, parentId });
    const cmts = await commentRepository.getByBook(Number(id));
    setComments(cmts);
    setBook((prev: any) => prev ? { ...prev, comments: cmts } : prev);
  };

  return { book, chapters, comments, isOwner, isSaved, loading, toggleSave, submitComment, setBook };
}
