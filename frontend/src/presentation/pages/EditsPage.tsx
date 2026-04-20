import { IconFriends } from '../components/ui/Icons'
import '../../assets/styles/EditsPage.css'
import { useState, useEffect } from 'react'
import apiClient from '../../data/api/apiClient'

type Status = 'Approved' | 'Rejected' | 'Processing';

function EditRequest({ request }: { request?: any }) {
  const data = request || { status: 'Processing', user: { visibleName: "Unknown" }, book: { title: "Unknown" } };
  const statusJSX: Record<Status, React.ReactElement> = {
    Approved: <span style={{color:'#00FF00'}}>✓ Approved</span>,
    Rejected: <span style={{color:'#ff2600'}}>✗ Rejected</span>,
    Processing: <span style={{color:'#fffc41'}}>? Processing</span>,
  };
  return (
    <div className='editRequest'>
      <div className='user'>
        <div className='ProfileImage'></div>
        <div className='userInfo'>
          <div className='Nickname'>{data.user.visibleName}<div className='friendship'><IconFriends /></div></div>
          <div className='requestingBook'>{data.book.title}</div>
        </div>
      </div>
      <div className='requestStatus'>{statusJSX[data.status as Status]}</div>
    </div>
  );
}

export default function EditsPage() {
  const [editsData, setEditsData] = useState<any>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    apiClient.get('/edits')
      .then(response => { setEditsData(response.data); setIsLoading(false); })
      .catch(() => {
        setEditsData({
          sentRequests: [
            { id: 1, status: 'Approved', user: { visibleName: "Andrii Dosyn" }, book: { title: "The Night We Met" } },
            { id: 2, status: 'Rejected', user: { visibleName: "John Doe" }, book: { title: "Dune" } },
            { id: 3, status: 'Processing', user: { visibleName: "Alice" }, book: { title: "1984" } }
          ],
          incomingRequests: [
            { id: 4, status: 'Approved', user: { visibleName: "Bob Smith" }, book: { title: "My First Novel" } },
            { id: 5, status: 'Processing', user: { visibleName: "Anton Hrimov" }, book: { title: "Wind in the willows" } },
          ]
        });
        setIsLoading(false);
      });
  }, []);

  if (isLoading || !editsData) return <div>Loading Edits...</div>;

  return (
    <div>
      <div className='editsWrapper'>
        <div className='sentEditsWrapper'>
          <div className="sentText">Sent requests</div>
          <div className='requestsBody'>
            {editsData.sentRequests.map((req: any) => <EditRequest key={req.id} request={req} />)}
          </div>
        </div>
        <div className='incomingEditsWrapper'>
          <div className="incomingText">Incoming requests</div>
          <div className='requestsBody'>
            {editsData.incomingRequests.map((req: any) => <EditRequest key={req.id} request={req} />)}
          </div>
        </div>
      </div>
    </div>
  );
}
