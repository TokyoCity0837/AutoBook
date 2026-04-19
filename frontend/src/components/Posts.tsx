import '../assets/styles/Posts.css'
import { IconFriends, IconUser } from './Icons'
import BookImage from '../assets/pictures/BookImage.jpeg'
import React, { useState, useRef } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api from '../api'

export function UserInfoForPost({ author }: { author?: any }) {
    const name = author?.visibleName || "Unknown Author";
    return (
        <div className="user">
            <div 
                className="profileImage"    
                style={author?.profileImage ? {
                    backgroundImage: `url(http://localhost:8080${author.profileImage})`,
                    backgroundSize: 'cover',
                    backgroundPosition: 'center'
                } : {}}
            />
            <div className="userInfo">
                <Link to={author?.id ? `/profile/${author.id}` : "#"} className="Nickname">
                    {name}
                </Link>
                <div className="Status"><IconFriends size={14} />Friend</div>
            </div>
        </div>
    );
}

function IconHeart({ size = 18 }) {
    return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
            <path d="M12 21C12 21 3 15.5 3 9.5C3 7.015 4.985 5 7.5 5C8.986 5 10.306 5.71 11.155 6.808C11.568 7.344 11.775 7.612 12 7.612C12.225 7.612 12.432 7.344 12.845 6.808C13.694 5.71 15.014 5 16.5 5C19.015 5 21 7.015 21 9.5C21 15.5 12 21 12 21Z" stroke="rgba(255,255,255,0.45)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
    )
}

function IconComment({ size = 17 }) {
    return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
            <path d="M21 12C21 16.418 16.97 20 12 20C10.5 20 9.093 19.673 7.857 19.09L3 20L4.338 16.063C3.493 14.973 3 13.542 3 12C3 7.582 7.03 4 12 4C16.97 4 21 7.582 21 12Z" stroke="rgba(255,255,255,0.45)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
    )
}

function IconShare({ size = 17 }) {
    return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
            <path d="M13.47 4.14C12.74 4.36 12.28 5.96 12.09 7.91C6.78 7.91 2 13.48 2 20.08C4.19 14.08 8.99 12.45 12.14 12.45C12.34 14.21 12.79 15.62 13.47 15.82C15.57 16.43 22 12.44 22 9.98C22 7.52 15.57 3.53 13.47 4.14Z" stroke="rgba(255,255,255,0.45)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
    )
}

function IconMore({ size = 18 }) {
    return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="rgba(255,255,255,0.35)">
            <circle cx="5" cy="12" r="1.5" /><circle cx="12" cy="12" r="1.5" /><circle cx="19" cy="12" r="1.5" />
        </svg>
    )
}

export function Like({ size = 18 }) { return <IconHeart size={size} /> }
export function Comment({ size = 17 }) { return <IconComment size={size} /> }

function IconAddPhoto({ size = 24 }) {
    return (
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M7 6H17C18.1046 6 19 6.89543 19 8V16C19 17.1046 18.1046 18 17 18H7C5.89543 18 5 17.1046 5 16V8C5 6.89543 5.89543 6 7 6Z" stroke="rgba(255,255,255,0.7)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
            <path d="M5 13L9 9L15 15L19 11" stroke="rgba(255,255,255,0.7)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
            <circle cx="9.5" cy="10.5" r="1.5" fill="rgba(255,255,255,0.7)" />
        </svg>
    )
}

