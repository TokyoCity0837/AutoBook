import { useState, useEffect, useRef, useCallback } from 'react';
import { bookRepository, chapterRepository } from '../../data/repositories';
import type { BookDetails } from '../../domain/models';

interface EditorChapter {
  id: number;
  title: string;
  content: string;
  sub: any[];
}

export function useEditor(bookId?: string) {
    const [bookData, setBookData] = useState<BookDetails | null>(null);
    const [bookTitle, setBookTitle] = useState('Untitled');
    const [chapters, setChapters] = useState<EditorChapter[]>([]);
    const [activeId, setActiveId] = useState(0);
    const [saving, setSaving] = useState(false);
    const contentRef = useRef<Record<number, string>>({});
  
    useEffect(() => {
      if (!bookId) return;
      let cancelled = false;
  
      bookRepository.getById(Number(bookId)).then(b => {
        if (cancelled) return;
        setBookData(b);
        setBookTitle(b.title);
      }).catch(console.error);
  
      chapterRepository.getByBook(Number(bookId)).then(async res => {
        if (cancelled) return;
        if (res && res.length > 0) {
          const mapped: EditorChapter[] = res.map((c: any) => ({
            id: c.id, title: c.title, content: c.content || '', sub: []
          }));
          mapped.forEach(c => { contentRef.current[c.id] = c.content; });
          setChapters(mapped);
          setActiveId(mapped[0].id);
        } else {
          try {
            const newCh = await chapterRepository.create(Number(bookId), { title: 'Chapter 1', content: '' });
            if (cancelled) return;
            const ch: EditorChapter = { id: newCh.id, title: newCh.title, content: '', sub: [] };
            contentRef.current[ch.id] = '';
            setChapters([ch]);
            setActiveId(ch.id);
          } catch (e) {
            console.error('Failed to auto-create chapter', e);
            if (!cancelled) setChapters([]);
          }
        }
      });
  
      return () => { cancelled = true; };
    }, [bookId]);
  
    const handleSave = useCallback(async () => {
      if (!bookId) return;
      setSaving(true);
      try {
        await bookRepository.update(Number(bookId), { title: bookTitle });
        for (const ch of chapters) {
          const content = contentRef.current[ch.id] ?? ch.content;
          if (ch.id > 1000000) {
            const newCh = await chapterRepository.create(Number(bookId), { title: ch.title, content });
            contentRef.current[newCh.id] = content;
            delete contentRef.current[ch.id];
            ch.id = newCh.id;
          } else {
            await chapterRepository.update(ch.id, { title: ch.title, content });
          }
        }
      } catch (err) {
        console.error('Save failed', err);
      } finally {
        setSaving(false);
      }
    }, [bookId, bookTitle, chapters]);
  
    return {
      bookData, bookTitle, setBookTitle,
      chapters, setChapters,
      activeId, setActiveId,
      contentRef,
      saving, handleSave,
    };
  }
