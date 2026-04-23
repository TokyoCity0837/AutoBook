import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { chapterRepository, bookRepository } from '../../data/repositories';
import { MEDIA_BASE_URL } from '../../shared/constants/config';
import '../../assets/styles/BookReadPage.css';

export default function BookReadPage() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();

    const [book, setBook] = useState<any>(null);
    const [chapters, setChapters] = useState<any[]>([]);
    const [activeIndex, setActiveIndex] = useState(0);
    const [loading, setLoading] = useState(true);
    const [sidebarOpen, setSidebarOpen] = useState(true);

    useEffect(() => {
        if (!id) return;
        Promise.all([
            bookRepository.getById(Number(id)),
            chapterRepository.getByBook(Number(id))
        ]).then(([bk, chs]) => {
            setBook(bk);
            setChapters(chs);
        }).finally(() => setLoading(false));
    }, [id]);

    if (loading) return <div className="readerWrap">Loading...</div>;
    if (!book || chapters.length === 0) return <div className="readerWrap">No content found.</div>;

    const allContent = chapters.map(ch => ch.content).join('');

    const scrollToChapter = (index: number) => {
        const anchors = document.querySelectorAll('.readerBody [data-anchor-id]');
        if (anchors[index]) {
            anchors[index].scrollIntoView({ behavior: 'smooth', block: 'start' });
            setActiveIndex(index);
        }
    };

    return (
        <div className="readerWrap">
            <div className={`readerSidebar ${sidebarOpen ? 'open' : 'closed'}`}>
                <div className="readerSidebarHeader">
                    <button className="readerBackBtn" onClick={() => navigate(`/book/${id}`)}>
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                            <path d="M19 12H5M5 12l7 7M5 12l7-7" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                        </svg>
                        Back
                    </button>
                    <button className="readerSidebarToggle" onClick={() => setSidebarOpen(v => !v)}>
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                            <path d={sidebarOpen ? "M15 18l-6-6 6-6" : "M9 18l6-6-6-6"} stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                        </svg>
                    </button>
                </div>

                <div className="readerBookInfo">
                    {book.coverImage && (
                        <img src={`${MEDIA_BASE_URL}${book.coverImage}`} alt="Cover" className="readerCover" />
                    )}
                    <div className="readerBookTitle">{book.title}</div>
                    <div className="readerBookAuthor">by {book.author?.visibleName}</div>
                </div>

                <div className="readerChapterList">
                    {chapters.map((ch, i) => (
                        <button
                            key={ch.id}
                            className={`readerChapterItem ${i === activeIndex ? 'active' : ''}`}
                            onClick={() => scrollToChapter(i)}>
                            <span className="readerChapterNum">{i + 1}</span>
                            {ch.title}
                        </button>
                    ))}
                </div>
            </div>


            <div className="readerContent">
                <div className="readerPaper">
                    <div
                        className="readerBody"
                        dangerouslySetInnerHTML={{ __html: allContent }}
                    />
                </div>
            </div>
        </div>
    );
}