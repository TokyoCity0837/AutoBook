import React, { useState, useEffect } from 'react';
import api from '../api';
import '../assets/styles/pages.css';
import '../assets/styles/PostPage.css';
import { useParams } from 'react-router-dom';
import { Post, CommentItem, CreationCommentInline, NestedCommentList } from '../components/Posts';

function AuthorWidget({ author }: { author?: any }) {
    if (!author) return null;
    return (
        <div className="authorWidget">
            <div className="authorWidgetTop">
                <div className="ProfileImage profileLarge"></div>
                <div className="authorWidgetInfo">
                    <div className="Nickname">{author.visibleName}</div>
                    <div className="authorStats">{author.followersCount || "24K"} Followers</div>
                </div>
            </div>
            <div className="authorBio">
                {author.bio || "Fantasy writer. Coffee enthusiast. Creating worlds..."}
            </div>
            <button className="followBtn">Follow</button>
        </div>
    );
}

function TrendingTagsWidget({ tags }: { tags?: string[] }) {
    const list = tags || ["#Fantasy", "#WritingUpdate"];
    return (
        <div className="tagsWidget">
            <h4 className="widgetTitle">Trending Tags</h4>
            <div className="tagsList">
                {list.map((tag, i) => (
                    <span key={i} className="tagItem">{tag}</span>
                ))}
            </div>
        </div>
    );
}

export default function PostPage() {
    const { id } = useParams<{ id: string }>();
    const [pageData, setPageData] = useState<any>(null);
    const [isLoading, setIsLoading] = useState(true);

    const loadData = () => {
        if (!id) return;
        Promise.all([
            api.get(`/posts/${id}`),
            api.get(`/comments/post/${id}`)
        ]).then(([postRes, comRes]) => {
            const post = postRes.data;
            setPageData({
                postDetails: post,
                author: post.author,
                tags: [],
                comments: comRes.data || []
            });
            setIsLoading(false);
        }).catch(error => {
            console.error("Error loading post", error);
            setIsLoading(false);
        });
    };

    useEffect(() => {
        loadData();
    }, [id]);

    const handleCommentSubmit = async (parentId: number | null, content: string) => {
        await api.post(`/comments/post/${id}`, { content, parentId });
        loadData(); // Re-fetch tree
    };

    if (isLoading || !pageData) {
        return <div className="postPageWrap">Loading...</div>;
    }

    return (
        <div className="postPageWrap">
            <div className="postPageLayout">
                <div className="postPageMain">
                    <div className="postContainer">
                        <Post post={pageData.postDetails} />
                    </div>

                    <div className="commentsSection">
                        <h3 className="commentsHeader">Comments ({pageData.comments.length})</h3>

                        <div className="mainCommentInput">
                            <CreationCommentInline 
                                placeholder="Write a comment..." 
                                placeholderButton="Comment" 
                                onSubmit={async (text) => await handleCommentSubmit(null, text)}
                            />
                        </div>

                        <div className="commentsTree" style={{ marginTop: '30px' }}>
                            <NestedCommentList comments={pageData.comments} onReplySubmit={handleCommentSubmit} />
                        </div>
                    </div>
                </div>

                <div className="postPageSidebar">
                    <AuthorWidget author={pageData.author} />
                    <TrendingTagsWidget tags={pageData.tags} />
                </div>
            </div>
        </div>
    );
}
