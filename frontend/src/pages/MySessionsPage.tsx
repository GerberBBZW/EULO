import React, { useState, useEffect } from 'react';
import { PageContainer } from '../components/layout/PageContainer';
import { Tabs } from '../components/ui/Tabs';
import { SessionCard } from '../components/features/SessionCard';
import { useAuth } from '../hooks/useAuth';
import { Page, Session } from '../types';
import { getSessions, updateSessionStatus } from '../api';

interface MySessionsPageProps {
  onNavigate: (page: Page) => void;
}

export function MySessionsPage({ onNavigate }: MySessionsPageProps) {
  const { currentUser } = useAuth();
  const [sessions, setSessions] = useState<Session[]>([]);
  const [activeTab, setActiveTab] = useState('learning');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!currentUser) return;
    getSessions(currentUser.id).then(setSessions).finally(() => setLoading(false));
  }, [currentUser]);

  const handleAction = async (action: string, session: Session) => {
    try {
      let newStatus: string | null = null;
      if (action === 'cancel') newStatus = 'cancelled';
      if (action === 'complete') newStatus = 'completed';
      if (action === 'accept') newStatus = 'matched';

      if (newStatus) {
        const updated = await updateSessionStatus(session.id, newStatus);
        setSessions(prev => prev.map(s => s.id === updated.id ? updated : s));
      }
    } catch {
      alert('Failed to update session status.');
    }
  };

  const learningSessions = sessions.filter(s => s.seekerId === currentUser?.id);
  const teachingSessions = sessions.filter(s => s.tutorId === currentUser?.id);
  const currentList = activeTab === 'learning' ? learningSessions : teachingSessions;

  return (
    <PageContainer
      currentPage="my-sessions"
      onNavigate={onNavigate}
      title="My Sessions"
      description="Manage your upcoming and past tutoring sessions."
    >
      <div className="space-y-6">
        <Tabs
          tabs={[
            { id: 'learning', label: 'Learning (As Student)' },
            { id: 'teaching', label: 'Teaching (As Tutor)' },
          ]}
          activeTab={activeTab}
          onChange={setActiveTab}
        />

        <div className="space-y-4">
          {loading ? (
            <div className="space-y-4">
              {[1, 2].map(i => <div key={i} className="h-32 bg-slate-100 rounded-xl animate-pulse" />)}
            </div>
          ) : currentList.length > 0 ? (
            currentList.map(session => (
              <SessionCard
                key={session.id}
                session={session}
                role={activeTab === 'learning' ? 'seeker' : 'tutor'}
                onAction={(action) => handleAction(action, session)}
              />
            ))
          ) : (
            <div className="text-center py-12 bg-white rounded-xl border border-slate-200 border-dashed">
              <p className="text-slate-500">
                {activeTab === 'learning'
                  ? "You haven't requested any tutoring sessions yet."
                  : "You don't have any upcoming teaching sessions."}
              </p>
            </div>
          )}
        </div>
      </div>
    </PageContainer>
  );
}
