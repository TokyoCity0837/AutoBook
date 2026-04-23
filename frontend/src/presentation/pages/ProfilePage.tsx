import { useProfile } from '../hooks/useProfile';
import { useParams, Link } from 'react-router-dom';
import Post from '../components/post/Post';
import { IconUser, IconFriends } from '../components/ui/Icons';
import { MEDIA_BASE_URL, DEFAULT_COVER_GRADIENT } from '../../shared/constants/config';
import { useUser } from '../../shared/contexts/UserContext';
import '../../assets/styles/pages.css';
import '../../assets/styles/profilePage.css';
import { DefaultAvatar } from '../components/user/UserInfoForPost';

function BookForProfile({ book }: { book?: any }) {
    if (!book) return null;
    const coverUrl = book.coverImage ? `${MEDIA_BASE_URL}${book.coverImage}` : null;
    return (
        <Link to={`/book/${book.id || 1}`} className="bookCardProfile">
            <div className="bookImgProfile">
                {coverUrl ? (
                    <img src={coverUrl} alt={book.title} className='ImgBookProfile' width={150} height={190} style={{ objectFit: 'cover' }} />
                ) : (
                    <div className='ImgBookProfileNoImage' style={{background: DEFAULT_COVER_GRADIENT}}>
                        {book.title}
                    </div>
                )}
            </div>
            <div className="bookDescProfile">
                <div className="bookTitleProfile">{book.title}</div>
                <div className="bookAuthorsProfile">{book.author?.visibleName || "..."}</div>
            </div>
        </Link>
    );
}

export default function ProfilePage() {
    const { id } = useParams<{ id: string }>();
    const { profile, loading, isFollowing, followLoading, toggleFollow } = useProfile(id);
    const { profileMe } = useUser();

    if (loading) return <div className="profileWrap">Loading profile...</div>;
    if (!profile) return <div className="profileWrap">User not found</div>;

    const avatarUrl = profile.profileImage ? `${MEDIA_BASE_URL}${profile.profileImage}` : null;
    const isPrivate = profile.isPrivate;


    return (
        <div className="profileWrap">
            <div className="mainProfileInfo">
                <div className="topBar">
                    <div className="profilePicAndName">
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
                            <div className="profileImage"
                                 style={{
                                    marginTop: '-55px'
                                 }}>
                                <DefaultAvatar name={profile.visibleName} size={64} />
                            </div>
                        )}
                        <div className="nameBox">

                            <div className="name">                            
                                {profile.visibleName}
                                {profile.isFriend && (
                                <div className="friendBadge">
                                    <IconFriends size={32} />
                                </div>)} 
                            </div>
                            <div className="nickname">@{profile.username}</div>


                            {id && profileMe && Number(id) !== profileMe.id && (
                                <div style={{ display: 'flex', gap: '14px', marginTop: '14px' }}>
                                    <button
                                        className={isFollowing ? "publishBtn following" : "publishBtn follow"}
                                        onClick={toggleFollow}
                                        disabled={followLoading}
                                        style={{opacity: followLoading ? 0.6 : 1 }}>
                                        {followLoading ? '...' : (isFollowing ? 'Following' : 'Follow')}
                                    </button>
                                </div>
                            )}
                        </div>
                    </div>
                    <div className="folowersAndFriends">
                        <div className="ffBox">
                            <div className="ffLabel"><IconUser size={18} /> Followers</div>
                            <div className="ffCount">{profile.followers ?? 0}</div>
                        </div>
                        <div className="ffBox">
                            <div className="ffLabel"><IconUser size={18} /> Friends</div>
                            <div className="ffCount">{profile.friends ?? 0}</div>
                        </div>
                    </div>
                </div>
                <div className="aboutMe">{profile.bio}</div>
            </div>
            <div className="mainPoststAndInfo">
                {isPrivate ? (
                    <div className="privateProfileScreen">
                        <svg width="48" height="48" viewBox="0 0 24 24" fill="none">
                            <rect x="3" y="11" width="18" height="11" rx="2" stroke="currentColor" strokeWidth="1.5"/>
                            <path d="M7 11V7a5 5 0 0110 0v4" stroke="currentColor" strokeWidth="1.5"/>
                        </svg>
                        <div className="privateProfileTitle">This account is private</div>
                        <div className="privateProfileSub">Follow this account to see their posts and books</div>
                    </div>
                ) : (
                    <>
                        <div className="profileAndBooks">
                            <div className="authorBooks">
                            {profile.books.length === 0 ? (
                                <div style={{ 
                                    width: '400px',
                                    display: 'flex', 
                                    alignItems: 'center', 
                                    justifyContent: 'center',
                                    color: 'rgba(255,255,255,0.3)',
                                    flexDirection: 'column',
                                    gap: '8px',
                                    fontSize: '14px'
                                }}>
                                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none">
                                        <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
                                        <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" stroke="currentColor" strokeWidth="1.5"/>
                                    </svg>
                                    No books yet
                                </div>
                            ) : (
                                profile.books.slice(0, 4).map((book: any) => (
                                    <BookForProfile key={book.id} book={book} />
                                ))
                            )}

                            </div>
                        </div>
                        <div className="postsContainer">
                            {profile.posts.length === 0 ? (
                                <div className="emptyPosts">
                                    <svg width="40" height="40" viewBox="0 0 24 24" fill="none">
                                        <path d="M12 20h9M16.5 3.5a2.121 2.121 0 013 3L7 19l-4 1 1-4L16.5 3.5z" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
                                    </svg>
                                    <div>No posts yet</div>
                                </div>
                            ) : (
                                profile.posts.map((item: any, index: number) => (
                                    <Post
                                        key={`${item.type}-${item.post.id}-${item.activityAt}-${index}`}
                                        post={item.post}
                                        repostMeta={{
                                            isRepost: item.type === 'REPOST',
                                            repostedBy: item.repostedBy,
                                            repostedAt: item.repostedAt
                                        }}
                                    />
                                ))
                            )}
                        </div>
                    </>
                )}
            </div>
        </div>
    );
}
