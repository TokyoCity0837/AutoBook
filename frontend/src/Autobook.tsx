import './Autobook.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from './Layout';
import Feed from './pages/FeedPage';
import Library from './pages/LibraryPage';
import Friends from './pages/FriendsPage';
import Edits from './pages/EditsPage';
import Settings from './pages/SettingsPage';
import Profile from './pages/ProfilePage';
import BookDetails from './pages/BookPage';
import EditorPage from './pages/EditorPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import PostPage from './pages/PostPage';
import React from 'react';

function Autobook() {
  return (
    <Router>
      <Layout>
        <Routes>
          <Route path="/" element={<Feed />} />
          <Route path="/library" element={<Library />} />
          <Route path="/friends" element={<Friends />} />
          <Route path="/requests" element={<Edits />} />
          <Route path="/settings" element={<Settings />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/book" element={<BookDetails />} />
          <Route path="/editor" element={<EditorPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/post/:id" element={<PostPage />} />
        </Routes>
      </Layout>
    </Router>
  );
}

export default Autobook;