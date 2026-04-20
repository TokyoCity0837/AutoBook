import React, { useState, useRef } from 'react';
import { useBookDetails } from '../hooks/useBookDetails';
import { useParams, useNavigate } from 'react-router-dom';
import { CreationCommentInline, NestedCommentList } from '../components/post/CommentSection';
import { IconFriends } from '../components/ui/Icons';
import { storageRepository, bookRepository } from '../../data/repositories';
import { MEDIA_BASE_URL } from '../../shared/constants/config';
import '../../assets/styles/pages.css';
import '../../assets/styles/BookPage.css';

function Plus() {
    return (
        <svg width="32" height="32" viewBox="0 0 24 24" fill="none">
            <path d="M6 12H18M12 6V18" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
    );
}

export default function BookPage() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const { book, comments, isOwner, isSaved, loading, toggleSave, submitComment, setBook } = useBookDetails(id);

    const [isEditing, setIsEditing] = useState(false);
    const [editDesc, setEditDesc] = useState('');
    const fileInputRef = useRef<HTMLInputElement>(null);

    React.useEffect(() => {
        if (book) setEditDesc(book.description || '');
    }, [book]);

    const handleSaveEdits = async () => {
        if (!id || !book) return;
        await bookRepository.update(Number(id), {
            title: book.title, description: editDesc,
            genre: book.genre, privacy: book.privacy, coverImage: book.coverImage
        });
        setBook((prev: any) => prev ? { ...prev, description: editDesc } : prev);
        setIsEditing(false);
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

    if (loading || !book) return <div className="bookDetailsWrap">Loading Book...</div>;

    return (
        <div className="bookDetailsWrap">
            <div className="bookImageDesc">
                {book.coverImage ? (
                    <img src={`${MEDIA_BASE_URL}${book.coverImage}`} alt="Cover" className="ImgBookDecs" />
                ) : (
                    <div className="ImgBookDecs" style={{ backgroundColor: '#222', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>No Cover</div>
                )}
                <div className="bookTitleDesc">{book.title}</div>
                <div className="bookAuthorsDesc">by {book.author?.visibleName}</div>

                {isOwner && (
                    <div style={{ marginTop: '15px', display: 'flex', gap: '10px' }}>
                        <button className="publishBtn" onClick={() => navigate(`/editor/${book.id}`)}>Open in Editor</button>
                        <button className="iconActionBtn" onClick={() => fileInputRef.current?.click()}>Change Cover</button>
                        <input type="file" style={{ display: 'none' }} accept="image/*" ref={fileInputRef} onChange={handleCoverUpload} />
                    </div>
                )}
            </div>

            <div className="bookInfoDecs">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                    <div style={{ display: 'flex', gap: '10px' }}>
                        <span className="bpStatBadge" style={{ background: 'rgba(255,255,255,0.1)', padding: '5px 12px', borderRadius: '15px' }}>{book.privacy || 'PUBLIC'}</span>
                        <span className="bpStatBadge" style={{ background: 'rgba(255,255,255,0.1)', padding: '5px 12px', borderRadius: '15px' }}>{book.genre || 'Original Fiction'}</span>
                    </div>
                    {isOwner && !isEditing && (
                        <button className="iconActionBtn" style={{ padding: '6px 12px', fontSize: '14px' }} onClick={() => setIsEditing(true)}>Edit Synopsis</button>
                    )}
                </div>

                {isEditing ? (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginBottom: '4vh' }}>
                        <textarea value={editDesc} onChange={e => setEditDesc(e.target.value)}
                            style={{ width: '100%', minHeight: '150px', background: 'rgba(255,255,255,0.1)', color: 'white', border: 'none', padding: '15px', borderRadius: '8px', fontSize: '18px' }} />
                        <div style={{ display: 'flex', gap: '10px' }}>
                            <button className="publishBtn" onClick={handleSaveEdits}>Save</button>
                            <button className="iconActionBtn" onClick={() => setIsEditing(false)}>Cancel</button>
                        </div>
                    </div>
                ) : (
                    <div className="bookDescription">{book.description || "No synopsis available."}</div>
                )}

                <div style={{ display: 'flex', gap: '15px' }}>
                    <div className="addToLibrary" style={{ cursor: 'pointer' }} onClick={() => navigate(`/book/${id}/read`)}>Read Book <IconFriends size={24} /></div>
                    {!isOwner && (
                        <div className="addToLibrary"
                            style={{ cursor: 'pointer', background: isSaved ? '#4caf50' : 'rgba(255,255,255,0.1)' }}
                            onClick={toggleSave}>
                            {isSaved ? "Saved" : "Add to library"} <Plus />
                        </div>
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
    );
}
