import React, { useState } from 'react';
import { Menu } from 'lucide-react';
import { Sidebar } from './Sidebar';
import { Page } from '../../types';
interface PageContainerProps {
  children: React.ReactNode;
  currentPage: Page;
  onNavigate: (page: Page) => void;
  title?: string;
  description?: string;
  action?: React.ReactNode;
}
export function PageContainer({
  children,
  currentPage,
  onNavigate,
  title,
  description,
  action
}: PageContainerProps) {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  return (
    <div className="flex h-screen bg-slate-50 overflow-hidden">
      <Sidebar
        currentPage={currentPage}
        onNavigate={onNavigate}
        isOpen={isSidebarOpen}
        onClose={() => setIsSidebarOpen(false)} />


      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        {/* Mobile Header */}
        <div className="lg:hidden flex items-center justify-between bg-white border-b border-slate-200 px-4 py-3">
          <div className="flex items-center">
            <button
              onClick={() => setIsSidebarOpen(true)}
              className="p-2 -ml-2 rounded-md text-slate-500 hover:bg-slate-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-teal-500">

              <Menu className="h-6 w-6" />
            </button>
            <span className="ml-3 font-semibold text-slate-900">EULO</span>
          </div>
        </div>

        {/* Main Content */}
        <main className="flex-1 overflow-y-auto focus:outline-none p-4 sm:p-6 lg:p-8">
          <div className="max-w-7xl mx-auto w-full">
            {(title || action) &&
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-8">
                <div>
                  {title &&
                <h1 className="text-2xl font-bold text-slate-900">
                      {title}
                    </h1>
                }
                  {description &&
                <p className="mt-1 text-sm text-slate-500">{description}</p>
                }
                </div>
                {action && <div className="mt-4 sm:mt-0">{action}</div>}
              </div>
            }

            {children}
          </div>
        </main>
      </div>
    </div>);

}