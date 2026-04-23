import { useFriends } from '../hooks/useFriends';
import { FriendCard } from '../components/user/FriendCard';
import '../../assets/styles/pages.css';
import '../../assets/styles/index.css';
import '../../assets/styles/friendsPage.css';

export default function FriendsPage() {
    const { friends, following, loading, followers } = useFriends();

    if (loading) return <div className='friendsPageWrap'>Loading Social Space...</div>;

    return (
        <div className='friendsPageWrap'>
            <div className="YBText">Your friends ({friends.length})</div>
            <div className="friendsWrap">
                {friends.map((user: any) => (
                    <FriendCard key={user.id} user={user} friend={true} />
                ))}
            </div>

            <div className="YBText">Following</div>
            <div className="friendsWrap">
                {following.map((user: any) => (
                    <FriendCard key={user.id} user={user} friend={false} />
                ))}
            </div>

            <div className="YBText">Followers</div>
            <div className="friendsWrap">
                {followers.map((user: any) => (
                    <FriendCard key={user.id} user={user} friend={false} />
                ))}
            </div>
        </div>
    );
}
