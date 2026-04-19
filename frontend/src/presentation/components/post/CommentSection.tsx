import { useState } from 'react';
import { IconFriends } from '../ui/Icons';
import type { UserCard } from '../../../domain/models';
import { MEDIA_BASE_URL } from '../../../shared/constants/config';
import { useUser } from '../../../shared/contexts/UserContext';
import '../../../assets/styles/Posts.css';

// ─── Small icons ────────────────────────────────────────

function IconHeart({ size = 18 }) {
    return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
            <path d="M12 21C12 21 3 15.5 3 9.5C3 7.015 4.985 5 7.5 5C8.986 5 10.306 5.71 11.155 6.808C11.568 7.344 11.775 7.612 12 7.612C12.225 7.612 12.432 7.344 12.845 6.808C13.694 5.71 15.014 5 16.5 5C19.015 5 21 7.015 21 9.5C21 15.5 12 21 12 21Z" stroke="rgba(255,255,255,0.45)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
    );
}

function IconComment({ size = 17 }) {
    return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
            <path d="M21 12C21 16.418 16.97 20 12 20C10.5 20 9.093 19.673 7.857 19.09L3 20L4.338 16.063C3.493 14.973 3 13.542 3 12C3 7.582 7.03 4 12 4C16.97 4 21 7.582 21 12Z" stroke="rgba(255,255,255,0.45)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
    );
}

// ─── Inline comment creation ────────────────────────────

interface CreationCommentInlineProps {
    placeholder?: string;
    placeholderButton?: string;
    onSubmit?: (text: string) => Promise<void>;
}

export function CreationCommentInline({ placeholder = "Leave a comment...", placeholderButton = "Reply", onSubmit }: CreationCommentInlineProps) {
    const [isFocused, setIsFocused] = useState(false);
    const [textValue, setTextValue] = useState('');
    const [loading, setLoading] = useState(false);
    const { profileMe } = useUser();

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


    const avatarUrl = profileMe?.profileImage ? `${MEDIA_BASE_URL}${profileMe.profileImage}` : null;

    return (
        <div className={`commentCreationInline ${isFocused ? 'focused' : ''}`}>
            <div
            className="ProfileImage profileSmall"
            style={avatarUrl ? {
                backgroundImage: `url(${avatarUrl})`,
                backgroundSize: 'cover',
                backgroundPosition: 'center'
            } : {}}/>
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
                        <button className="commentPostBtn" onClick={handleSubmit} disabled={loading}>
                            {loading ? '...' : placeholderButton}
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
}

// ─── Comment item ───────────────────────────────────────

export interface CommentItemProps {
    author: UserCard;
    text: string;
    date: string;
    likes: number;
    replies?: React.ReactNode;
    onReplySubmit?: (text: string) => Promise<void>;
    onLike?: () => void;
    id?: number;
}

export function CommentItem({ author, text, date, likes, replies, onReplySubmit, onLike }: CommentItemProps) {
    const [isReplying, setIsReplying] = useState(false);
    const [localLikes, setLocalLikes] = useState(likes);
    const [isLiked, setIsLiked] = useState(false);

    const handleLike = () => {
        if (isLiked) return;
        if (onLike) onLike();
        setLocalLikes(localLikes + 1);
        setIsLiked(true);
    };

    const avatarUrl = author?.profileImage ? `${MEDIA_BASE_URL}${author.profileImage}` : null;

    return (
        <div className="commentNode">
            <div className="commentContent">
                <div className="userAndDate">
                    <div className="user">
                        <div
                        className="ProfileImage profileSmall"
                        style={avatarUrl ? {
                            backgroundImage: `url(${avatarUrl})`,
                            backgroundSize: 'cover',
                            backgroundPosition: 'center'
                        } : {}}
                         />

                        <div className="userInfoSmall">
                            <div className="NicknameForDecs">
                                {author.visibleName} <span className="statusWrap"><IconFriends size={14} /></span>
                            </div>
                        </div>
                    </div>
                    <div className="date">{date}</div>
                </div>
                <div className="commentText">{text}</div>
                <div className="commentActions">
                    <div className="actionBtn" onClick={handleLike} style={{ cursor: 'pointer', color: isLiked ? '#4caf50' : 'inherit' }}>
                        <IconHeart size={16} /> {localLikes}
                    </div>
                    <div className="actionBtn" onClick={() => setIsReplying(!isReplying)}>
                        <IconComment size={16} /> Reply
                    </div>
                </div>
            </div>

            {isReplying && (
                <div className="replyInputWrap">
                    <CreationCommentInline
                        placeholder={`Replying to ${author.visibleName}...`}
                        onSubmit={async (text) => {
                            if (onReplySubmit) {
                                await onReplySubmit(text);
                                setIsReplying(false);
                            }
                        }}
                    />
                </div>
            )}

            {replies && <div className="commentReplies">{replies}</div>}
        </div>
    );
}

// ─── Nested comment list (tree) ─────────────────────────

interface NestedCommentListProps {
    comments: any[];
    onReplySubmit: (parentId: number | null, text: string) => Promise<void>;
    onCommentLike?: (commentId: number) => void;
    commentType?: 'book' | 'post';
}

export function NestedCommentList({ comments, onReplySubmit, onCommentLike }: NestedCommentListProps) {
    if (!comments || comments.length === 0) return null;
    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
            {comments.map((c: any) => (
                <CommentItem
                    key={c.id}
                    id={c.id}
                    author={c.author || 'Unknown'}
                    text={c.content}
                    date={new Date(c.createdAt).toLocaleDateString()}
                    likes={c.likes || 0}
                    onLike={() => onCommentLike?.(c.id)}
                    onReplySubmit={async (text) => await onReplySubmit(c.id, text)}
                    replies={<NestedCommentList comments={c.replies || []} onReplySubmit={onReplySubmit} onCommentLike={onCommentLike} />}
                />
            ))}
        </div>
    );
}
