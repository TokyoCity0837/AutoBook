import './pages.css'
import '../index.css'
import './friendsPage.css'
import { Accept, Reject } from './EditsPage'

function FrinedForPage() {
    return (
        <div className='user'>
            <div className='ProfileImage'></div>
            <div className='.userInfo'>
                <div className='Nickname'>Andrii Dosyn</div>
            </div>
        </div>
    );
}

function IncomingFrined() {
    return (
        <div className='user incomingUser'>
            <div className='ProfileImage'></div>
            <div className='.userInfo'>
                <div className='Nickname'>Andrii Dosyn</div>
                <div className="friendsActions">
                    <div className="addFriend">
                        Add <Accept size={16} className='acceptFriend'/>
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
        </div>
    );
  }