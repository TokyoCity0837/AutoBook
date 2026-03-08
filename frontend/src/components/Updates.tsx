import './Updates.css'
import '../pages/pages.css'
import book1 from '../assets/svgs/pictures/book1.jpg'
import { UserInfoForPost } from './Posts';

export function Update(){
    return(
        <div className='updateBlock'>
            <UserInfoForPost />
            <div className="bookInfo">
                <div className='bookImg'>
                    <img src={book1} alt="book" className='imgUpdate'/>
                </div>
                <div className="bookText">
                    <div className="bookName">Wind in the willows</div>
                    <div className="bookAuthors">Anton Hrimov Andrii Dosyn </div>
                </div>
            </div>

        </div>
    );
}