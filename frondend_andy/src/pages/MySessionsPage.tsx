import React, { useState } from 'react';
import { PageContainer } from '../components/layout/PageContainer';
import { Tabs } from '../components/ui/Tabs';
import { SessionCard } from '../components/features/SessionCard';
import { Page, Session } from '../types';
interface MySessionsPageProps {
  onNavigate: (page: Page) => void;
}
// Mock Data
const SESSIONS: Session[] = [
{
  id: 's1',
  seekerId: 'u1',
  seekerName: 'Alex Rivera',
  tutorId: 't1',
  tutorName: 'Sarah Chen',
  subjectId: 'sub1',
  subjectName: 'Web Development',
  status: 'matched',
  date: new Date(Date.now() + 86400000).toISOString(),
  mode: 'online'
},
{
  id: 's2',
  seekerId: 'u1',
  seekerName: 'Alex Rivera',
  tutorId: 't2',
  tutorName: 'Marcus Johnson',
  subjectId: 'sub2',
  subjectName: 'Mathematics',
  status: 'completed',
  date: new Date(Date.now() - 172800000).toISOString(),
  mode: 'onsite'
},
{
  id: 's3',
  seekerId: 'u3',
  seekerName: 'Jenny Wilson',
  tutorId: 'u1',
  tutorName: 'Alex Rivera',
  subjectId: 'sub3',
  subjectName: 'Intro to Programming',
  status: 'open',
  date: new Date(Date.now() + 259200000).toISOString(),
  mode: 'online'
}];

export function MySessionsPage({ onNavigate }: MySessionsPageProps) {
  const [activeTab, setActiveTab] = useState('learning');
  const learningSessions = SESSIONS.filter((s) => s.seekerId === 'u1'); // Mock current user ID
  const teachingSessions = SESSIONS.filter((s) => s.tutorId === 'u1');
  return (
    <PageContainer
      currentPage="my-sessions"
      onNavigate={onNavigate}
      title="My Sessions"
      description="Manage your upcoming and past tutoring sessions.">

      <div className="space-y-6">
        <Tabs
          tabs={[
          {
            id: 'learning',
            label: 'Learning (As Student)'
          },
          {
            id: 'teaching',
            label: 'Teaching (As Tutor)'
          }]
          }
          activeTab={activeTab}
          onChange={setActiveTab} />


        <div className="space-y-4">
          {activeTab === 'learning' ?
          learningSessions.length > 0 ?
          learningSessions.map((session) =>
          <SessionCard
            key={session.id}
            session={session}
            role="seeker"
            onAction={(action) => console.log(action)} />

          ) :

          <div className="text-center py-12 bg-white rounded-xl border border-slate-200 border-dashed">
                <p className="text-slate-500">
                  You haven't requested any tutoring sessions yet.
                </p>
              </div> :

          teachingSessions.length > 0 ?
          teachingSessions.map((session) =>
          <SessionCard
            key={session.id}
            session={session}
            role="tutor"
            onAction={(action) => console.log(action)} />

          ) :

          <div className="text-center py-12 bg-white rounded-xl border border-slate-200 border-dashed">
              <p className="text-slate-500">
                You don't have any upcoming teaching sessions.
              </p>
            </div>
          }
        </div>
      </div>
    </PageContainer>);

}