export function CreationPost({ onPostCreated }: { onPostCreated?: () => void }) {
    const [isExpanded, setIsExpanded] = useState(false);
    const [textValue, setTextValue] = useState('');
    const [loading, setLoading] = useState(false);
    const [authorName, setAuthorName] = useState('Loading...');
    const [uploadedImageUrl, setUploadedImageUrl] = useState<string | null>(null);
    const [uploadingImage, setUploadingImage] = useState(false);
    const fileInputRef = useRef<HTMLInputElement>(null);

    React.useEffect(() => {
        api.get('/users/profile/me')
           .then(res => setAuthorName(res.data.visibleName))
           .catch(() => setAuthorName('Unknown'));
    }, []);

    const handlePublish = async () => {
        if (!textValue.trim() && !uploadedImageUrl) return;
        setLoading(true);
        try {
            await api.post('/posts', { 
                content: textValue || ' ', // API might require content
                postType: 'FEED',
                imageUrl: uploadedImageUrl 
            });
            
            setTextValue(''); 
            setUploadedImageUrl(null);
            setIsExpanded(false);
            if (onPostCreated) onPostCreated();
        } catch (err) {
            console.error("Error creating post", err);
            alert("Failed to publish post.");
        } finally {
            setLoading(false);
        }
    };

    const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files[0]) {
            setUploadingImage(true);
            setIsExpanded(true);
            try {
                const formData = new FormData();
                formData.append('file', e.target.files[0]);
                const uploadRes = await api.post('/upload', formData, {
                    headers: { 'Content-Type': 'multipart/form-data' }
                });
                setUploadedImageUrl(uploadRes.data);
            } catch (err) {
                console.error("Image upload failed", err);
                alert("Failed to upload image. Make sure server is running.");
            } finally {
                setUploadingImage(false);
            }
        }
    };

    return (
        <div
            className={`NewPost ${isExpanded ? 'expanded' : ''}`}
            tabIndex={-1}
            onBlur={(e) => {
                if (!e.currentTarget.contains(e.relatedTarget)) {
                    if (textValue.trim() === '') {
                        setIsExpanded(false);
                    }
                }
            }}
        >
            <div className="newPostTop">
                <div className="ProfileImage" />
                <div className="Nickname">{authorName}</div>
            </div>
            <textarea
                value={textValue}
                onChange={e => setTextValue(e.target.value)}
                maxLength={500}
                placeholder="What's on your mind?"
                className="addPostBar"
                onFocus={(e) => {
                    e.target.placeholder = '';
                    setIsExpanded(true);
                }}
                onBlur={(e) => {
                    if (e.target.value === '') {
                        e.target.placeholder = "What's on your mind?";
                    }
                }}
            />
            {(isExpanded || uploadedImageUrl || uploadingImage) && (
                <div style={{ display: 'flex', flexDirection: 'column', width: '100%', gap: '10px' }}>
                    {uploadingImage && <div style={{ color: '#ff7700', fontSize: '13px', padding: '10px' }}>Uploading Image...</div>}
                    {uploadedImageUrl && (
                        <div style={{ position: 'relative', width: '120px', borderRadius: '8px', overflow: 'hidden' }}>
                            <img src={`http://localhost:8080${uploadedImageUrl}`} alt="preview" style={{ width: '100%', display: 'block' }} />
                            <button 
                                onClick={() => setUploadedImageUrl(null)} 
                                style={{ position: 'absolute', top: 5, right: 5, background: 'rgba(0,0,0,0.5)', color: 'white', border: 'none', borderRadius: '50%', cursor: 'pointer', width: '24px', height: '24px' }}>
                                ×
                            </button>
                        </div>
                    )}
                    <div className="newPostActions" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                            <button className="iconActionBtn" onClick={() => fileInputRef.current?.click()} disabled={uploadingImage}>
                                <IconAddPhoto />
                            </button>
                            <input 
                                type="file" 
                                accept="image/*" 
                                style={{ display: 'none' }} 
                                ref={fileInputRef}
                                onChange={handleFileChange}
                            />
                        </div>
                        <button 
                            className="publishBtn" 
                            onClick={handlePublish}
                            disabled={loading || uploadingImage}
                        >
                            {loading ? 'Publishing...' : 'Publish'}
                        </button>
                    </div>
                </div>
            )}
        </div>
    )
}

export function Post({ post }: { post?: any }) {
    const navigate = useNavigate();

    const data = post || {
        id: 1,
        content: "Konečne som dokončil prvú časť svojho románu...",
        author: null,
        likeCount: 2400,
        commentCount: 148,
        repostCount: 32,
        hasImage: true
    };

    const [likes, setLikes] = useState(data.likeCount);
    const [isLiked, setIsLiked] = useState(false);
    const [reposts, setReposts] = useState(data.repostCount || 0);

    const handleLike = (e: React.MouseEvent) => {
        e.stopPropagation();
        if (isLiked) {
            // Unlike
            api.delete(`/posts/${data.id}/like`)
                .then(() => { setLikes((l: number) => Math.max(0, l - 1)); setIsLiked(false); })
                .catch(console.error);
        } else {
            // Like
            api.put(`/posts/${data.id}/like`)
                .then(() => { setLikes((l: number) => l + 1); setIsLiked(true); })
                .catch(console.error);
        }
    };

    const handleRepost = (e: React.MouseEvent) => {
        e.stopPropagation();
        api.put(`/posts/${data.id}/repost`)
           .then(() => setReposts(reposts + 1))
           .catch(console.error);
    };

    const HeartIcon = ({ filled, size = 22 }: { filled: boolean, size?: number }) => (
        <svg width={size} height={size} viewBox="0 0 24 24" fill={filled ? '#ff4757' : 'none'}>
            <path d="M12 21C12 21 3 15.5 3 9.5C3 7.015 4.985 5 7.5 5C8.986 5 10.306 5.71 11.155 6.808C11.568 7.344 11.775 7.612 12 7.612C12.225 7.612 12.432 7.344 12.845 6.808C13.694 5.71 15.014 5 16.5 5C19.015 5 21 7.015 21 9.5C21 15.5 12 21 12 21Z" stroke={filled ? '#ff4757' : 'rgba(255,255,255,0.45)'} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
    );

    return (
        <div className="Post">
            <UserInfoForPost author={data.author} />
            <div className="postContentWrap" onClick={() => navigate(`/post/${data.id}`)}>
                <div className="PostText">
                    {data.content}
                </div>
                {data.hasImage && data.imageUrl && (
                    <div className="postImage">
                        <img src={`http://localhost:8080${data.imageUrl}`} alt="post content" />
                    </div>
                )}
                {data.hasImage && !data.imageUrl && (
                    <div className="postImage">
                        <img src={BookImage} alt="book" />
                    </div>
                )}
            </div>
            <div className="PostLine" />
            <div className="PostAcvtivity">
                <div className="LikeActivity" onClick={handleLike} style={{ cursor: 'pointer', transition: 'color 0.2s', color: isLiked ? '#ff4757' : 'inherit' }}><HeartIcon filled={isLiked} /><span className="LikesAmount">{likes}</span></div>
                <div className="CommentActivity" onClick={() => navigate(`/post/${data.id}`)} style={{ cursor: 'pointer' }}><IconComment size={22} /><span className="CommentsAmount">{data.commentCount || 0}</span></div>
                <div className="ShareActivity" onClick={handleRepost} style={{ cursor: 'pointer' }}><IconShare size={22} /><span className="ShareAmount">{reposts}</span></div>
                <div className="MoreActivity"><IconMore size={26} /></div>
            </div>
        </div>
    )
}

