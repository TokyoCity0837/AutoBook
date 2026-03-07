import type { ReactNode } from 'react';
import Sidebar from './components/Sidebar';

type LayoutProps = {
  children: ReactNode;
};

export default function Layout({ children }: LayoutProps) {
  return (
    <div className="layout">
      <div className="sidebar">
        <Sidebar />
      </div>
      <div className="content">
        {children}
      </div>
    </div>
  );
}