import React, { useState } from 'react';
import { Users, BookOpen, Calendar, ArrowRight } from 'lucide-react';
import { PageContainer } from '../components/layout/PageContainer';
import { Card, CardContent } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { VocationalGroupSelector } from '../components/features/VocationalGroupSelector';
import { SubjectCard } from '../components/features/SubjectCard';
import { SessionCard } from '../components/features/SessionCard';
import { useAuth } from '../hooks/useAuth';
import { Page, Subject, Session } from '../types';
interface DashboardPageProps {
  onNavigate: (page: Page) => void;
}
// Mock Data
const SUBJECTS: Subject[] = [
{
  id: 's1',
  name: 'Web Development',
  category: 'vocational',
  tutorCount: 5
},
{
  id: 's2',
  name: 'Database Systems',
  category: 'vocational',
  tutorCount: 3
},
{
  id: 's3',
  name: 'Mathematics',
  category: 'general',
  tutorCount: 8
},
{
  id: 's4',
  name: 'Business English',
  category: 'general',
  tutorCount: 4
}];

const UPCOMING_SESSION: Session = {
  id: 'sess1',
  seekerId: 'u1',
  seekerName: 'Alex Rivera',
  tutorId: 'u2',
  tutorName: 'Sarah Chen',
  subjectId: 's1',
  subjectName: 'Web Development',
  status: 'matched',
  date: new Date(Date.now() + 86400000).toISOString(),
  mode: 'online'
};
export function DashboardPage({ onNavigate }: DashboardPageProps) {
  const { currentUser } = useAuth();
  const [selectedGroup, setSelectedGroup] = useState(
    currentUser?.vocationalGroup || 'Information Technology'
  );
  return (
    <PageContainer
      currentPage="dashboard"
      onNavigate={onNavigate}
      title={`Welcome back, ${currentUser?.name.split(' ')[0]}!`}
      description="Here's what's happening in your learning community.">

      <div className="space-y-8">
        {/* Stats Row */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Card className="bg-gradient-to-br from-teal-500 to-teal-600 text-white border-none">
            <CardContent className="p-6">
              <div className="flex items-center justify-between mb-4">
                <div className="p-2 bg-white/20 rounded-lg">
                  <Calendar className="w-6 h-6 text-white" />
                </div>
                <span className="text-xs font-medium bg-white/20 px-2 py-1 rounded-full">
                  This Week
                </span>
              </div>
              <p className="text-3xl font-bold">3</p>
              <p className="text-teal-100 text-sm">Upcoming Sessions</p>
            </CardContent>
          </Card>

          <Card className="bg-white">
            <CardContent className="p-6">
              <div className="flex items-center justify-between mb-4">
                <div className="p-2 bg-indigo-100 rounded-lg">
                  <Users className="w-6 h-6 text-indigo-600" />
                </div>
                <span className="text-xs font-medium text-slate-500">
                  Active
                </span>
              </div>
              <p className="text-3xl font-bold text-slate-900">124</p>
              <p className="text-slate-500 text-sm">Available Tutors</p>
            </CardContent>
          </Card>

          <Card className="bg-white">
            <CardContent className="p-6">
              <div className="flex items-center justify-between mb-4">
                <div className="p-2 bg-amber-100 rounded-lg">
                  <BookOpen className="w-6 h-6 text-amber-600" />
                </div>
                <span className="text-xs font-medium text-slate-500">
                  Total
                </span>
              </div>
              <p className="text-3xl font-bold text-slate-900">42</p>
              <p className="text-slate-500 text-sm">Subjects Offered</p>
            </CardContent>
          </Card>
        </div>

        {/* Vocational Group & Subjects */}
        <section>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-bold text-slate-900">
              Explore Subjects
            </h2>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => onNavigate('find-tutoring')}>

              View All <ArrowRight className="w-4 h-4 ml-1" />
            </Button>
          </div>

          <div className="mb-6">
            <VocationalGroupSelector
              selectedGroup={selectedGroup}
              onChange={setSelectedGroup} />

          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            {SUBJECTS.map((subject) =>
            <SubjectCard
              key={subject.id}
              subject={subject}
              onClick={() => onNavigate('find-tutoring')} />

            )}
          </div>
        </section>

        {/* Upcoming Session */}
        <section>
          <h2 className="text-lg font-bold text-slate-900 mb-4">
            Next Session
          </h2>
          <SessionCard session={UPCOMING_SESSION} role="seeker" />
        </section>

        {/* CTA Banner */}
        <div className="bg-slate-900 rounded-2xl p-8 text-center sm:text-left sm:flex sm:items-center sm:justify-between relative overflow-hidden">
          <div className="relative z-10">
            <h2 className="text-2xl font-bold text-white mb-2">
              Want to help others learn?
            </h2>
            <p className="text-slate-300 max-w-xl mb-6 sm:mb-0">
              Share your knowledge and earn community hours. Become a tutor
              today!
            </p>
          </div>
          <div className="relative z-10">
            <Button
              size="lg"
              className="bg-white text-slate-900 hover:bg-slate-100 border-none"
              onClick={() => onNavigate('offer-tutoring')}>

              Become a Tutor
            </Button>
          </div>

          {/* Decorative elements */}
          <div className="absolute top-0 right-0 -mr-16 -mt-16 w-64 h-64 bg-teal-500 rounded-full opacity-10 blur-3xl"></div>
          <div className="absolute bottom-0 left-0 -ml-16 -mb-16 w-64 h-64 bg-indigo-500 rounded-full opacity-10 blur-3xl"></div>
        </div>
      </div>
    </PageContainer>);

}