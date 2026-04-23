import React, { useState, useRef } from 'react';
import { useBookDetails } from '../hooks/useBookDetails';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { CreationCommentInline, NestedCommentList } from '../components/post/CommentSection';
import { storageRepository, bookRepository } from '../../data/repositories';
import { MEDIA_BASE_URL, DEFAULT_COVER_GRADIENT } from '../../shared/constants/config';
import '../../assets/styles/pages.css';
import '../../assets/styles/BookPage.css';


export default function BookPage() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const { book, comments, isOwner, isSaved, loading, toggleSave, submitComment, setBook } = useBookDetails(id);

    const [isEditing, setIsEditing] = useState(false);
    const [editDesc, setEditDesc] = useState('');
    const fileInputRef = useRef<HTMLInputElement>(null);

    React.useEffect(() => {
        if (book) setEditDesc(book.description || '');
    }, [book]);

    const [showEditModal, setShowEditModal] = useState(false);
    const [editForm, setEditForm] = useState({ title: '', description: '', genre: '', privacy: '' });
    
    React.useEffect(() => {
        if (book) {
            setEditForm({
                title: book.title || '',
                description: book.description || '',
                genre: book.genre || '',
                privacy: book.privacy || 'PUBLIC',
            });
        }
    }, [book]);
    
    const handleSaveEdits = async () => {
        if (!id || !book) return;
        await bookRepository.update(Number(id), editForm);
        setBook((prev: any) => prev ? { ...prev, ...editForm } : prev);
        setShowEditModal(false);
    };


    const handleCoverUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
        if (!e.target.files?.[0] || !id || !book) return;
        try {
            const newCover = await storageRepository.upload(e.target.files[0]);
            await bookRepository.update(Number(id), {
                title: book.title, description: book.description,
                genre: book.genre, privacy: book.privacy, coverImage: newCover
            });
            setBook((prev: any) => prev ? { ...prev, coverImage: newCover } : prev);
        } catch (err) { console.error('Cover upload failed', err); }
    };

    const handleDelete = async () => {
        try {
            await bookRepository.delete(book.id);
            navigate('/library');
        } catch (err: any) {
            console.error('Delete failed:', err.response?.data);
            alert('Не вдалося видалити книгу');
        }
    };

    

    if (loading || !book) return <div className="bookDetailsWrap">Loading Book...</div>;

    return (
        <>
        <div className="bookDetailsWrap">
            <div className="bookImageDesc">
                {book.coverImage ? (
                    <img src={`${MEDIA_BASE_URL}${book.coverImage}`} alt="Cover" className="ImgBookDecs" />
                ) : (
                    <div className="ImgBookDecs" style={{ background: DEFAULT_COVER_GRADIENT, display: 'flex', 
                        alignItems: 'center', justifyContent: 'center', 
                        fontSize: '24px', fontWeight: '600' }}>{book.title}</div>
                )}
                <div className="bookTitleDesc">{book.title}</div>
                <Link to={book.author?.id ? `/profile/${book.author.id}` : "#"} style={{ textDecoration: 'underline', color: '#fff' }} className="bookAuthorsDesc">by {book.author?.visibleName}</Link>

                {isOwner && (
                    <div className="bookOwnerActions">
                        <button className="bookActionBtn primary" onClick={() => navigate(`/editor/${book.id}`)}>
                            Open in Editor
                        </button>
                        <button className="bookActionBtn" onClick={() => fileInputRef.current?.click()}>
                            Change Cover
                        </button>
                        <button className="bookActionBtn" onClick={() => setShowEditModal(true)}>
                            Edit Book
                        </button>
                        <input type="file" style={{ display: 'none' }} accept="image/*" ref={fileInputRef} onChange={handleCoverUpload} />
                    </div>
                )}
            </div>

            <div className="bookInfoDecs">
                <div className="bookMeta">
                    <span className="bookMetaBadge">
                        <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
                            {book.privacy === 'PRIVATE'
                                ? <><rect x="3" y="11" width="18" height="11" rx="2" stroke="currentColor" strokeWidth="2"/><path d="M7 11V7a5 5 0 0110 0v4" stroke="currentColor" strokeWidth="2"/></>
                                : <><circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/><path d="M2 12h20M12 2c2.5 2.5 4 6 4 10s-1.5 7.5-4 10" stroke="currentColor" strokeWidth="2"/></>
                            }
                        </svg>
                        {book.privacy || 'PUBLIC'}
                    </span>
                    {book.genre && (
                        <span className="bookMetaBadge">
                            <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
                                <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                                <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" stroke="currentColor" strokeWidth="2"/>
                            </svg>
                            {book.genre}
                        </span>
                    )}
                </div>

                {isEditing ? (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginBottom: '4vh' }}>
                        <textarea value={editDesc} onChange={e => setEditDesc(e.target.value)}
                            style={{ width: '40vw', minHeight: '150px', background: 'rgba(255,255,255,0.1)', color: 'white', border: 'none', padding: '15px', borderRadius: '8px', fontSize: '18px' }} />
                        <div style={{ display: 'flex', gap: '10px', width: '42vw' }}>
                            <button className="bookActionBtn primary" onClick={handleSaveEdits}>Save</button>
                            <button className="bookActionBtn" onClick={() => setIsEditing(false)}>Cancel</button>
                        </div>
                    </div>
                ) : (
                    <div className="bookDescription">{book.description || "No synopsis available."}</div>
                )}

                <div className="bookActions">
                    <button className="bookPrimaryBtn" onClick={() => navigate(`/book/${id}/read`)}>
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                            <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                            <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                        </svg>
                        Read Book
                    </button>
                    {!isOwner && (
                        <button
                            className={`bookSecondaryBtn ${isSaved ? 'saved' : ''}`}
                            onClick={toggleSave}>
                            {isSaved ? (<>
                                    <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                                        <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
                                    </svg> Saved
                                </>
                            ) : (<>
                                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                                        <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                    </svg>Add to Library
                                </>
                            )}
                        </button>
                    )}
                </div>

                <div className="splitLine" />
                <div className="comments">
                    <div style={{ marginBottom: '25px', borderBottom: '1px solid rgba(255,255,255,0.08)', paddingBottom: '30px' }}>
                        <CreationCommentInline placeholder="Write a comment..." placeholderButton='Comment'
                            onSubmit={async (text) => await submitComment(null, text)} />
                    </div>
                    <div style={{ display: 'flex', flexDirection: 'column', paddingLeft: '20px' }}>
                        <NestedCommentList comments={comments} onReplySubmit={submitComment} commentType="book" />
                    </div>
                </div>
            </div>
        </div>


        {showDeleteModal && (
            <div className="modalOverlay" onClick={() => setShowDeleteModal(false)}>
                <div className="modalContent" onClick={e => e.stopPropagation()}>
                    <h3>Delete Book</h3>
                    <p>Are you sure you want to delete <strong>{book.title}</strong>? This action cannot be undone.</p>
                    <div className="modalActions">
                        <button className="modalBtn cancelBtn" onClick={() => setShowDeleteModal(false)}>Cancel</button>
                        <button className="modalBtn confirmBtn" onClick={handleDelete}>Delete</button>
                    </div>
                </div>
            </div>
        )}

       {showEditModal && (
        <div className="modalOverlay" onMouseDown={(e) => {if (e.target === e.currentTarget) setShowEditModal(false);}}>
            <div className="modalContent" onClick={e => e.stopPropagation()}>
                <div className="modalHeader">
                    <h2 className="modalTitle">Edit Book</h2>
                    <button className="modalClose" onClick={() => setShowEditModal(false)}>
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
                            value={editForm.title}
                            onChange={e => setEditForm(p => ({ ...p, title: e.target.value }))}
                        />
                    </div>

                    <div className="modalField">
                        <label className="modalLabel">Synopsis</label>
                        <textarea
                            className="modalTextarea"
                            value={editForm.description}
                            onChange={e => setEditForm(p => ({ ...p, description: e.target.value }))}
                            rows={5}
                        />
                    </div>

                    <div className="modalField">
                        <label className="modalLabel">Genre</label>
                        <input
                            className="modalInput"
                            value={editForm.genre}
                            onChange={e => setEditForm(p => ({ ...p, genre: e.target.value }))}
                        />
                    </div>

                    <div className="modalField">
                        <label className="modalLabel">Visibility</label>
                        <div className="modalSegment">
                            <button
                                className={`modalSegmentBtn${editForm.privacy === 'PUBLIC' ? ' active' : ''}`}
                                onClick={() => setEditForm(p => ({ ...p, privacy: 'PUBLIC' }))}>
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
                                    <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
                                    <path d="M2 12h20M12 2c2.5 2.5 4 6 4 10s-1.5 7.5-4 10c-2.5-2.5-4-6-4-10s1.5-7.5 4-10z" stroke="currentColor" strokeWidth="2"/>
                                </svg>
                                Public
                            </button>
                            <button
                                className={`modalSegmentBtn${editForm.privacy === 'PRIVATE' ? ' active' : ''}`}
                                onClick={() => setEditForm(p => ({ ...p, privacy: 'PRIVATE' }))}>
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
                                    <rect x="3" y="11" width="18" height="11" rx="2" stroke="currentColor" strokeWidth="2"/>
                                    <path d="M7 11V7a5 5 0 0110 0v4" stroke="currentColor" strokeWidth="2"/>
                                </svg>
                                Private
                            </button>
                        </div>
                    </div>
                </div>

                <div className="modalFooter modalFooterSplit">
                    <button className="modalBtnSecondary confirmBtn" onClick={() => { setShowEditModal(false); setShowDeleteModal(true); }}>
                        Delete Book
                    </button>
                    <div style={{ display: 'flex', gap: '10px' }}>
                        <button className="modalBtnSecondary" onClick={() => setShowEditModal(false)}>Cancel</button>
                        <button className="modalBtnPrimary" onClick={handleSaveEdits}>Save</button>
                    </div>
                </div>
            </div>
            </div>
        )}

        </>
    );
}
