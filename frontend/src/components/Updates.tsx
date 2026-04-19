import '../assets/styles/Updates.css'
import '../assets/styles/pages.css'
import book1 from '../assets/pictures/book1.jpg'
import { UserInfoForPost } from './Posts';
import React from 'react';


export function Update({ text, update }: { text?: string, update?: any }) {
    const data = update || {
        bookName: "Wind in the willows",
        bookAuthors: "Anton Hrimov Andrii Dosyn"
    };
    return (
        <div className='updateBlock'>
            <UserInfoForPost author={data.author} />
            <div className="bookInfo">
                <div className='bookImg'>
                    <img src={book1} alt="book" className='imgUpdate' />
                </div>
                <div className="bookText">
                    <div className="bookName">{data.bookName}</div>
                    <div className="bookAuthors">{data.bookAuthors}</div>
                    {/* {text && <div className="updateText" style={{color: '#999', fontSize: '13px', marginTop: '5px'}}>{text}</div>} */}
                </div>
            </div>
        </div>
    );
}