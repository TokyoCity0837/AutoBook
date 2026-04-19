import { Link } from 'react-router-dom';
import { IconFriends } from '../ui/Icons';

interface FriendCardProps {
    user: any;
    friend?: boolean;
}

export function FriendCard({ user, friend = true }: FriendCardProps) {
    if (!user) return null;
    return (
        <div className='friendCard'>
            <div className='friendCardBanner'></div>
            <div className='friendCardAvatar'></div>
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
