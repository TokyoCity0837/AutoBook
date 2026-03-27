import '../assets/styles/Sidebar.css'
import { Link, NavLink } from 'react-router-dom'

import { IconFeed, IconLibrary, IconFriends, IconRequests, IconSettings, IconLogout } from './Icons'

function ProfileActive() { return <div className="sidebarProfileActive" /> }
function ProfileDisabled() { return <div className="sidebarProfile" /> }

function SettingsActive() {
    return <div className="SettingsActive"><IconSettings /></div>
}

export default function Sidebar() {
    return (
        <div className="sidebar">
            <div className="sidebarTop">
                <NavLink to="/profile" className="sidebarProfile">
                    {({ isActive }) => (isActive ? <ProfileActive /> : <ProfileDisabled />)}
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
    )
}