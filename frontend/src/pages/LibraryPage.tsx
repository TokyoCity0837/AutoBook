import './pages.css'
import BookImage1 from '../assets/svgs/pictures/book1.jpg'
import './Library.css'

export function Edit() {
    return (

            <svg width="30" height="30" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <g clip-path="url(#clip0_2246_520)">
            <path d="M15.0733 4.53338L8.31576 11.2908C7.64285 11.9638 5.64533 12.2754 5.19908 11.8292C4.75283 11.3829 5.05741 9.38543 5.73033 8.71251L12.4949 1.94794C12.6617 1.76594 12.8637 1.61964 13.0887 1.51786C13.3136 1.41607 13.5568 1.36089 13.8036 1.35569C14.0504 1.3505 14.2958 1.39536 14.5248 1.4876C14.7538 1.57983 14.9618 1.71753 15.1361 1.89234C15.3104 2.06716 15.4475 2.27548 15.5391 2.50474C15.6307 2.73401 15.675 2.97944 15.6691 3.22626C15.6632 3.47308 15.6073 3.71618 15.505 3.94082C15.4026 4.16547 15.2557 4.36706 15.0733 4.53338Z" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M7.79163 2.83334H4.24996C3.49851 2.83334 2.77789 3.13185 2.24653 3.66321C1.71518 4.19456 1.41663 4.91523 1.41663 5.66668V12.75C1.41663 13.5015 1.71518 14.2221 2.24653 14.7535C2.77789 15.2849 3.49851 15.5833 4.24996 15.5833H12.0416C13.607 15.5833 14.1666 14.3083 14.1666 12.75V9.20834" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </g>
            <defs>
            <clipPath id="clip0_2246_520">
            <rect width="24" height="24" fill="white"/>
            </clipPath>
            </defs>
            </svg>
    );
}

export function Book({edit = false}) {
    return (
        <div className="bookCard">
            <div className="bookImg">
                <img src={BookImage1} alt="book" className='ImgBook' width={220} height={270}/>
            </div>
            <div className="bookDesc">
                <div className="bookTitle">Wind in the willows</div>
                <div className="bookAuthors">Anton Hrimov Andrii Dosyn</div>
                <div className="editWrap">
                    {edit && <Edit />}    
                </div>
            </div>    
        </div>
    );
}

export function Plus() {
    return (
        <svg width="128" height="128" viewBox="0 0 164 164" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path fill-rule="evenodd" clip-rule="evenodd" d="M112.75 87.125H87.125V112.75C87.125 115.569 84.8341 117.875 82 117.875C79.1659 117.875 76.875 115.569 76.875 112.75V87.125H51.25C48.4159 87.125 46.125 84.8188 46.125 82C46.125 79.1812 48.4159 76.875 51.25 76.875H76.875V51.25C76.875 48.4312 79.1659 46.125 82 46.125C84.8341 46.125 87.125 48.4312 87.125 51.25V76.875H112.75C115.584 76.875 117.875 79.1812 117.875 82C117.875 84.8188 115.584 87.125 112.75 87.125ZM82 0C36.7104 0 0 36.695 0 82C0 127.305 36.7104 164 82 164C127.29 164 164 127.305 164 82C164 36.695 127.29 0 82 0Z" fill="white"/>
        </svg>

    );
}

export default function Library() {
    return (
            <div className="libraryWrap">
                <div className="yourBooks">                   
                        <div className="YBText">Your books</div>
                    <div className="booksWrap">
                        <div className="border">
                            <div className="plus">
                                <Plus />
                            </div>  
                            {/* <div className="bookDesc">
                                <div className="bookTitle">New book</div>
                            </div>  */}
                        </div>
                        <Book edit={true} />
                        <Book edit={true} />
                        <Book edit={true} />
                        <Book edit={true} />
                        <Book edit={true} />
                        <Book edit={true} />
                        <Book edit={true} />
                        <Book edit={true} />
                    </div>
                </div>

                <div className="library">
                <div className="YBText">Library</div>
                    <div className="booksWrap">
                        <Book />
                        <Book />
                        <Book />
                        <Book />
                        <Book />
                    </div>
                </div>
            </div>
    );
  }