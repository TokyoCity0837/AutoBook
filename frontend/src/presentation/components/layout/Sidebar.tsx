import '../../../assets/styles/Sidebar.css';
import { Link, NavLink } from 'react-router-dom';
import { IconFeed, IconLibrary, IconFriends, IconRequests, IconSettings, IconLogout } from '../ui/Icons';
import { useUser } from '../../../shared/contexts/UserContext';
import { MEDIA_BASE_URL } from '../../../shared/constants/config';

function SettingsActive() {
    return <div className="SettingsActive"><IconSettings /></div>;
}

export function Sidebar() {
    const { profileMe } = useUser();
    const avatarUrl = profileMe?.profileImage ? `${MEDIA_BASE_URL}${profileMe.profileImage}` : null;

    return (
        <div className="sidebar">
            <div className="sidebarTop">
                <NavLink to="/profile" className="sidebarProfile">
                    {({ isActive }) => (
                        <div
                            className={isActive ? 'sidebarProfileActive' : 'sidebarProfile'}
                            style={avatarUrl ? { backgroundImage: `url(${avatarUrl})`, backgroundSize: 'cover', backgroundPosition: 'center' } : {}}
                        />
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
            <Link to="/login" className="logout"><IconLogout /></Link>
        </div>
    );
}
