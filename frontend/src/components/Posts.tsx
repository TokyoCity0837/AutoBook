import './Posts.css'
import { FriendFeed } from './Sidebar';
import BookImage from '../assets/svgs/pictures/BookImage.jpeg'

function Like(){
    return(
        <svg
        version="1.1"
        xmlns="http://www.w3.org/2000/svg"
        width="36px"
        height="36px"
        viewBox="0 0 32 32"
        xmlSpace="preserve"
        fill="#000000"
        >
        <g strokeWidth="0" />
        <g strokeLinecap="round" strokeLinejoin="round" />
        <g>
          <path
            className="bentblocks_een"
            fill="#ffffff"
            d="M21.081,6C23.752,6.031,26,8.766,26,12c0,5.106-6.47,10.969-10.001,13.593C12.466,22.974,6,17.12,6,12c0-3.234,2.248-5.969,4.918-6C13.586,6.175,13.926,6.801,16,8.879C18.069,6.806,18.418,6.173,21.081,6 M20.911,4.006L20.912,4C18.993,4,17.259,4.785,16,6.048C14.741,4.785,13.007,4,11.088,4l0.001,0.006C7.044,3.936,4,7.719,4,12c0,8,11.938,16,11.938,16h0.124C16.062,28,28,20,28,12C28,7.713,24.951,3.936,20.911,4.006z"
          />
        </g>
      </svg>
    );
}

function Comment() {
    return (
        <svg
    width="28px"
    height="28px"
    viewBox="0 0 32 32"
    version="1.1"
    xmlns="http://www.w3.org/2000/svg"
    xmlnsXlink="http://www.w3.org/1999/xlink"
    fill="#000000"
  >
    <g strokeWidth="0" />
    <g strokeLinecap="round" strokeLinejoin="round" />
    <g>
      <title>comment-2</title>
      <desc>Created with Sketch Beta.</desc>
      <defs />
      <g transform="translate(-152.000000, -255.000000)" fill="#ffffff">
        <path
          d="M168,281 C166.832,281 165.704,280.864 164.62,280.633 L159.912,283.463 L159.975,278.824 C156.366,276.654 154,273.066 154,269 C154,262.373 160.268,257 168,257 C175.732,257 182,262.373 182,269 C182,275.628 175.732,281 168,281 L168,281 Z M168,255 C159.164,255 152,261.269 152,269 C152,273.419 154.345,277.354 158,279.919 L158,287 L165.009,282.747 C165.979,282.907 166.977,283 168,283 C176.836,283 184,276.732 184,269 C184,261.269 176.836,255 168,255 L168,255 Z M175,266 L161,266 C160.448,266 160,266.448 160,267 C160,267.553 160.448,268 161,268 L175,268 C175.552,268 176,267.553 176,267 C176,266.448 175.552,266 175,266 L175,266 Z M173,272 L163,272 C162.448,272 162,272.447 162,273 C162,273.553 162.448,274 163,274 L173,274 C173.552,274 174,273.553 174,273 C174,272.447 173.552,272 173,272 L173,272 Z"
        />
      </g>
    </g>
  </svg>
    );
  }

function Share({color = "white", size = 30}){
  return(
    <svg
      width={size}
      height={size}
      viewBox="-0.5 0 25 25"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        d="M13.47 4.13998C12.74 4.35998 12.28 5.96 12.09 7.91C6.77997 7.91 2 13.4802 2 20.0802C4.19 14.0802 8.99995 12.45 12.14 12.45C12.34 14.21 12.79 15.6202 13.47 15.8202C15.57 16.4302 22 12.4401 22 9.98006C22 7.52006 15.57 3.52998 13.47 4.13998Z"
        stroke={color}
        strokeWidth="1.5"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

function More({color = "white", size = 50}){
  return(
    <svg
    width={size}
    height={size}
    viewBox="0 0 24 24"
    fill={color}
    xmlns="http://www.w3.org/2000/svg"
  >
    <path d="M8 12a2 2 0 1 1-4 0 2 2 0 0 1 4 0Zm10-2a2 2 0 1 0 0 4 2 2 0 0 0 0-4Zm-6 0a2 2 0 1 0 0 4 2 2 0 0 0 0-4Z"/>
  </svg>
  );
}

export function CreationPost(){
  return(
    <div className='NewPost'>Create a new post!</div>
  );
}

export function Post(){
    return(
        <div className='Post'>
            <div className='user'>
                <div className='ProfileImage'></div>
                <div className='.userInfo'>
                    <div className='Nickname'>Andrii Dosyn</div>
                    <div className='Status'>
                        <FriendFeed />
                        Friend
                    </div>
                </div>
            </div>
            <div className='PostText'>
                Konečne som dokončil prvú časť svojho románu. Dlho som hľadal
                správny hlas pre túto príbeh — tmavý, melancholický, ale s nádejou na
                konci. Dúfam, že vás prvá kapitola zaujme rovnako ako mňa pri písaní
            </div>
            <div className='postImage'>
                <img src={BookImage} alt="book" width={560} height={300}/>
            </div>
            <div className='PostLine'></div>
            <div className='PostAcvtivity'>
                <div className='LikeActivity'><Like />
                    <div className='LikesAmount'>2.4k</div>
                </div>
                <div className='CommentActivity'><Comment />
                    <div className='CommentsAmount'>1.5k</div>
                </div>
                <div className='ShareActivity'><Share />
                    <div className='ShareAmount'>832</div>
                </div>
                <div className='MoreActivity'><More /></div>
            </div>

        </div>
    );
}