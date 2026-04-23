import { Link } from 'react-router-dom';
import { DEFAULT_COVER_GRADIENT, MEDIA_BASE_URL } from '../../../shared/constants/config';
import type { BookCard as BookCardType } from '../../../domain/models';

interface BookCardProps {
    book: BookCardType;
    edit?: boolean;
}

function EditIcon() {
    return (
        <svg width="30" height="30" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <g clipPath="url(#clip0_2246_520)">
                <path d="M15.0733 4.53338L8.31576 11.2908C7.64285 11.9638 5.64533 12.2754 5.19908 11.8292C4.75283 11.3829 5.05741 9.38543 5.73033 8.71251L12.4949 1.94794C12.6617 1.76594 12.8637 1.61964 13.0887 1.51786C13.3136 1.41607 13.5568 1.36089 13.8036 1.35569C14.0504 1.3505 14.2958 1.39536 14.5248 1.4876C14.7538 1.57983 14.9618 1.71753 15.1361 1.89234C15.3104 2.06716 15.4475 2.27548 15.5391 2.50474C15.6307 2.73401 15.675 2.97944 15.6691 3.22626C15.6632 3.47308 15.6073 3.71618 15.505 3.94082C15.4026 4.16547 15.2557 4.36706 15.0733 4.53338Z" stroke="white" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                <path d="M7.79163 2.83334H4.24996C3.49851 2.83334 2.77789 3.13185 2.24653 3.66321C1.71518 4.19456 1.41663 4.91523 1.41663 5.66668V12.75C1.41663 13.5015 1.71518 14.2221 2.24653 14.7535C2.77789 15.2849 3.49851 15.5833 4.24996 15.5833H12.0416C13.607 15.5833 14.1666 14.3083 14.1666 12.75V9.20834" stroke="white" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
            </g>
            <defs>
                <clipPath id="clip0_2246_520">
                    <rect width="24" height="24" fill="white"/>
                </clipPath>
            </defs>
        </svg>
    );
}

export function BookCard({ book, edit = false }: BookCardProps) {
    const coverUrl = book.coverImage ? `${MEDIA_BASE_URL}${book.coverImage}` : null;
    return (
        <Link to={`/book/${book.id}`} className="bookCard">
            <div className="bookImg">
                {coverUrl ? (
                    <img src={coverUrl} alt={book.title} className='ImgBook' width={220} height={270} style={{ objectFit: 'cover' }} />
                ) : (
                    <div className='ImgBook noImage' style={{
                        width: 220, height: 270,
                        background: DEFAULT_COVER_GRADIENT,
                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                        borderRadius: '20px', borderBottomLeftRadius: '0px', borderBottomRightRadius: '0px'
                    }}>
                        <span className="noImageTitle">{book.title}</span>
                    </div>
                )}
            </div>
            <div className="bookDesc">
                <div className="bookTitle">{book.title}</div>
                <div className="bookAuthors">{book.author?.visibleName || "Unknown Author"}</div>
                <div className="editWrap">
                    {edit && <EditIcon />}
                </div>
            </div>
        </Link>
    );
}
