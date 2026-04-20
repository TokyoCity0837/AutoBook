import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { postRepository } from '../../../data/repositories';
import { UserInfoForPost } from '../user/UserInfoForPost';
import { MEDIA_BASE_URL } from '../../../shared/constants/config';
import BookImage from '../../../assets/pictures/BookImage.jpeg';
import '../../../assets/styles/Posts.css';
import type { Post as PostType } from '../../../domain/models';

// ─── Icon components (local) ────────────────────────────

function IconComment({ size = 17 }) {
    return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
            <path d="M21 12C21 16.418 16.97 20 12 20C10.5 20 9.093 19.673 7.857 19.09L3 20L4.338 16.063C3.493 14.973 3 13.542 3 12C3 7.582 7.03 4 12 4C16.97 4 21 7.582 21 12Z" stroke="rgba(255,255,255,0.45)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
    );
}

function IconShare({ size = 17 }) {
    return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
            <path d="M13.47 4.14C12.74 4.36 12.28 5.96 12.09 7.91C6.78 7.91 2 13.48 2 20.08C4.19 14.08 8.99 12.45 12.14 12.45C12.34 14.21 12.79 15.62 13.47 15.82C15.57 16.43 22 12.44 22 9.98C22 7.52 15.57 3.53 13.47 4.14Z" stroke="rgba(255,255,255,0.45)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
    );
}

function IconMore({ size = 18 }) {
    return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="rgba(255,255,255,0.35)">
            <circle cx="5" cy="12" r="1.5" /><circle cx="12" cy="12" r="1.5" /><circle cx="19" cy="12" r="1.5" />
        </svg>
    );
}

function HeartIcon({ filled, size = 22 }: { filled: boolean; size?: number }) {
    return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill={filled ? '#ff4757' : 'none'}>
            <path d="M12 21C12 21 3 15.5 3 9.5C3 7.015 4.985 5 7.5 5C8.986 5 10.306 5.71 11.155 6.808C11.568 7.344 11.775 7.612 12 7.612C12.225 7.612 12.432 7.344 12.845 6.808C13.694 5.71 15.014 5 16.5 5C19.015 5 21 7.015 21 9.5C21 15.5 12 21 12 21Z" stroke={filled ? '#ff4757' : 'rgba(255,255,255,0.45)'} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
    );
}

// ─── Post component ─────────────────────────────────────

interface PostProps {
    post?: PostType;
}

export function Post({ post }: PostProps) {
    const navigate = useNavigate();

    const data = post || {
        id: 1, content: "", author: null,
        likeCount: 0, commentCount: 0, repostCount: 0, hasImage: false, imageUrl: null
    } as any;

    const [likes, setLikes] = useState(data.likeCount);
    const [isLiked, setIsLiked] = useState(false);
    const [reposts, setReposts] = useState(data.repostCount || 0);

    const handleLike = (e: React.MouseEvent) => {
        e.stopPropagation();
        if (isLiked) {
            postRepository.unlike(data.id)
                .then(() => { setLikes((l: number) => Math.max(0, l - 1)); setIsLiked(false); })
                .catch(console.error);
        } else {
            postRepository.like(data.id)
                .then(() => { setLikes((l: number) => l + 1); setIsLiked(true); })
                .catch(console.error);
        }
    };

    const handleRepost = (e: React.MouseEvent) => {
        e.stopPropagation();
        postRepository.repost(data.id)
            .then(() => setReposts((r: number) => r + 1))
            .catch(console.error);
    };

    return (
        <div className="Post">
            <UserInfoForPost author={data.author} />
            <div className="postContentWrap" onClick={() => navigate(`/post/${data.id}`)}>
                <div className="PostText">{data.content}</div>
                {data.hasImage && data.imageUrl && (
                    <div className="postImage">
                        <img src={`${MEDIA_BASE_URL}${data.imageUrl}`} alt="post content" />
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
                <div className="LikeActivity" onClick={handleLike} style={{ cursor: 'pointer', transition: 'color 0.2s', color: isLiked ? '#ff4757' : 'inherit' }}>
                    <HeartIcon filled={isLiked} /><span className="LikesAmount">{likes}</span>
                </div>
                <div className="CommentActivity" onClick={() => navigate(`/post/${data.id}`)} style={{ cursor: 'pointer' }}>
                    <IconComment size={22} /><span className="CommentsAmount">{data.commentCount || 0}</span>
                </div>
                <div className="ShareActivity" onClick={handleRepost} style={{ cursor: 'pointer' }}>
                    <IconShare size={22} /><span className="ShareAmount">{reposts}</span>
                </div>
                <div className="MoreActivity"><IconMore size={26} /></div>
            </div>
        </div>
    );
}
