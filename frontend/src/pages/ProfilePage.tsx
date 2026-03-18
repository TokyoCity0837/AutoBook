import '../assets/styles/pages.css'
import { IconUser } from '../pages/SettingsPage';
import '../assets/styles/profilePage.css'
import { Post } from '../components/Posts'
import React from 'react';
import { Link } from 'react-router-dom';
import BookImage1 from '../assets/pictures/book1.jpg'

function BookForProfile() {
    return (
        <Link to="/book" className="bookCardProfile">
            <div className="bookImgProfile">
                <img src={BookImage1} alt="book" className='ImgBookProfile' width={150} height={190}/>
            </div>
            <div className="bookDescProfile">
                <div className="bookTitleProfile">Wind in the willows</div>
                <div className="bookAuthorsProfile">Anton Hrimov Andrii Dosyn</div>
            </div>    
        </Link>
    );
}

export default function Profile() {
    return (
        <div className="profileWrap">
            <div className="mainProfileInfo">
                <div className="topBar">
                    <div className="profilePicAndName">
                        <div className="profileImage">
                        
                        </div>
                        <div className="nameBox">
                            <div className="name">
                                Andrii Dosyn
                            </div>
                            <div className="nickname">
                                d.osid.osid.osid.osi
                            </div>
                        </div>
                    </div>
                    <div className="folowersAndFriends">
                        <div className="ffBox">
                            <div className="ffCount">2.4k</div>
                            <div className="ffLabel">
                                <IconUser size={18} />
                                Followers
                            </div>
                        </div>
                        <div className="ffBox">
                            <div className="ffCount">183</div>
                            <div className="ffLabel">
                                <IconUser size={18} />
                                Following
                            </div>
                        </div>
                        <div className="ffBox">
                            <div className="ffCount">27</div>
                            <div className="ffLabel">
                                <IconUser size={18} />
                                Friends
                            </div>
                        </div>
                    </div>
                </div>
                <div className="aboutMe">
                Lorem ipsum dolor sit amet consectetur adipiscing elit. Quisque faucibus ex sapien vitae pellentesque sem placerat. In id cursus mi pretium tellus duis convallis. Tempus leo eu aenean sed diam urna tempor. Pulvinar vivamus fringilla lacus nec metus bibendum egestas. Iaculis massa nisl malesuada lacinia integer nunc posuere. Ut hendrerit semper vel class aptent taciti sociosqu. Ad litora torquent per conubia nostra inceptos himenaeos.
                </div>
            </div>
            <div className="mainPoststAndInfo">
                <div className="profileAndBooks">

                    {/* <div className="profileInformation">
                        Information
                        <div className="infoBox">
                            <div className="infoText">Age</div>
                            <div className="infoValue">18</div>
                        </div>
                        <div className="infoBox">
                            <div className="infoText">Country</div>
                            <div className="infoValue">Ukraine</div>
                        </div>
                        <div className="infoBox">
                            <div className="infoText">Education</div>
                            <div className="infoValue">STU FIIT</div>
                        </div>
                        <div className="infoBox">
                            <div className="infoText">Publication</div>
                            <div className="infoValue">BBC</div>
                        </div>
                        <div className="infoBox">
                            <div className="infoText">Hobby</div>
                            <div className="infoValue">Cooking</div>
                        </div>
                        <div className="infoBox">
                            <div className="infoText">Genres</div>
                            <div className="infoValue">Thriller, Mystery </div>
                        </div>
                    </div> */}

                    <div className="authorBooks">
                        <BookForProfile />
                        <BookForProfile />
                        <BookForProfile />
                        <BookForProfile />
                    </div>
                    
                </div>
                <div className="postsContainer">
                    <Post />
                    <Post />
                    <Post />
                    <Post />
                </div>
            </div>
        </div>
    );
  }