import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

import { Layout } from '../presentation/components/layout/Layout';
import LoginPage from '../presentation/pages/LoginPage';
import RegisterPage from '../presentation/pages/RegisterPage';
import FeedPage from '../presentation/pages/FeedPage';
import LibraryPage from '../presentation/pages/LibraryPage';
import FriendsPage from '../presentation/pages/FriendsPage';
import EditsPage from '../presentation/pages/EditsPage';
import SettingsPage from '../presentation/pages/SettingsPage';
import ProfilePage from '../presentation/pages/ProfilePage';
import BookPage from '../presentation/pages/BookPage';
import EditorPage from '../presentation/pages/EditorPage';
import PostPage from '../presentation/pages/PostPage';

import { UserProvider } from '../shared/contexts/UserContext';
import { ProtectedRoute } from './ProtectedRoute';

export default function Autobook() {
  return (
    <Router>
      <Routes>
        {/* PUBLIC */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* APP — protected */}
        <Route
          element={
            <UserProvider>
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            </UserProvider>
          }
        >
          <Route path="/" element={<FeedPage />} />
          <Route path="/library" element={<LibraryPage />} />
          <Route path="/friends" element={<FriendsPage />} />
          <Route path="/requests" element={<EditsPage />} />
          <Route path="/settings" element={<SettingsPage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/profile/:id" element={<ProfilePage />} />
          <Route path="/book/:id" element={<BookPage />} />
          <Route path="/editor" element={<EditorPage />} />
          <Route path="/editor/:id" element={<EditorPage />} />
          <Route path="/post/:id" element={<PostPage />} />
        </Route>
      </Routes>
    </Router>
  );
}
