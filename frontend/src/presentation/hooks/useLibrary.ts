import { useState, useEffect } from 'react';
import { bookRepository, libraryRepository } from '../../data/repositories';
import type { BookCard } from '../../domain/models';

export function useLibrary() {
  const [myBooks, setMyBooks] = useState<BookCard[]>([]);
  const [savedBooks, setSavedBooks] = useState<BookCard[]>([]);
  const [loading, setLoading] = useState(true);

  const refresh = async () => {
    setLoading(true);
    try {
      const [myRes, savedRes] = await Promise.all([
        bookRepository.getMyBooks(),
        libraryRepository.getMySaved()
      ]);
      setMyBooks(myRes);
      setSavedBooks(savedRes.map((s: any) => s.book));
    } catch (err) {
      console.error('Failed to load library', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { refresh(); }, []);

  return { myBooks, savedBooks, loading, refresh };
}
