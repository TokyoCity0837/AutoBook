import { Link } from 'react-router-dom';
import { IconFriends } from '../ui/Icons';
import { MEDIA_BASE_URL } from '../../../shared/constants/config';
import type { UserCard } from '../../../domain/models';

export function UserInfoForPost({ author }: { author?: UserCard }) {
    const name = author?.visibleName || "Unknown Author";
    const avatarUrl = author?.profileImage ? `${MEDIA_BASE_URL}${author.profileImage}` : null;
    return (
        <div className="user">
            <div
                className="profileImage"
                style={avatarUrl ? {
                    backgroundImage: `url(${avatarUrl})`,
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
