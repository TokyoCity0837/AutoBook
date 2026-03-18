import React from 'react'
import '../assets/styles/Sidebar.css'
import { Link, NavLink } from 'react-router-dom'

// ─── Icons (24×24 stroke, уніфіковані) ───────────────────────────
function IconFeed() {
    return <svg width="32" height="32" viewBox="0 0 24 24" fill="none"><path d="M20.965 7C20.887 5.128 20.637 3.98 19.828 3.172C18.657 2 16.771 2 13 2H11C7.229 2 5.343 2 4.172 3.172C3 4.343 3 6.229 3 10V14C3 17.771 3 19.657 4.172 20.828C5.343 22 7.229 22 11 22H13C16.771 22 18.657 22 19.828 20.828C21 19.657 21 17.771 21 14V11" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/><path d="M6 12C6 10.586 6 9.879 6.44 9.44C6.879 9 7.586 9 9 9H15C16.414 9 17.121 9 17.56 9.44C18 9.879 18 10.586 18 12V16C18 17.414 18 18.121 17.56 18.56C17.121 19 16.414 19 15 19H9C7.586 19 6.879 19 6.44 18.56C6 18.121 6 17.414 6 16V12Z" stroke="currentColor" strokeWidth="1.5"/><path d="M7 6H12" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/></svg>
}
function IconLibrary() {
    return <svg width="32" height="32" viewBox="0 0 24 24" fill="none"><path d="M4 19V6.2C4 5.08 4 4.52 4.218 4.092C4.41 3.716 4.716 3.41 5.092 3.218C5.52 3 6.08 3 7.2 3H16.8C17.92 3 18.48 3 18.908 3.218C19.284 3.41 19.59 3.716 19.782 4.092C20 4.52 20 5.08 20 6.2V17H6C4.895 17 4 17.895 4 19ZM4 19C4 20.105 4.895 21 6 21H20M9 7H15M9 11H15M19 17V21" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/></svg>
}
function IconFriends() {
    return <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M16 11C17.657 11 19 9.657 19 8C19 6.343 17.657 5 16 5" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/><path d="M18 20C19.105 20 20 19.105 20 18C20 16.343 18.21 15 16 15" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/><path d="M9 11C10.657 11 12 9.657 12 8C12 6.343 10.657 5 9 5C7.343 5 6 6.343 6 8C6 9.657 7.343 11 9 11Z" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/><path d="M15 20C15 20 15 18.343 9 18.343C3 18.343 3 20 3 20" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/></svg>
}
function IconRequests() {
    return <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M21.28 6.4L11.74 15.94C10.79 16.89 7.97 17.33 7.34 16.7C6.71 16.07 7.14 13.25 8.09 12.3L17.64 2.75C18.08 2.28 18.7 2 19.36 2C20.02 2 20.64 2.28 21.08 2.75C21.52 3.22 21.77 3.86 21.77 4.52C21.77 5.18 21.52 5.82 21.08 6.29L21.28 6.4Z" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/><path d="M11 4H6C4.939 4 3.922 4.421 3.172 5.172C2.421 5.922 2 6.939 2 8V18C2 19.061 2.421 20.078 3.172 20.828C3.922 21.579 4.939 22 6 22H17C19.21 22 20 20.2 20 18V13" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/></svg>
}
function IconSettings() {
    return <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M15 12C15 13.657 13.657 15 12 15C10.343 15 9 13.657 9 12C9 10.343 10.343 9 12 9C13.657 9 15 10.343 15 12Z" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/><path d="M12.905 3.06C12.699 3 12.466 3 12 3C11.534 3 11.301 3 11.095 3.06C10.794 3.148 10.528 3.328 10.335 3.575C10.202 3.744 10.116 3.96 9.943 4.393C9.694 5.015 9.004 5.335 8.369 5.123L7.798 4.933C7.393 4.798 7.19 4.73 6.992 4.719C6.7 4.702 6.41 4.77 6.157 4.916C5.985 5.015 5.834 5.166 5.532 5.468C5.211 5.789 5.051 5.949 4.949 6.132C4.799 6.401 4.736 6.709 4.768 7.016C4.789 7.224 4.873 7.434 5.042 7.856C5.306 8.515 5.052 9.269 4.443 9.634L4.165 9.801C3.74 10.056 3.528 10.183 3.374 10.359C3.237 10.514 3.134 10.696 3.071 10.893C3 11.116 3 11.366 3 11.866C3 12.459 3 12.755 3.095 13.009C3.178 13.233 3.314 13.434 3.491 13.595C3.692 13.777 3.964 13.886 4.509 14.104C5.065 14.326 5.352 14.944 5.162 15.513L4.947 16.158C4.798 16.605 4.724 16.829 4.717 17.049C4.709 17.313 4.77 17.574 4.896 17.807C5 18 5.167 18.167 5.5 18.5C5.833 18.833 6 19 6.193 19.104C6.426 19.23 6.687 19.291 6.951 19.283C7.171 19.276 7.395 19.202 7.842 19.053L8.369 18.877C9.004 18.665 9.694 18.986 9.943 19.607C10.116 20.04 10.202 20.256 10.335 20.425C10.528 20.672 10.794 20.852 11.095 20.94C11.301 21 11.534 21 12 21C12.466 21 12.699 21 12.905 20.94C13.206 20.852 13.472 20.672 13.665 20.425C13.798 20.256 13.884 20.04 14.057 19.607C14.306 18.986 14.996 18.665 15.631 18.877L16.158 19.053C16.605 19.202 16.829 19.276 17.048 19.283C17.312 19.291 17.574 19.23 17.806 19.104C18 19 18.166 18.833 18.5 18.5C18.833 18.167 19 18 19.104 17.807C19.229 17.574 19.291 17.313 19.283 17.049C19.276 16.829 19.201 16.605 19.052 16.158L18.837 15.513C18.648 14.944 18.934 14.326 19.491 14.104C20.036 13.886 20.308 13.777 20.509 13.595C20.686 13.434 20.822 13.233 20.905 13.009C21 12.755 21 12.459 21 11.866C21 11.366 21 11.116 20.929 10.893C20.866 10.696 20.763 10.514 20.626 10.359C20.472 10.183 20.26 10.056 19.835 9.801L19.557 9.634C18.948 9.269 18.694 8.515 18.958 7.856C19.126 7.434 19.211 7.224 19.232 7.016C19.264 6.709 19.2 6.401 19.051 6.132C18.949 5.949 18.788 5.789 18.468 5.468C18.166 5.166 18.015 5.015 17.843 4.916C17.589 4.77 17.299 4.702 17.008 4.719C16.809 4.73 16.607 4.798 16.202 4.933L15.631 5.123C14.996 5.335 14.306 5.015 14.057 4.393C13.884 3.96 13.798 3.744 13.665 3.575C13.472 3.328 13.206 3.148 12.905 3.06Z" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/></svg>
}
function IconLogout() {
    return <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M16 17L21 12M21 12L16 7M21 12H9" stroke="#A94242" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/><path d="M9 3H7C5.895 3 5 3.895 5 5V19C5 20.105 5.895 21 7 21H9" stroke="#A94242" strokeWidth="1.5" strokeLinecap="round"/></svg>
}

export function FriendFeed({ size = 18 }) {
    return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
            <path d="M16 11C17.657 11 19 9.657 19 8C19 6.343 17.657 5 16 5" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
            <path d="M9 11C10.657 11 12 9.657 12 8C12 6.343 10.657 5 9 5C7.343 5 6 6.343 6 8C6 9.657 7.343 11 9 11Z" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
            <path d="M15 20C15 20 15 18.343 9 18.343C3 18.343 3 20 3 20" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
        </svg>
    )
}

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