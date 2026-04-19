import { useState, useEffect } from 'react';
import api from '../api';
import '../assets/styles/pages.css';
import { IconUser } from '../components/Icons';
import '../assets/styles/profilePage.css';
import { Post } from '../components/Posts';
import { Link, useParams } from 'react-router-dom';

const DEFAULT_COVER_GRADIENT = 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)';

function BookForProfile({ book }: { book?: any }) {
    if (!book) return null;
    const coverUrl = book.coverImage ? `http://localhost:8080${book.coverImage}` : null;
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

export default function Profile() {
    const { id } = useParams<{ id: string }>();
    const [profileData, setProfileData] = useState<any>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isFollowing, setIsFollowing] = useState(false);
    const [followLoading, setFollowLoading] = useState(false);

    useEffect(() => {
        const fetchId = id || 'me';
        setIsLoading(true);
        api.get(`/users/profile/${fetchId}`)
            .then(response => {
                setProfileData(response.data);
                setIsLoading(false);
            })
            .catch(error => {
                console.error("Error fetching profile", error);
                setProfileData(null);
                setIsLoading(false);
            });

        if (id && id !== 'me') {
            api.get(`/follows/status/${id}`)
               .then(res => setIsFollowing(res.data === true))
               .catch(() => setIsFollowing(false));
        }
    }, [id]);

    const handleFollowToggle = async () => {
        if (!id || id === 'me' || followLoading) return;
        setFollowLoading(true);
        try {
            if (!isFollowing) {
                await api.post(`/follows/direct/${id}`);
                setIsFollowing(true);
            } else {
                await api.delete(`/follows/remove/${id}`);
                setIsFollowing(false);
            }
        } catch (err: any) {
            if (err.response?.status === 409) {
                setIsFollowing(true);
            } else {
                console.error('Follow toggle failed', err);
            }
        } finally {
            setFollowLoading(false);
        }
    };

    if (isLoading) return <div className="profileWrap">Loading profile...</div>;
    if (!profileData) return <div className="profileWrap">User not found or log in required</div>;

    const avatarUrl = profileData.profileImage ? `http://localhost:8080${profileData.profileImage}` : null;

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
                            <div className="name">{profileData.visibleName}</div>
                            <div className="nickname">@{profileData.username}</div>
                            {id && id !== 'me' && (
                                <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
                                    <button 
                                        className={isFollowing ? "iconActionBtn" : "publishBtn"}
                                        onClick={handleFollowToggle}
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
                            <div className="ffCount">{profileData.followers ?? 0}</div>
                            <div className="ffLabel"><IconUser size={18} /> Followers</div>
                        </div>
                        <div className="ffBox">
                            <div className="ffCount">{profileData.friends ?? 0}</div>
                            <div className="ffLabel"><IconUser size={18} /> Friends</div>
                        </div>
                    </div>
                </div>
                <div className="aboutMe">
                    {profileData.bio}
                </div>
            </div>

            <div className="mainPoststAndInfo">
                <div className="profileAndBooks">
                    <div className="authorBooks">
                        {profileData.books.slice(0, 4).map((book: any) => (
                            <BookForProfile key={book.id} book={book} />
                        ))}
                    </div>
                </div>
                <div className="postsContainer">
                    {profileData.posts.map((post: any) => (
                        <Post key={post.id} post={post} />
                    ))}
                </div>
            </div>
        </div>
    );
}
