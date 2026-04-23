import '../../../assets/styles/Sidebar.css';
import { NavLink, useNavigate } from 'react-router-dom';
import { IconFeed, IconLibrary, IconFriends, IconRequests, IconSettings, IconLogout } from '../ui/Icons';
import { DefaultAvatar } from '../user/UserInfoForPost';
import { useUser } from '../../../shared/contexts/UserContext';
import { MEDIA_BASE_URL } from '../../../shared/constants/config';
import { authRepository } from '../../../data/repositories';
import { useState } from 'react';

function SettingsActive() {
    return <div className="SettingsActive"><IconSettings /></div>;
}

export function Sidebar() {
    // const { id } = useParams<{ id: string }>();
    const { profileMe, clearUser } = useUser();
    const navigate = useNavigate();
    const avatarUrl = profileMe?.profileImage ? `${MEDIA_BASE_URL}${profileMe.profileImage}` : null;
    
    const [showLogoutModal, setShowLogoutModal] = useState(false);

    const handleLogout = async () => {
        try {
            await authRepository.logout();
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            clearUser();
            navigate('/login');
        }
    };

    return (
        <div className="sidebar">
            <div className="sidebarTop">
            <NavLink to="/profile" className="sidebarProfile">
                    {avatarUrl ? (
                        <div
                            className="sidebarProfile"
                            style={{
                                backgroundImage: `url(${avatarUrl})`,
                                backgroundSize: 'cover',
                                backgroundPosition: 'center'
                            }}
                        />
                    ) : (
                        <DefaultAvatar name={profileMe?.visibleName ? profileMe?.visibleName : ''} size={46} />
                    )}
                </NavLink>
                <div className="sidebarLine" />
            </div>
            <div className="sidebarBottom">
                <NavLink to="/" className="icon">
                    {({ isActive }) => <span style={{ color: isActive ? '#fff' : undefined }}><IconFeed /></span>}
                </NavLink>
                <NavLink to="/library" className="icon">
                    {({ isActive }) => <span style={{ color: isActive ? '#fff' : undefined }}><IconLibrary /></span>}
                </NavLink>
                <NavLink to="/friends" className="icon">
                    {({ isActive }) => <span style={{ color: isActive ? '#fff' : undefined }}><IconFriends /></span>}
                </NavLink>
                <NavLink to="/requests" className="icon">
                    {({ isActive }) => <span style={{ color: isActive ? '#fff' : undefined }}><IconRequests /></span>}
                </NavLink>
            </div>
            <NavLink to="/settings" className="settings">
                {({ isActive }) => (isActive ? <SettingsActive /> : <IconSettings />)}
            </NavLink>
            <div className="logout" onClick={() => setShowLogoutModal(true)}><IconLogout /></div>

            {showLogoutModal && (
                <div className="modalOverlay" onClick={() => setShowLogoutModal(false)}>
                    <div className="modalContent" onClick={e => e.stopPropagation()}>
                        <h3>Logout</h3>
                        <p>Are you sure you want to log out?</p>
                        <div className="modalActions">
                            <button className="modalBtn cancelBtn" onClick={() => setShowLogoutModal(false)}>Cancel</button>
                            <button className="modalBtn confirmBtn" onClick={handleLogout}>Log Out</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
