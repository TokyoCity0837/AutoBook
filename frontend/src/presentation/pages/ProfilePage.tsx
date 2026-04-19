import { useProfile } from '../hooks/useProfile';
import { useParams, Link } from 'react-router-dom';
import { Post } from '../components/post/Post';
import { IconUser } from '../components/ui/Icons';
import { MEDIA_BASE_URL, DEFAULT_COVER_GRADIENT } from '../../shared/constants/config';
import { useUser } from '../../shared/contexts/UserContext';
import '../../assets/styles/pages.css';
import '../../assets/styles/profilePage.css';

function BookForProfile({ book }: { book?: any }) {
    if (!book) return null;
    const coverUrl = book.coverImage ? `${MEDIA_BASE_URL}${book.coverImage}` : null;
    return (
        <Link to={`/book/${book.id || 1}`} className="bookCardProfile">
            <div className="bookImgProfile">
                {coverUrl ? (
                    <img src={coverUrl} alt={book.title} className='ImgBookProfile' width={150} height={190} style={{ objectFit: 'cover' }} />
                ) : (
                    <div className='ImgBookProfile' style={{
                        width: 150, height: 190, background: DEFAULT_COVER_GRADIENT,
                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                        borderRadius: '6px', color: 'white', fontSize: '12px',
                        textAlign: 'center', padding: '10px', fontWeight: 600
                    }}>
                        {book.title}
                    </div>
                )}
            </div>
            <div className="bookDescProfile">
                <div className="bookTitleProfile">{book.title}</div>
                <div className="bookAuthorsProfile">{book.author?.visibleName || "Author"}</div>
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

    return (
        <div className="profileWrap">
            <div className="mainProfileInfo">
                <div className="topBar">
                    <div className="profilePicAndName">
                        <div className="profileImage" style={avatarUrl ? {
                            backgroundImage: `url(${avatarUrl})`,
                            backgroundSize: 'cover',
                            backgroundPosition: 'center'
                        } : {}}>
                            {!avatarUrl && <IconUser />}
                        </div>
                        <div className="nameBox">
                            <div className="name">{profile.visibleName}</div>
                            <div className="nickname">@{profile.username}</div>
                            {id && profileMe && Number(id) !== profileMe.id && (
                                <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
                                    <button
                                        className={isFollowing ? "iconActionBtn" : "publishBtn"}
                                        onClick={toggleFollow}
                                        disabled={followLoading}
                                        style={{ padding: '8px 16px', fontSize: '14px', borderRadius: '20px', border: isFollowing ? '1px solid #444' : 'none', opacity: followLoading ? 0.6 : 1 }}>
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
                <div className="profileAndBooks">
                    <div className="authorBooks">
                        {profile.books.slice(0, 4).map((book: any) => (
                            <BookForProfile key={book.id} book={book} />
                        ))}
                    </div>
                </div>
                <div className="postsContainer">
                    {profile.posts.map((post: any) => (
                        <Post key={post.id} post={post} />
                    ))}
                </div>
            </div>
        </div>
    );
}
