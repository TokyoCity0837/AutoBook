import { Link } from 'react-router-dom';
import { IconFriends } from '../ui/Icons';
import { MEDIA_BASE_URL } from '../../../shared/constants/config';
import type { UserCard } from '../../../domain/models';

export function DefaultAvatar({ name, size = 40 }: { name: string; size?: number }) {
    const initials = name
        .split(' ')
        .map(w => w[0])
        .join('')
        .toUpperCase()
        .slice(0, 2);

    const colors = ['#6366f1', '#8b5cf6', '#ec4899', '#f59e0b', '#10b981', '#3b82f6'];
    const colorIndex = name.charCodeAt(0) % colors.length;
    const bg = colors[colorIndex];

    return (
        <div style={{
            width: size, height: size,
            borderRadius: '50%',
            background: bg,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
            fontSize: size * 0.38,
            fontWeight: 600,
            flexShrink: 0,
            userSelect: 'none',
            textDecoration: 'none'
        }}>
            {initials}
        </div>
    );
}

export function UserInfoForPost({ author }: { author?: UserCard }) {
    const name = author?.visibleName || "Unknown Author";
    const avatarUrl = author?.profileImage ? `${MEDIA_BASE_URL}${author.profileImage}` : null;

    return (
        <div className="user">
            {avatarUrl ? (
                <div
                    className="profileImage"
                    style={{
                        backgroundImage: `url(${avatarUrl})`,
                        backgroundSize: 'cover',
                        backgroundPosition: 'center'
                    }}
                />
            ) : (
                <DefaultAvatar name={name} size={64} />
            )}
            <div className="userInfo">
                <Link to={author?.id ? `/profile/${author.id}` : "#"} className="Nickname">
                    {name}
                </Link>
                {author?.isFriend && ( 
                    <div className="Status"><IconFriends size={14} />Friend</div>
                )}
            </div>
        </div>
    );
}
