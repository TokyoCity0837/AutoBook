import {FriendFeed} from '../components/Sidebar.tsx'
import '../assets/styles/EditsPage.css'

type Status = 'Approved' | 'Rejected' | 'Processing';

interface EditRequestProps {
  status: Status;
}

function EditRequest({ status }: EditRequestProps) {

  const statusJSX = {
    Approved: <Accept />,
    Rejected: <Reject />,
    Processing: <Processing />,
  };


  return (
  <div className='editRequest'>
        <div className='user'>
            <div className='ProfileImage'></div>
            <div className='userInfo'>
                <div className='Nickname'>Andrii Dosyn
                  <div className='friendship'><FriendFeed /></div>
                </div>
                <div className='requestingBook'>The Night We Met</div>
            </div>
          </div>
         <div className='requestStatus'>
            {statusJSX[status]}
        </div> 
  </div>
  ); 
}

export function Accept({size = 26, className = "status-icon"}){
  return(
    <svg
      width={size}
      height={size}
      className={className}
      viewBox="0 0 26 26"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        fillRule="evenodd"
        d="M1008,120a12,12,0,1,1,12-12A12,12,0,0,1,1008,120Zm0-22a10,10,0,1,0,10,10A10,10,0,0,0,1008,98Zm-0.08,14.333a0.819,0.819,0,0,1-.22.391,0.892,0.892,0,0,1-.72.259,0.913,0.913,0,0,1-.94-0.655l-2.82-2.818a0.9,0.9,0,0,1,1.27-1.271l2.18,2.184,4.46-7.907a1,1,0,0,1,1.38-.385,1.051,1.051,0,0,1,.36,1.417Z"
        transform="translate(-996 -96)"
        fill="#00FF00"
      />
    </svg>
  );
}

export function Reject({className = "status-icon"}){
  return(
    <svg
      className={className}
      viewBox="0 0 24 24"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        d="M8.00386 9.41816C7.61333 9.02763 7.61334 8.39447 8.00386 8.00395C8.39438 7.61342 9.02755 7.61342 9.41807 8.00395L12.0057 10.5916L14.5907 8.00657C14.9813 7.61605 15.6144 7.61605 16.0049 8.00657C16.3955 8.3971 16.3955 9.03026 16.0049 9.42079L13.4199 12.0058L16.0039 14.5897C16.3944 14.9803 16.3944 15.6134 16.0039 16.0039C15.6133 16.3945 14.9802 16.3945 14.5896 16.0039L12.0057 13.42L9.42097 16.0048C9.03045 16.3953 8.39728 16.3953 8.00676 16.0048C7.61624 15.6142 7.61624 14.9811 8.00676 14.5905L10.5915 12.0058L8.00386 9.41816Z"
        fill="#ff2600"
      />
      <path
        fillRule="evenodd"
        clipRule="evenodd"
        d="M23 12C23 18.0751 18.0751 23 12 23C5.92487 23 1 18.0751 1 12C1 5.92487 5.92487 1 12 1C18.0751 1 23 5.92487 23 12ZM3.00683 12C3.00683 16.9668 7.03321 20.9932 12 20.9932C16.9668 20.9932 20.9932 16.9668 20.9932 12C20.9932 7.03321 16.9668 3.00683 12 3.00683C7.03321 3.00683 3.00683 7.03321 3.00683 12Z"
        fill="#ff2600"
      />
    </svg>
  );
}

function Processing(){
  return(
    <svg
      className="status-icon"
      viewBox="0 0 22 22"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        d="M12 3C7.04 3 3 7.04 3 12C3 16.96 7.04 21 12 21C16.96 21 21 16.96 21 12C21 7.04 16.96 3 12 3ZM12 19.5C7.86 19.5 4.5 16.14 4.5 12C4.5 7.86 7.86 4.5 12 4.5C16.14 4.5 19.5 7.86 19.5 12C19.5 16.14 16.14 19.5 12 19.5ZM14.3 7.7C14.91 8.31 15.25 9.13 15.25 10C15.25 10.87 14.91 11.68 14.3 12.3C13.87 12.73 13.33 13.03 12.75 13.16V13.5C12.75 13.91 12.41 14.25 12 14.25C11.59 14.25 11.25 13.91 11.25 13.5V12.5C11.25 12.09 11.59 11.75 12 11.75C12.47 11.75 12.91 11.57 13.24 11.24C13.57 10.91 13.75 10.47 13.75 10C13.75 9.53 13.57 9.09 13.24 8.76C12.58 8.1 11.43 8.1 10.77 8.76C10.44 9.09 10.26 9.53 10.26 10C10.26 10.41 9.92 10.75 9.51 10.75C9.1 10.75 8.76 10.41 8.76 10C8.76 9.13 9.1 8.32 9.71 7.7C10.94 6.47 13.08 6.47 14.31 7.7H14.3ZM13 16.25C13 16.8 12.55 17.25 12 17.25C11.45 17.25 11 16.8 11 16.25C11 15.7 11.45 15.25 12 15.25C12.55 15.25 13 15.7 13 16.25Z"
        fill="#fffc41"
      />
    </svg>
  );
}

export default function Edits() {
    return (
      <div>
            <div className='editsWrapper'>
              <div className='sentEditsWrapper'>
                <div className="sentText">Sent requests</div>
                <div className='requestsBody'>
                  <EditRequest status = 'Approved' />
                  <EditRequest status = 'Rejected' />
                  <EditRequest status = 'Processing' />
                </div>
              </div>
              <div className='incomingEditsWrapper'>
                <div className="incomingText">Incoming requests</div>
                <div className='requestsBody'>
                  <EditRequest status = 'Approved' />
                  <EditRequest status = 'Rejected' />
                  <EditRequest status = 'Processing' />
                  <EditRequest status = 'Processing' />
                  <EditRequest status = 'Rejected' />
                  <EditRequest status = 'Rejected' />
                  <EditRequest status = 'Approved' />
                  <EditRequest status = 'Approved' />
                  <EditRequest status = 'Rejected' />
                  <EditRequest status = 'Processing' />
                  <EditRequest status = 'Processing' />
                  <EditRequest status = 'Rejected' />
                  <EditRequest status = 'Rejected' />
                  <EditRequest status = 'Approved' />
                </div>
              </div>
            </div>
      </div>
    );
  }