export interface CommentProps {
    author: string;
    text: string;
    date: string;
    likes: number;
    replies?: React.ReactNode;
    onReplySubmit?: (text: string) => Promise<void>;
    commentType?: 'book' | 'post';
    id?: number;
}

export function CommentItem({ id, commentType = 'post', author, text, date, likes, replies, onReplySubmit }: CommentProps) {
    const [isReplying, setIsReplying] = useState(false);
    const [localLikes, setLocalLikes] = useState(likes);
    const [isLiked, setIsLiked] = useState(false);

    const handleLike = () => {
        if (isLiked || !id) return;
        const endpoint = commentType === 'book' ? `/book-comments/${id}/like` : `/comments/${id}/like`;
        api.put(endpoint).then(() => {
            setLocalLikes(localLikes + 1);
            setIsLiked(true);
        }).catch(console.error);
    };

    return (
        <div className="commentNode">
            <div className="commentContent">
                <div className="userAndDate">
                    <div className="user">
                        <div className="ProfileImage profileSmall"></div>
                        <div className="userInfoSmall">
                            <div className="NicknameForDecs">
                                {author} <span className="statusWrap"><IconFriends size={14} /></span>
                            </div>
                        </div>
                    </div>
                    <div className="date">{date}</div>
                </div>
                <div className="commentText">{text}</div>

                <div className="commentActions">
                    <div className="actionBtn" onClick={handleLike} style={{ cursor: 'pointer', color: isLiked ? '#4caf50' : 'inherit' }}><Like size={16} /> {localLikes}</div>
                    <div className="actionBtn" onClick={() => setIsReplying(!isReplying)}>
                        <Comment size={16} /> Reply
                    </div>
                </div>
            </div>

            {isReplying && (
                <div className="replyInputWrap">
                    <CreationCommentInline 
                        placeholder={`Replying to ${author}...`} 
                        onSubmit={async (text) => {
                            if (onReplySubmit) {
                                await onReplySubmit(text);
                                setIsReplying(false);
                            }
                        }}
                    />
                </div>
            )}

            {replies && (
                <div className="commentReplies">
                    {replies}
                </div>
            )}
        </div>
    );
}

export function NestedCommentList({ comments, onReplySubmit, commentType = 'post' }: { comments: any[], onReplySubmit: (parentId: number | null, text: string) => Promise<void>, commentType?: 'book' | 'post' }) {
    if (!comments || comments.length === 0) return null;
    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
            {comments.map((c: any) => (
                <CommentItem 
                    key={c.id} 
                    id={c.id}
                    commentType={commentType}
                    author={c.author?.visibleName || 'Unknown'} 
                    text={c.content} 
                    date={new Date(c.createdAt).toLocaleDateString()} 
                    likes={c.likes || 0}
                    onReplySubmit={async (text) => await onReplySubmit(c.id, text)}
                    replies={<NestedCommentList comments={c.replies || []} onReplySubmit={onReplySubmit} commentType={commentType} />}
                />
            ))}
        </div>
    );
}

export function CreationCommentInline({ placeholder = "Leave a comment...", placeholderButton = "Reply", onSubmit }: { placeholder?: string, placeholderButton?: string, onSubmit?: (text: string) => Promise<void> }) {
    const [isFocused, setIsFocused] = useState(false);
    const [textValue, setTextValue] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async () => {
        if (!textValue.trim()) return;
        setLoading(true);
        if (onSubmit) {
            await onSubmit(textValue).catch(console.error);
        }
        setTextValue('');
        setIsFocused(false);
        setLoading(false);
    };

    return (
        <div className={`commentCreationInline ${isFocused ? 'focused' : ''}`}>
            <div className="ProfileImage profileSmall"></div>
            <div className="inputArea">
                <textarea
                    className="commentCreationInputInline"
                    placeholder={placeholder}
                    value={textValue}
                    onChange={e => setTextValue(e.target.value)}
                    onFocus={() => setIsFocused(true)}
                    onBlur={(e) => {
                        if (e.target.value === '') setIsFocused(false);
                    }}
                />
                {isFocused && (
                    <div className="commentSubmitRow">
                        <button 
                            className="commentPostBtn" 
                            onClick={handleSubmit}
                            disabled={loading}
                        >
                            {loading ? '...' : placeholderButton}
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
}
