import { usePostPage } from '../hooks/usePostPage';
import { Link, useParams } from 'react-router-dom';
import { Post } from '../components/post/Post';
import { CreationCommentInline, NestedCommentList } from '../components/post/CommentSection';
import { MEDIA_BASE_URL } from '../../shared/constants/config';
import '../../assets/styles/pages.css';
import '../../assets/styles/PostPage.css';

function AuthorWidget({ author }: { author?: any }) {
    if (!author) return null;

    const avatarUrl = author?.profileImage ? `${MEDIA_BASE_URL}${author.profileImage}` : null;

    return (
        <div className="authorWidget">
            <Link to={author?.id ? `/profile/${author.id}` : "#"} className="authorWidgetTop Nickname">
                <div
                className="ProfileImage profileLarge"
                style={avatarUrl ? {
                    backgroundImage: `url(${avatarUrl})`,
                    backgroundSize: 'cover',
                    backgroundPosition: 'center'
                } : {}}/>
                <div className="authorWidgetInfo">
                    <div className="Nickname">{author.visibleName}</div>
                </div>
            </Link>

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
    const { post, comments, loading, submitComment, likeComment } = usePostPage(id);

    if (loading || !post) return <div className="postPageWrap">Loading...</div>;

    return (
        <div className="postPageWrap">
            <div className="postPageLayout">
                <div className="postPageMain">
                    <div className="postContainer">
                        <Post post={post} />
                    </div>
                    <div className="commentsSection">
                        <h3 className="commentsHeader">Comments ({comments.length})</h3>
                        <div className="mainCommentInput">
                            <CreationCommentInline
                                
                                placeholder="Write a comment..."
                                placeholderButton="Comment"
                                onSubmit={async (text) => await submitComment(null, text)}
                            />
                        </div>
                        <div className="commentsTree" style={{ marginTop: '30px' }}>
                            <NestedCommentList
                                comments={comments}
                                onReplySubmit={submitComment}
                                onCommentLike={likeComment}
                            />
                        </div>
                    </div>
                </div>
                <div className="postPageSidebar">
                    <AuthorWidget author={(post as any).author} />
                    <TrendingTagsWidget />
                </div>
            </div>
        </div>
    );
}
