import './Autobook.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from './Layout';
import Feed from './pages/FeedPage';
import Library from './pages/LibraryPage';
import Friends from './pages/FriendsPage';
import Edits from './pages/EditsPage';
import Settings from './pages/SettingsPage';
import Profile from './pages/ProfilePage';

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
        </Routes>
      </Layout>
    </Router>
  );
}

export default Autobook;