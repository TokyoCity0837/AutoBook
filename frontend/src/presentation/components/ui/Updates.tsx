import { IconFriends } from '../ui/Icons'
import '../../../assets/styles/Posts.css'

export function Update({ text = "Edit Comment" }: { text?: string }) {
    return (
        <div className="updateWrap">
            <div className="userAndDate">
                <div className='user'>
                    <div className='ProfileImage'></div>
                    <div className='.userInfo'>
                        <div className='NicknameForDecs'>Update <div className="statusWrap"><IconFriends size={14} /></div></div>
                    </div>
                </div>
                <div className="date">10.03.2026</div>
            </div>
            <div className="updateText">{text}</div>
        </div>
    );
}
