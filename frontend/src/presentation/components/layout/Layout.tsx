import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import '../../../assets/styles/index.css';

export function Layout() {
  return (
    <div className="layout">
      <Sidebar />
      <div className="content">
        <Outlet />
      </div>
    </div>
  );
}
