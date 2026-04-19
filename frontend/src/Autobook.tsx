import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import React from 'react';

import Layout from './Layout';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';

import Feed from './pages/FeedPage';
import Library from './pages/LibraryPage';
import Friends from './pages/FriendsPage';
import Edits from './pages/EditsPage';
import Settings from './pages/SettingsPage';
import Profile from './pages/ProfilePage';
import BookDetails from './pages/BookPage';
import EditorPage from './pages/EditorPage';
import PostPage from './pages/PostPage';

import { UserProvider } from './UserContext';
import ProtectedRoute from './ProtectedRoute';

export default function Autobook() {
  return (
    <Router>
      <Routes>
        {/* PUBLIC */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* APP */}
        <Route
          element={
            <UserProvider>
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            </UserProvider>
          }
        >
          <Route path="/" element={<Feed />} />
          <Route path="/library" element={<Library />} />
          <Route path="/friends" element={<Friends />} />
          <Route path="/requests" element={<Edits />} />
          <Route path="/settings" element={<Settings />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/profile/:id" element={<Profile />} />
          <Route path="/book/:id" element={<BookDetails />} />
          <Route path="/editor" element={<EditorPage />} />
          <Route path="/editor/:id" element={<EditorPage />} />
          <Route path="/post/:id" element={<PostPage />} />
        </Route>
      </Routes>
    </Router>
  );
}