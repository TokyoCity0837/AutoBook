import React, { useState, useEffect } from 'react';
import api from '../api';
import '../assets/styles/pages.css';
import { Post, CreationPost } from "../components/Posts";
import { Update } from "../components/Updates";
import { useNavigate } from 'react-router-dom';

export function SearchIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <g id="SVGRepo_bgCarrier" stroke-width="0" />
      <g id="SVGRepo_iconCarrier">
        <path d="M15.7955 15.8111L21 21M18 10.5C18 14.6421 14.6421 18 10.5 18C6.35786 18 3 14.6421 3 10.5C3 6.35786 6.35786 3 10.5 3C14.6421 3 18 6.35786 18 10.5Z" stroke="#ebebeb" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
      </g>
    </svg>
  )
}

export default function Feed() {
  const [feedData, setFeedData] = useState<any>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<any[]>([]);
  const navigate = useNavigate();

  const fetchFeed = () => {
    setIsLoading(true);
    api.get('/posts/feed').then(res => {
      setFeedData({
        updates: [],
        posts: res.data
      });
      setIsLoading(false);
    }).catch(err => {
      console.error("Failed to load feed", err);
      setFeedData({ updates: [], posts: [] });
      setIsLoading(false);
    });
  };

  useEffect(() => {
    fetchFeed();
  }, []);

  useEffect(() => {
    if (searchQuery.trim().length === 0) {
      setSearchResults([]);
      return;
    }
    const delay = setTimeout(() => {
      api.get(`/users/search?username=${searchQuery}`)
        .then(res => setSearchResults(res.data))
        .catch(console.error);
    }, 300);
    return () => clearTimeout(delay);
  }, [searchQuery]);

  if (isLoading || !feedData) {
    return <div>Loading Feed...</div>;
  }

  return (
    <div>
      <div className="searchAndUpdates">
        <div className='search' style={{ position: 'relative' }}>
          <div className='searchIcon'><SearchIcon /></div>
          <input
            type="text"
            maxLength={50}
            placeholder="Search for authors and books"
            className="searchBar"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onFocus={e => e.target.placeholder = ''}
            onBlur={e => e.target.placeholder = 'Search for authors and books'}
          />
          {searchResults.length > 0 && searchQuery && (
            <div className="searchDropdown" style={{ position: 'absolute', top: '100%', left: 0, right: 0, background: '#1c1c1c', border: '1px solid #333', borderRadius: '8px', zIndex: 10, maxHeight: '300px', overflowY: 'auto' }}>
              {searchResults.map(u => (
                <div key={u.id} style={{ padding: '10px 15px', borderBottom: '1px solid #333', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '10px' }} onClick={() => navigate(`/profile/${u.id}`)}>
                  <div style={{ width: '30px', height: '30px', borderRadius: '50%', background: '#ff7700' }} />
                  <div>
                    <div style={{ fontSize: '14px', color: '#fff' }}>{u.visibleName}</div>
                    <div style={{ fontSize: '12px', color: '#888' }}>@{u.username}</div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="UpdatesWrapper">
          <div className="UpdatesContainer">
            {feedData.updates.map((update: any) => (
              <Update key={update.id} text={update.text} />
            ))}
          </div>
        </div>
      </div>

      <div className='PostsContainer'>
        <CreationPost onPostCreated={fetchFeed} />
        {feedData.posts.map((post: any) => (
          <Post key={post.id} post={post} />
        ))}
      </div>
    </div>
  )
}