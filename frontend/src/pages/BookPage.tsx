import '../assets/styles/pages.css'
import BookImage1 from '../assets/pictures/book1.jpg'
import '../assets/styles/BookPage.css'
import { IconFriends } from '../components/Icons'
import { Like, Comment } from '../components/Posts'
import React from 'react'

function Plus() {
    return (
        <svg width="32" height="32" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M6 12H18M12 6V18" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
        </svg>
    );
}

export function Update({ text = "Edit Comment Edit Comment Edit Comment Edit Comment Edit Comment Edit Comment" }) {
    return (
        <div className="updateWrap">
            <div className="userAndDate">
                <div className='user'>
                    <div className='ProfileImage'></div>
                    <div className='.userInfo'>
                        <div className='NicknameForDecs'>Andrii Dosyn <div className="statusWrap"><IconFriends /></div></div>
                    </div>
                </div>
                <div className="date">10.03.2026</div>
            </div>
            <div className="updateText">{text}</div>

        </div>
    );
}

function CreationComment() {
    return (
        <div className="commentCreation">
            <div className="commentCreationTop">
                <div className='ProfileImage'></div>
                <textarea
                    className="commentCreationInput"
                    placeholder="Leave a comment..."
                    maxLength={500}
                    onFocus={e => e.target.placeholder = ''}
                    onBlur={e => e.target.placeholder = 'Leave a comment...'}
                />
            </div>
            <div className="commentCreationActions">
                <button className="commentSubmitBtn">Comment</button>
            </div>
        </div>
    );
}

function CommentBook() {
    return (
        <div className="commentBlock">
            <Update text="Super amazing book!" />
            <div className="likeAndForwards">
                <div className="likesComment"><Like size={24} /></div>2.4k
                <div className="forwardsComment"><Comment size={20} /></div>512
            </div>
        </div>
    );
}

export default function BookDetails() {
    return (
        <div className="bookDetailsWrap">
            <div className="bookImageDesc">
                <img src={BookImage1} alt="book" className='ImgBookDecs' />
                <div className="bookTitleDesc">Wind in the willows</div>
                <div className="bookAuthorsDesc">Anton Hrimov, Andrii Dosyn</div>
            </div>
            <div className="bookInfoDecs">
                <div className="bookDescription">Lorem ipsum dolor sit amet consectetur adipiscing elit. Quisque faucibus ex sapien vitae pellentesque sem placerat. In id cursus mi pretium tellus duis convallis. Tempus leo eu aenean sed diam urna tempor. Pulvinar vivamus fringilla lacus nec metus bibendum egestas. Iaculis massa nisl malesuada lacinia integer nunc posuere. Ut hendrerit semper vel class aptent taciti sociosqu. Ad litora torquent per conubia nostra inceptos himenaeos.</div>
                <div className="addToLibrary">Add to library <Plus /></div>
                <div className="updatesBlock">
                    <Update />
                    <Update />
                    <Update />
                    <Update />
                </div>
                <div className="splitLine"></div>
                <div className="comments">
                    <CreationComment />
                    <CommentBook />
                    <CommentBook />
                    <CommentBook />
                    <CommentBook />
                    <CommentBook />
                    <CommentBook />
                    <CommentBook />
                </div>
            </div>
        </div>
    );
}