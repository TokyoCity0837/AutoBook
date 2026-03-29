import '../assets/styles/pages.css'
import '../index.css'
import '../assets/styles/friendsPage.css'
import { Accept, Reject } from './EditsPage'
import { Link } from 'react-router-dom';
import React from 'react';
import { IconFriends } from '../components/Icons'

function FrinedForPage({ friend = true }) {
    return (
        <div className='user'>
            <div className='ProfileImage'></div>
            <div className='.userInfo'>
                <Link to="/profile" className='linkToProfile'>
                    <div className='Nickname'>Andrii Dosyn</div>
                </Link>
                {friend && (
                    <div className='Status'>
                        <IconFriends />
                        Friend
                    </div>
                )}
            </div>
        </div>
    );
}

function IncomingFrined() {
    return (
        <div className='user incomingUser'>
            <div className='ProfileImage'></div>
            <div className='.userInfo'>
                <Link to="/profile" className='linkToProfile'>
                    <div className='Nickname'>Andrii Dosyn</div>
                </Link>
                <div className="friendsActions">
                    <div className="addFriend">
                        Add <Accept size={16} className='acceptFriend' />
                    </div>
                    <div className="addFriend">
                        Reject <Reject className='acceptFriend' />
                    </div>
                </div>
            </div>
        </div>
    );
}

export default function Friends() {
    return (
        <div className='friendsPageWrap'>
            <div className="YBText">Your friends</div>
            <div className="friendsWrap">
                <FrinedForPage />
                <FrinedForPage />
                <FrinedForPage />
                <FrinedForPage />
                <FrinedForPage />
            </div>
            <div className="YBText">Incoming requests</div>
            <div className="friendsWrap">
                <IncomingFrined />
                <IncomingFrined />
                <IncomingFrined />
                <IncomingFrined />
                <IncomingFrined />
                <IncomingFrined />
                <IncomingFrined />
            </div>
            <div className="YBText">Folowing</div>
            <div className="friendsWrap">
                <FrinedForPage friend={false} />
                <FrinedForPage friend={false} />
                <FrinedForPage friend={false} />
                <FrinedForPage friend={false} />
                <FrinedForPage friend={false} />
                <FrinedForPage friend={false} />
                <FrinedForPage friend={false} />
            </div>
        </div>
    );
}