import '../assets/styles/pages.css'
import '../index.css'
import '../assets/styles/friendsPage.css'
import { Accept, Reject } from './EditsPage'
import { Link } from 'react-router-dom';
import React from 'react';
import { IconFriends } from '../components/Icons'

function FriendForPage({ friend = true }) {
    return (
        // <div className='user'>
        //     <div className='ProfileImage'></div>
        //     <div className='userInfo'>
        //         <Link to="/profile" className='linkToProfile'>
        //             <div className='Nickname'>Andrii Dosyn</div>
        //         </Link>
        //         {friend && (
        //             <div className='Status'>
        //                 <IconFriends />
        //                 Friend
        //             </div>
        //         )}
        //     </div>
        // </div>
        <div className='friendCard'>
        <div className='friendCardBanner'></div>
        <div className='friendCardAvatar'></div>
        <div className='friendCardBody'>
            <Link to="/profile" className='linkToProfile'>
                <div className='friendCardName'>Andrii Dosyn</div>
            </Link>
            <div className='friendCardMeta'>@d.osid.osid.osid.osi</div>
            <div className='friendCardActions'>
            </div>
        </div>
    </div>
    );
}

function IncomingFriendCard() {
    return (
        <div className='friendCard'>
            <div className='friendCardBanner'></div>
            <div className='friendCardAvatar'></div>
            <div className='friendCardBody'>
                <Link to="/profile" className='linkToProfile'>
                    <div className='friendCardName'>Andrii Dosyn</div>
                </Link>
                <div className='friendCardMeta'>@d.osid.osid.osid.osi</div>
                <div className='friendCardActions'>
                    <button className='friendCardBtn friendCardBtn--accept'>
                        Add
                    </button>
                    <button className='friendCardBtn friendCardBtn--reject'>
                        Reject
                    </button>
                </div>
            </div>
        </div>
    );
}

export default function Friends() {
    return (
        <div className='friendsPageWrap'>
            <div className="YBText">Incoming requests</div>
            <div className="friendsWrap friendsWrap--cards">
                <IncomingFriendCard />
                <IncomingFriendCard />
                <IncomingFriendCard />
                <IncomingFriendCard />
                <IncomingFriendCard />
                <IncomingFriendCard />
            </div>
            <div className="YBText">Your friends</div>
            <div className="friendsWrap">
                <FriendForPage />
                <FriendForPage />
                <FriendForPage />
                <FriendForPage />
                <FriendForPage />
            </div>
            <div className="YBText">Following</div>
            <div className="friendsWrap">
                <FriendForPage friend={false} />
                <FriendForPage friend={false} />
                <FriendForPage friend={false} />
                <FriendForPage friend={false} />
            </div>
        </div>
    );
}