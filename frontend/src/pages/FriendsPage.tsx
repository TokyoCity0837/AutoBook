import '../assets/styles/pages.css';
import '../index.css';
import '../assets/styles/friendsPage.css';
import { Link } from 'react-router-dom';
import { useState, useEffect } from 'react';
import api from '../api';
import { IconFriends } from '../components/Icons';

export function FriendForPage({ user, friend = true }: { user?: any, friend?: boolean }) {
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

export default function Friends() {
    const [friendsData, setFriendsData] = useState<any>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        Promise.all([
            api.get('/follows/friends/me'),
            api.get('/follows/following')
        ]).then(([friendsRes, followingRes]) => {
            // getFriends returns FollowResponse[] where the friend is in .follower
            // (because the endpoint returns followers filtered by mutual)
            const friendUsers = friendsRes.data.map((f: any) => f.follower);
            const friendIds = new Set(friendUsers.map((u: any) => u.id));
            
            // Following list: the user I follow is in .following
            const followingUsers = followingRes.data.map((f: any) => f.following);
            const pureFollowing = followingUsers.filter((u: any) => !friendIds.has(u.id));

            setFriendsData({
                friends: friendUsers,
                following: pureFollowing
            });
            setIsLoading(false);
        }).catch(err => {
            console.error("Failed to load friends", err);
            setFriendsData({ friends: [], following: [] });
            setIsLoading(false);
        });
    }, []);

    if (isLoading || !friendsData) {
        return <div className='friendsPageWrap'>Loading Social Space...</div>;
    }

    return (
        <div className='friendsPageWrap'>
            <div className="YBText">Your friends ({friendsData.friends.length})</div>
            <div className="friendsWrap">
                {friendsData.friends.map((user: any) => (
                    <FriendForPage key={user.id} user={user} friend={true} />
                ))}
            </div>
            
            <div className="YBText">Following</div>
            <div className="friendsWrap">
                {friendsData.following.map((user: any) => (
                    <FriendForPage key={user.id} user={user} friend={false} />
                ))}
            </div>
        </div>
    );
}