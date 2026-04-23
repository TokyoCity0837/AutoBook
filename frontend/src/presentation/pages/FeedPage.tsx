import { useNavigate } from 'react-router-dom';
import { useFeed, useUserSearch } from '../hooks/useFeed';
import Post from '../components/post/Post';
import { CreationPost } from '../components/post/CreationPost';
// import { Update } from '../components/ui/Updates';
import { MEDIA_BASE_URL } from '../../shared/constants/config';
import '../../assets/styles/pages.css';

function SearchIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M15.7955 15.8111L21 21M18 10.5C18 14.6421 14.6421 18 10.5 18C6.35786 18 3 14.6421 3 10.5C3 6.35786 6.35786 3 10.5 3C14.6421 3 18 6.35786 18 10.5Z" stroke="#ebebeb" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

export default function FeedPage() {
  const { posts, loading, refresh } = useFeed();
  const { query, setQuery, results } = useUserSearch();
  const navigate = useNavigate();

  if (loading) return <div>Loading Feed...</div>;

  return (
    <div>
      <div className="searchAndUpdates">
        <div className='search' style={{ position: 'relative' }}>
          <div className='searchIcon'><SearchIcon /></div>
          <input
            type="text" maxLength={50}
            placeholder="Search for authors and books"
            className="searchBar"
            value={query}
            onChange={e => setQuery(e.target.value)}
            onFocus={e => e.target.placeholder = ''}
            onBlur={e => e.target.placeholder = 'Search for authors and books'}
          />
          {results.length > 0 && query && (
            <div className="searchDropdown" style={{ position: 'absolute', top: '100%', left: 0, right: 0, background: '#12151c', border: '1.5px solid rgba(255, 255, 255, 0.05)', borderRadius: '20px', zIndex: 10, maxHeight: '300px', overflowY: 'auto' }}>
              {results.map((u: any) => (
                <div key={u.id} style={{ padding: '10px 15px', borderBottom: '1.5px solid rgba(255, 255, 255, 0.05)', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '10px' }} onClick={() => navigate(`/profile/${u.id}`)}>
                  <div style={{
                    width: '30px', height: '30px', borderRadius: '50%',
                    background: u.profileImage ? `url(${MEDIA_BASE_URL}${u.profileImage}) center/cover` : '#fff'
                  }} />
                  <div>
                    <div style={{ fontSize: '16px', color: '#fff' }}>{u.visibleName}</div>
                    <div style={{ fontSize: '14px', color: '#888' }}>@{u.username}</div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
        <div className="UpdatesWrapper">
          {/* <div className="UpdatesContainer" /> */}
        </div>
      </div>

      <div className='PostsContainer'>
        <CreationPost onPostCreated={refresh} />
        {posts.map((post: any) => (
          <Post key={post.id} post={post} />
        ))}
      </div>
    </div>
  );
}
