import '../assets/styles/pages.css';
import '../assets/styles/BookPage.css';
import { IconFriends } from '../components/Icons';
import { CreationCommentInline, NestedCommentList } from '../components/Posts';
import React, { useState, useEffect, useRef } from 'react';
import api from '../api';
import { useParams, useNavigate } from 'react-router-dom';

function Plus() {
    return (
        <svg width="32" height="32" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M6 12H18M12 6V18" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
    );
}

export function Update({ text = "Edit Comment", authorName="Andrii Dosyn" }: { text?: string, authorName?: string }) {
    return (
        <div className="updateWrap">
            <div className="userAndDate">
                <div className='user'>
                    <div className='ProfileImage'></div>
                    <div className='.userInfo'>
                        <div className='NicknameForDecs'>{authorName} <div className="statusWrap"><IconFriends size={14} /></div></div>
                    </div>
                </div>
                <div className="date">10.03.2026</div>
            </div>
            <div className="updateText">{text}</div>
        </div>
    );
}

export default function BookDetails() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [bookData, setBookData] = useState<any>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isOwner, setIsOwner] = useState(false);
    const [isSaved, setIsSaved] = useState(false);

    const [isEditing, setIsEditing] = useState(false);
    const [editDesc, setEditDesc] = useState('');
    const fileInputRef = useRef<HTMLInputElement>(null);

    useEffect(() => {
        if (!id) return;
        const fetchData = async () => {
            try {
                const [bookRes, userRes, chRes, commentRes, statusRes] = await Promise.all([
                    api.get(`/books/${id}`),
                    api.get('/users/profile/me').catch(() => ({ data: null })),
                    api.get(`/chapters/book/${id}`),
                    api.get(`/book-comments/book/${id}`),
                    api.get(`/library/book/${id}/status`).catch(() => ({ data: false }))
                ]);
                const bk = bookRes.data;
                const me = userRes.data;
                setBookData({
                    ...bk,
                    chaptersAllDecs: chRes.data || [],
                    comments: commentRes.data || []
                });
                setIsSaved(statusRes.data || false);
                setEditDesc(bk.description || '');
                if (me && bk.author && me.id === bk.author.id) {
                    setIsOwner(true);
                }
            } catch (err) {
                console.error("Error loading book detail");
            } finally {
                setIsLoading(false);
            }
        };
        fetchData();
    }, [id]);

    const handleSaveEdits = async () => {
        await api.put(`/books/${id}`, { 
            title: bookData.title,
            description: editDesc,
            genre: bookData.genre,
            privacy: bookData.privacy,
            coverImage: bookData.coverImage 
        });
        setBookData({ ...bookData, description: editDesc });
        setIsEditing(false);
    };

    const handleCoverUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files[0]) {
            const formData = new FormData();
            formData.append('file', e.target.files[0]);
            try {
                const uploadRes = await api.post('/upload', formData, {
                    headers: { 'Content-Type': 'multipart/form-data' }
                });
                const newCover = uploadRes.data;
                await api.put(`/books/${id}`, {
                    title: bookData.title,
                    description: bookData.description,
                    genre: bookData.genre,
                    privacy: bookData.privacy,
                    coverImage: newCover
                });
                setBookData({ ...bookData, coverImage: newCover });
            } catch (err) {
                console.error('Cover upload failed', err);
            }
        }
    };
                


    if (isLoading || !bookData) {
        return <div className="bookDetailsWrap">Loading Book...</div>;
    }

    const handleCommentSubmit = async (parentId: number | null, text: string) => {
        try {
            await api.post(`/book-comments/book/${id}`, { content: text, parentId });
            const commentRes = await api.get(`/book-comments/book/${id}`);
            setBookData((prev: any) => ({ ...prev, comments: commentRes.data }));
        } catch (error) {
            console.error("Failed to post comment", error);
        }
    };

    return (
        <div className="bookDetailsWrap">
            <div className="bookImageDesc">
                {bookData.coverImage ? (
                    <img src={`http://localhost:8080${bookData.coverImage}`} alt="Book Cover" className="ImgBookDecs" />
                ) : (
                    <div className="ImgBookDecs" style={{ backgroundColor: '#222', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>No Cover</div>
                )}
                <div className="bookTitleDesc">{bookData.title}</div>
                <div className="bookAuthorsDesc">by {bookData.author?.visibleName}</div>
                
                {isOwner && (
                    <div style={{ marginTop: '15px', display: 'flex', gap: '10px' }}>
                        <button className="publishBtn" onClick={() => navigate(`/editor/${bookData.id}`)}>
                            Open in Editor
                        </button>
                        <button className="iconActionBtn" onClick={() => fileInputRef.current?.click()}>
                            Change Cover
                        </button>
                        <input type="file" style={{ display: 'none' }} accept="image/*" ref={fileInputRef} onChange={handleCoverUpload} />
                    </div>
                )}
            </div>

            <div className="bookInfoDecs">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                    <div style={{ display: 'flex', gap: '10px' }}>
                        <span className="bpStatBadge" style={{ background: 'rgba(255,255,255,0.1)', padding: '5px 12px', borderRadius: '15px' }}>{bookData.privacy || 'PUBLIC'}</span>
                        <span className="bpStatBadge" style={{ background: 'rgba(255,255,255,0.1)', padding: '5px 12px', borderRadius: '15px' }}>{bookData.genre || 'Original Fiction'}</span>
                    </div>
                    {isOwner && !isEditing && (
                        <button className="iconActionBtn" style={{ padding: '6px 12px', fontSize: '14px' }} onClick={() => setIsEditing(true)}>Edit Synopsis</button>
                    )}
                </div>

                {isEditing ? (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginBottom: '4vh' }}>
                        <textarea 
                            value={editDesc} 
                            onChange={e => setEditDesc(e.target.value)} 
                            style={{ width: '100%', minHeight: '150px', background: 'rgba(255,255,255,0.1)', color: 'white', border: 'none', padding: '15px', borderRadius: '8px', fontSize: '18px' }} 
                        />
                        <div style={{ display: 'flex', gap: '10px' }}>
                            <button className="publishBtn" onClick={handleSaveEdits}>Save</button>
                            <button className="iconActionBtn" onClick={() => setIsEditing(false)}>Cancel</button>
                        </div>
                    </div>
                ) : (
                    <div className="bookDescription">
                        {bookData.description || "No synopsis available for this book."}
                    </div>
                )}

                <div style={{ display: 'flex', gap: '15px' }}>
                    <div className="addToLibrary" style={{ cursor: 'pointer' }} onClick={() => navigate(`/book/${id}/read`)}>Read Book <IconFriends size={24} /></div>
                    {!isOwner && (
                        <div 
                            className="addToLibrary" 
                            style={{ cursor: 'pointer', background: isSaved ? '#4caf50' : 'rgba(255,255,255,0.1)' }}
                            onClick={async () => {
                                try {
                                    await api.post(`/library/book/${id}`);
                                    setIsSaved(!isSaved);
                                } catch (e) { console.error(e); }
                            }}
                        >
                            {isSaved ? "Saved" : "Add to library"} <Plus />
                        </div>
                    )}
                </div>

                <div className="updatesBlock">
                    {(bookData.updates || []).map((update: any) => (
                        <Update key={update.id} text={update.text} authorName={update.authorName} />
                    ))}
                </div>
                <div className="splitLine"></div>
                <div className="comments">
                    <div style={{ marginBottom: '25px', borderBottom: '1px solid rgba(255,255,255,0.08)', paddingBottom: '30px' }}>
                        <CreationCommentInline 
                            placeholder="Write a comment..." 
                            placeholderButton='Comment' 
                            onSubmit={async (text) => await handleCommentSubmit(null, text)}
                        />
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', paddingLeft: '20px' }}>
                        <NestedCommentList comments={bookData.comments} onReplySubmit={handleCommentSubmit} commentType="book" />
                    </div>
                </div>
            </div>
        </div>
    );
}