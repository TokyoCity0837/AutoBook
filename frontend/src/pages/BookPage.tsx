import '../assets/styles/pages.css'
import BookImage1 from '../assets/pictures/book1.jpg'
import '../assets/styles/BookPage.css'
import { IconFriends } from '../components/Icons'
import { CommentItem, CreationCommentInline } from '../components/Posts'
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

                    <div style={{ marginBottom: '25px', borderBottom: '1px solid rgba(255,255,255,0.08)', paddingBottom: '30px' }}>
                        <CreationCommentInline placeholder="Write a comment..." placeholderButton='Comment' />
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '25px', paddingLeft: '20px' }}>
                        <CommentItem
                            author="User 1"
                            date="2 days ago"
                            text="I loved the detailed world-building here."
                            likes={12}
                            replies={
                                <CommentItem
                                    author="Andrii Dosyn"
                                    date="1 day ago"
                                    text="Thank you! I'm glad you noticed the world-building."
                                    likes={4}
                                />
                            }
                        />
                        <CommentItem
                            author="User 2"
                            date="3 days ago"
                            text="Super amazing book! I couldn't put it down."
                            likes={18}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
}