import '../assets/styles/Posts.css'
import { IconFriends } from './Icons'
import BookImage from '../assets/pictures/BookImage.jpeg'
import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'

export function UserInfoForPost() {
    return (
        <div className="user">
            <div className="ProfileImage" />
            <div className="userInfo">
                <Link to="/profile" className="Nickname">Andrii Dosyn</Link>
                <div className="Status"><IconFriends size={14} />Friend</div>
            </div>
        </div>
    )
}

function IconHeart({ size = 18 }) {
    return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
            <path d="M12 21C12 21 3 15.5 3 9.5C3 7.015 4.985 5 7.5 5C8.986 5 10.306 5.71 11.155 6.808C11.568 7.344 11.775 7.612 12 7.612C12.225 7.612 12.432 7.344 12.845 6.808C13.694 5.71 15.014 5 16.5 5C19.015 5 21 7.015 21 9.5C21 15.5 12 21 12 21Z" stroke="rgba(255,255,255,0.45)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
    )
}

function IconComment({ size = 17 }) {
    return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
            <path d="M21 12C21 16.418 16.97 20 12 20C10.5 20 9.093 19.673 7.857 19.09L3 20L4.338 16.063C3.493 14.973 3 13.542 3 12C3 7.582 7.03 4 12 4C16.97 4 21 7.582 21 12Z" stroke="rgba(255,255,255,0.45)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
    )
}

function IconShare({ size = 17 }) {
    return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
            <path d="M13.47 4.14C12.74 4.36 12.28 5.96 12.09 7.91C6.78 7.91 2 13.48 2 20.08C4.19 14.08 8.99 12.45 12.14 12.45C12.34 14.21 12.79 15.62 13.47 15.82C15.57 16.43 22 12.44 22 9.98C22 7.52 15.57 3.53 13.47 4.14Z" stroke="rgba(255,255,255,0.45)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
    )
}

function IconMore({ size = 18 }) {
    return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="rgba(255,255,255,0.35)">
            <circle cx="5" cy="12" r="1.5" /><circle cx="12" cy="12" r="1.5" /><circle cx="19" cy="12" r="1.5" />
        </svg>
    )
}

export function Like({ size = 18 }) { return <IconHeart size={size} /> }
export function Comment({ size = 17 }) { return <IconComment size={size} /> }

function IconAddPhoto({ size = 24 }) {
    return (
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M7 6H17C18.1046 6 19 6.89543 19 8V16C19 17.1046 18.1046 18 17 18H7C5.89543 18 5 17.1046 5 16V8C5 6.89543 5.89543 6 7 6Z" stroke="rgba(255,255,255,0.7)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
            <path d="M5 13L9 9L15 15L19 11" stroke="rgba(255,255,255,0.7)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
            <circle cx="9.5" cy="10.5" r="1.5" fill="rgba(255,255,255,0.7)" />
        </svg>
    )
}

export function CreationPost() {
    const [isExpanded, setIsExpanded] = useState(false);
    const [textValue, setTextValue] = useState('');

    return (
        <div
            className={`NewPost ${isExpanded ? 'expanded' : ''}`}
            tabIndex={-1}
            onBlur={(e) => {
                if (!e.currentTarget.contains(e.relatedTarget)) {
                    if (textValue.trim() === '') {
                        setIsExpanded(false);
                    }
                }
            }}
        >
            <div className="newPostTop">
                <div className="ProfileImage" />
                <div className="Nickname">Andrii Dosyn</div>
            </div>
            <textarea
                value={textValue}
                onChange={e => setTextValue(e.target.value)}
                maxLength={500}
                placeholder="What's on your mind?"
                className="addPostBar"
                onFocus={(e) => {
                    e.target.placeholder = '';
                    setIsExpanded(true);
                }}
                onBlur={(e) => {
                    if (e.target.value === '') {
                        e.target.placeholder = "What's on your mind?";
                    }
                }}
            />
            {isExpanded && (
                <div className="newPostActions">
                    <button className="iconActionBtn">
                        <IconAddPhoto />
                    </button>
                    <button className="publishBtn">Publish</button>
                </div>
            )}
        </div>
    )
}

export function Post() {
    const navigate = useNavigate();

    return (
        <div className="Post">
            <UserInfoForPost />
            <div className="postContentWrap" onClick={() => navigate('/post/1')}>
                <div className="PostText">
                    Konečne som dokončil prvú časť svojho románu. Dlho som hľadal
                    správny hlas pre túto príbeh — tmavý, melancholický, ale s nádejou na
                    konci. Dúfam, že vás prvá kapitola zaujme rovnako ako mňa pri písaní
                </div>
                <div className="postImage">
                    <img src={BookImage} alt="book" />
                </div>
            </div>
            <div className="PostLine" />
            <div className="PostAcvtivity">
                <div className="LikeActivity"><IconHeart size={22} /><span className="LikesAmount">2.4k</span></div>
                <div className="CommentActivity" onClick={() => navigate('/post/1')}><IconComment size={22} /><span className="CommentsAmount">148</span></div>
                <div className="ShareActivity"><IconShare size={22} /><span className="ShareAmount">32</span></div>
                <div className="MoreActivity"><IconMore size={26} /></div>
            </div>
        </div>
    )
}

export interface CommentProps {
    author: string;
    text: string;
    date: string;
    likes: number;
    replies?: React.ReactNode;
}

export function CommentItem({ author, text, date, likes, replies }: CommentProps) {
    const [isReplying, setIsReplying] = useState(false);

    return (
        <div className="commentNode">
            <div className="commentContent">
                <div className="userAndDate">
                    <div className="user">
                        <div className="ProfileImage profileSmall"></div>
                        <div className="userInfoSmall">
                            <div className="NicknameForDecs">
                                {author} <span className="statusWrap"><IconFriends size={14} /></span>
                            </div>
                        </div>
                    </div>
                    <div className="date">{date}</div>
                </div>
                <div className="commentText">{text}</div>

                <div className="commentActions">
                    <div className="actionBtn"><Like size={16} /> {likes}</div>
                    <div className="actionBtn" onClick={() => setIsReplying(!isReplying)}>
                        <Comment size={16} /> Reply
                    </div>
                </div>
            </div>

            {isReplying && (
                <div className="replyInputWrap">
                    <CreationCommentInline placeholder={`Replying to ${author}...`} />
                </div>
            )}

            {replies && (
                <div className="commentReplies">
                    {replies}
                </div>
            )}
        </div>
    );
}

export function CreationCommentInline({ placeholder = "Leave a comment...", placeholderButton = "Reply" }) {
    const [isFocused, setIsFocused] = useState(false);
    return (
        <div className={`commentCreationInline ${isFocused ? 'focused' : ''}`}>
            <div className="ProfileImage profileSmall"></div>
            <div className="inputArea">
                <textarea
                    className="commentCreationInputInline"
                    placeholder={placeholder}
                    onFocus={() => setIsFocused(true)}
                    onBlur={(e) => {
                        if (e.target.value === '') setIsFocused(false);
                    }}
                />
                {isFocused && (
                    <div className="commentSubmitRow">
                        <button className="commentPostBtn">{placeholderButton}</button>
                    </div>
                )}
            </div>
        </div>
    );
}
