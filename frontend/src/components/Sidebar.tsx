import '../assets/styles/Sidebar.css'
import { Link, NavLink, useParams } from 'react-router-dom'

import { IconFeed, IconLibrary, IconFriends, IconRequests, IconSettings, IconLogout } from './Icons'
import { useUser } from '../UserContext';

function SettingsActive() {
    return <div className="SettingsActive"><IconSettings /></div>
}


export default function Sidebar() {
    const { profile } = useUser();
  
    const avatarUrl = profile?.profileImage ? `http://localhost:8080${profile.profileImage}` : null;
  
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
            <div className="sidebarLine" /></div>
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
    )
}