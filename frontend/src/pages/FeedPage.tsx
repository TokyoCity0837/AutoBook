import './pages.css'
import {Post} from "../components/Posts"

export function SearchIcon(){
    return(
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <g id="SVGRepo_bgCarrier" stroke-width="0"/>
            <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"/>
            <g id="SVGRepo_iconCarrier"> <path d="M15.7955 15.8111L21 21M18 10.5C18 14.6421 14.6421 18 10.5 18C6.35786 18 3 14.6421 3 10.5C3 6.35786 6.35786 3 10.5 3C14.6421 3 18 6.35786 18 10.5Z" stroke="#ebebeb" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/> </g>
        </svg>
    );
}

export default function Feed() {
    return (
        <div className="content">
            <div className='search'>
                <div className='searchIcon'><SearchIcon /></div>
                <input type="text" maxLength={50} placeholder="Search for authors and books" className="searchBar"
                onFocus={(e) => e.target.placeholder = ''}
                onBlur={(e) => e.target.placeholder = 'Search for authors and books'}></input>
            </div>
            <div className='PostsContainer'>

                <Post />
                <Post />
                <Post />

            </div>

        </div>
    );
  }