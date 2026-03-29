import React from 'react';
import '../assets/styles/pages.css';
import '../assets/styles/PostPage.css';
import { Post, CommentItem, CreationCommentInline } from '../components/Posts';

function AuthorWidget() {
    return (
        <div className="authorWidget">
            <div className="authorWidgetTop">
                <div className="ProfileImage profileLarge"></div>
                <div className="authorWidgetInfo">
                    <div className="Nickname">Andrii Dosyn</div>
                    <div className="authorStats">24K Followers</div>
                </div>
            </div>
            <div className="authorBio">
                Fantasy writer. Coffee enthusiast. Creating worlds one line of code and one paragraph at a time. Author of "Wind in the willows".
            </div>
            <button className="followBtn">Follow</button>
        </div>
    );
}

function TrendingTagsWidget() {
    return (
        <div className="tagsWidget">
            <h4 className="widgetTitle">Trending Tags</h4>
            <div className="tagsList">
                <span className="tagItem">#Fantasy</span>
                <span className="tagItem">#WritingUpdate</span>
                <span className="tagItem">#ChapterOne</span>
                <span className="tagItem">#WorldBuilding</span>
                <span className="tagItem">#MagicSystem</span>
            </div>
        </div>
    );
}

export default function PostPage() {
    return (
        <div className="postPageWrap">
            <div className="postPageLayout">
                <div className="postPageMain">
                    <div className="postContainer">
                        <Post />
                    </div>

                    <div className="commentsSection">
                        <h3 className="commentsHeader">Comments (24)</h3>

                        <div className="mainCommentInput">
                            <CreationCommentInline placeholder="Write a comment..." placeholderButton="Comment" />
                        </div>

                        <div className="commentsTree">
                            <CommentItem
                                author="User 1"
                                date="2 hours ago"
                                text="This is exactly what I was feeling when reading that chapter! The atmosphere you described is spot on."
                                likes={12}
                                replies={
                                    <CommentItem
                                        author="Andrii Dosyn"
                                        date="1 hour ago"
                                        text="Thank you! I really tried to capture that melancholic vibe."
                                        likes={4}
                                    />
                                }
                            />

                            <CommentItem
                                author="User 2"
                                date="5 hours ago"
                                text="Can't wait for the next part. Do you have a release date in mind?"
                                likes={8}
                                replies={
                                    <>
                                        <CommentItem
                                            author="Andrii Dosyn"
                                            date="4 hours ago"
                                            text="Hopefully by next month. Just finishing up the editing."
                                            likes={5}
                                            replies={
                                                <CommentItem
                                                    author="User 2"
                                                    date="3 hours ago"
                                                    text="Awesome, take your time!"
                                                    likes={2}
                                                />
                                            }
                                        />
                                    </>
                                }
                            />

                            <CommentItem
                                author="User 3"
                                date="1 day ago"
                                text="I loved the detailed world-building here."
                                likes={15}
                            />
                        </div>
                    </div>
                </div>

                <div className="postPageSidebar">
                    <AuthorWidget />
                    <TrendingTagsWidget />
                </div>
            </div>
        </div>
    );
}
