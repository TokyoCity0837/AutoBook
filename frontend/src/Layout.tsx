import type { ReactNode } from 'react';
import Sidebar from './components/Sidebar';
import './index.css';

type LayoutProps = {
  children: ReactNode;
};

export default function Layout({ children }: LayoutProps) {
  return (
    <div className="layout">
        <Sidebar />
      <div className="content">
        {children}
      </div>
    </div>
  );
}