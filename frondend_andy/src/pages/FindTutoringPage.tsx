import React, { useState } from 'react';
import { Search, Filter } from 'lucide-react';
import { PageContainer } from '../components/layout/PageContainer';
import { Input } from '../components/ui/Input';
import { Select } from '../components/ui/Select';
import { TutorCard } from '../components/features/TutorCard';
import { Page, TutoringOffer } from '../types';
interface FindTutoringPageProps {
  onNavigate: (page: Page) => void;
}
// Mock Data
const OFFERS: TutoringOffer[] = [
{
  id: 'o1',
  tutorId: 't1',
  tutorName: 'Sarah Chen',
  tutorAvatar:
  'https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-1.2.1&auto=format&fit=crop&w=256&q=80',
  subjectId: 's1',
  subjectName: 'Web Development',
  mode: 'online',
  description:
  'Specializing in React, TypeScript, and modern CSS. I can help you debug your code or understand core concepts.',
  availability: 'Mon, Wed, Fri • After 4PM'
},
{
  id: 'o2',
  tutorId: 't2',
  tutorName: 'Marcus Johnson',
  tutorAvatar:
  'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?ixlib=rb-1.2.1&auto=format&fit=crop&w=256&q=80',
  subjectId: 's3',
  subjectName: 'Mathematics',
  mode: 'both',
  description:
  'Patient math tutor for Algebra and Calculus. I use visual methods to explain complex problems.',
  availability: 'Tue, Thu • Lunch break'
},
{
  id: 'o3',
  tutorId: 't3',
  tutorName: 'Emily Davis',
  subjectId: 's4',
  subjectName: 'Business English',
  mode: 'onsite',
  description:
  'Help with presentations, essay writing, and professional communication skills.',
  availability: 'Weekdays • Library'
},
{
  id: 'o4',
  tutorId: 't4',
  tutorName: 'David Kim',
  tutorAvatar:
  'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?ixlib=rb-1.2.1&auto=format&fit=crop&w=256&q=80',
  subjectId: 's2',
  subjectName: 'Database Systems',
  mode: 'online',
  description:
  'SQL expert. Can help with database design, normalization, and query optimization.',
  availability: 'Weekends • Flexible'
},
{
  id: 'o5',
  tutorId: 't5',
  tutorName: 'Jessica Wong',
  subjectId: 's1',
  subjectName: 'Web Development',
  mode: 'both',
  description: 'Frontend basics: HTML, CSS, JavaScript. Great for beginners!',
  availability: 'Mon-Wed • Evenings'
}];

export function FindTutoringPage({ onNavigate }: FindTutoringPageProps) {
  const [searchTerm, setSearchTerm] = useState('');
  const [subjectFilter, setSubjectFilter] = useState('all');
  const [modeFilter, setModeFilter] = useState('all');
  // Filter logic
  const filteredOffers = OFFERS.filter((offer) => {
    const matchesSearch =
    offer.tutorName.toLowerCase().includes(searchTerm.toLowerCase()) ||
    offer.subjectName.toLowerCase().includes(searchTerm.toLowerCase()) ||
    offer.description.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesSubject =
    subjectFilter === 'all' || offer.subjectName === subjectFilter;
    const matchesMode =
    modeFilter === 'all' ||
    modeFilter === 'online' && (
    offer.mode === 'online' || offer.mode === 'both') ||
    modeFilter === 'onsite' && (
    offer.mode === 'onsite' || offer.mode === 'both');
    return matchesSearch && matchesSubject && matchesMode;
  });
  const uniqueSubjects = Array.from(new Set(OFFERS.map((o) => o.subjectName)));
  return (
    <PageContainer
      currentPage="find-tutoring"
      onNavigate={onNavigate}
      title="Find a Tutor"
      description="Browse available peer tutors in your vocational group.">

      <div className="space-y-6">
        {/* Filters */}
        <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
          <div className="grid grid-cols-1 md:grid-cols-12 gap-4">
            <div className="md:col-span-6">
              <Input
                placeholder="Search by subject, tutor name..."
                leftIcon={<Search className="w-4 h-4" />}
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)} />

            </div>
            <div className="md:col-span-3">
              <Select
                options={[
                {
                  value: 'all',
                  label: 'All Subjects'
                },
                ...uniqueSubjects.map((s) => ({
                  value: s,
                  label: s
                }))]
                }
                value={subjectFilter}
                onChange={(e) => setSubjectFilter(e.target.value)} />

            </div>
            <div className="md:col-span-3">
              <Select
                options={[
                {
                  value: 'all',
                  label: 'Any Mode'
                },
                {
                  value: 'online',
                  label: 'Online'
                },
                {
                  value: 'onsite',
                  label: 'On-site'
                }]
                }
                value={modeFilter}
                onChange={(e) => setModeFilter(e.target.value)} />

            </div>
          </div>
        </div>

        {/* Results */}
        {filteredOffers.length > 0 ?
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredOffers.map((offer) =>
          <TutorCard
            key={offer.id}
            offer={offer}
            onRequest={() => alert(`Request sent to ${offer.tutorName}!`)} />

          )}
          </div> :

        <div className="text-center py-16 bg-white rounded-xl border border-slate-200 border-dashed">
            <div className="mx-auto w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mb-4">
              <Filter className="w-8 h-8 text-slate-400" />
            </div>
            <h3 className="text-lg font-medium text-slate-900">
              No tutors found
            </h3>
            <p className="text-slate-500 max-w-sm mx-auto mt-2">
              Try adjusting your filters or search terms. New tutors join EULO
              every day!
            </p>
          </div>
        }
      </div>
    </PageContainer>);

}