import { useState } from 'react';
import { useLibrary } from '../hooks/useLibrary';
import { BookCard } from '../components/book/BookCard';
import { bookRepository } from '../../data/repositories';
import '../../assets/styles/libraryPage.css';

export function Plus() {
    return (
        <svg width="96" height="96" viewBox="0 0 164 164" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path fillRule="evenodd" clipRule="evenodd" d="M112.75 87.125H87.125V112.75C87.125 115.569 84.8341 117.875 82 117.875C79.1659 117.875 76.875 115.569 76.875 112.75V87.125H51.25C48.4159 87.125 46.125 84.8188 46.125 82C46.125 79.1812 48.4159 76.875 51.25 76.875H76.875V51.25C76.875 48.4312 79.1659 46.125 82 46.125C84.8341 46.125 87.125 48.4312 87.125 51.25V76.875H112.75C115.584 76.875 117.875 79.1812 117.875 82C117.875 84.8188 115.584 87.125 112.75 87.125ZM82 0C36.7104 0 0 36.695 0 82C0 127.305 36.7104 164 82 164C127.29 164 164 127.305 164 82C164 36.695 127.29 0 82 0Z" fill="white"/>
        </svg>
    );
}

/* ─── Create Book Modal ────────────────────────────────── */

function CreateBookModal({ open, onClose, onCreated }: {
    open: boolean;
    onClose: () => void;
    onCreated: () => void;
}) {
    const [title, setTitle] = useState('');
    const [desc, setDesc] = useState('');
    const [privacy, setPrivacy] = useState('PUBLIC');
    const [creating, setCreating] = useState(false);
    const [error, setError] = useState('');

    if (!open) return null;

    const handleCreate = async () => {
        if (!title.trim()) { setError('Title is required'); return; }
        setCreating(true);
        setError('');
        try {
            await bookRepository.create({ title, privacy, description: desc });
            setTitle(''); setDesc(''); setPrivacy('PUBLIC');
            onCreated();
            onClose();
        } catch (err) {
            console.error('Error creating book', err);
            setError('Failed to create book. Try again.');
        } finally {
            setCreating(false);
        }
    };

    return (
        <div className="modalOverlay" onClick={onClose}>
            <div className="modalContent" onClick={e => e.stopPropagation()}>
                <div className="modalHeader">
                    <h2 className="modalTitle">Create New Book</h2>
                    <button className="modalClose" onClick={onClose}>
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                        </svg>
                    </button>
                </div>

                <div className="modalBody">
                    <div className="modalField">
                        <label className="modalLabel">Title</label>
                        <input
                            className="modalInput"
                            placeholder="Enter book title..."
                            value={title}
                            onChange={e => setTitle(e.target.value)}
                            autoFocus
                        />
                    </div>

                    <div className="modalField">
                        <label className="modalLabel">Synopsis <span style={{ color: 'rgba(255,255,255,0.3)', fontWeight: 400 }}>(optional)</span></label>
                        <textarea
                            className="modalTextarea"
                            placeholder="Write a short description of your book..."
                            value={desc}
                            onChange={e => setDesc(e.target.value)}
                            rows={4}
                        />
                    </div>

                    <div className="modalField">
                        <label className="modalLabel">Visibility</label>
                        <div className="modalSegment">
                            <button
                                className={`modalSegmentBtn${privacy === 'PUBLIC' ? ' active' : ''}`}
                                onClick={() => setPrivacy('PUBLIC')}
                            >
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/><path d="M2 12h20M12 2c2.5 2.5 4 6 4 10s-1.5 7.5-4 10c-2.5-2.5-4-6-4-10s1.5-7.5 4-10z" stroke="currentColor" strokeWidth="2"/></svg>
                                Public
                            </button>
                            <button
                                className={`modalSegmentBtn${privacy === 'PRIVATE' ? ' active' : ''}`}
                                onClick={() => setPrivacy('PRIVATE')}
                            >
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none"><rect x="3" y="11" width="18" height="11" rx="2" stroke="currentColor" strokeWidth="2"/><path d="M7 11V7a5 5 0 0110 0v4" stroke="currentColor" strokeWidth="2"/></svg>
                                Private
                            </button>
                        </div>
                    </div>

                    {error && <div className="modalError">{error}</div>}
                </div>

                <div className="modalFooter">
                    <button className="modalBtnSecondary" onClick={onClose}>Cancel</button>
                    <button className="modalBtnPrimary" onClick={handleCreate} disabled={creating}>
                        {creating ? (
                            <><span className="modalSpinner" /> Creating...</>
                        ) : (
                            'Create Book'
                        )}
                    </button>
                </div>
            </div>
        </div>
    );
}

/* ─── Library Page ─────────────────────────────────────── */

export default function LibraryPage() {
    const { myBooks, savedBooks, loading, refresh } = useLibrary();
    const [showCreate, setShowCreate] = useState(false);

    if (loading) return <div className='libraryPageWrap'>Loading Library...</div>;

    return (
        <div className='libraryPageWrap'>
            <CreateBookModal
                open={showCreate}
                onClose={() => setShowCreate(false)}
                onCreated={refresh}
            />

            <div className="yourBooks">
                <div className="YBText">Your Books</div>
                <div className="booksWrap">
                    <div className="border" onClick={() => setShowCreate(true)} style={{ cursor: 'pointer' }}>
                        <div className="plusIco">
                            <Plus />
                        </div>
                    </div>
                    {myBooks.map((book: any) => (
                        <BookCard key={book.id} book={book} edit={true} />
                    ))}
                </div>
            </div>

            <div className="YBText" style={{ marginTop: '30px' }}>Saved to Library</div>
            <div className="booksWrap">
                {savedBooks.length > 0 ? savedBooks.map((book: any) => (
                    <BookCard key={book.id} book={book} />
                )) : (
                    <div style={{ color: 'rgba(255,255,255,0.3)', padding: '20px' }}>No saved books yet</div>
                )}
            </div>
        </div>
    );
}
