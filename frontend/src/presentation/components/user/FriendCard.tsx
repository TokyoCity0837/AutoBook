import { Link } from 'react-router-dom';
import { IconFriends } from '../ui/Icons';
import { MEDIA_BASE_URL } from '../../../shared/constants/config';
import { DefaultAvatar } from './UserInfoForPost';
import type {UserCard} from "../../../domain/models";

interface FriendCardProps {
    user: UserCard;
    friend?: boolean;
}

export function FriendCard({ user, friend = true }: FriendCardProps) {
    if (!user) return null;

    const avatarUrl = user?.profileImage ? `${MEDIA_BASE_URL}${user.profileImage}` : null;
    return (
        <div className='friendCard'>
            <div className='friendCardBanner'></div>

            {avatarUrl ? (
                <div
                    className="friendCardAvatar"
                    style={{
                        backgroundImage: `url(${avatarUrl})`,
                        backgroundSize: 'cover',
                        backgroundPosition: 'center'
                    }}
                />
            ) : (
                <div className="friendCardAvatar">
                    <DefaultAvatar name={user?.visibleName} size={64} />
                </div>
            )}

            <div className='friendCardBody'>
                <Link to={`/profile/${user.id}`} className='linkToProfile'>
                    <div className='friendCardName'>{user.visibleName}</div>
                </Link>
                <div className='friendCardMeta'>@{user.username}</div>
                <div className='friendCardActions'>
                    {friend && <span style={{fontSize: '13px', color: '#888'}}><IconFriends size={14}/> Friend</span>}
                </div>
            </div>
        </div>
    );
}
