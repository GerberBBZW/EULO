import React, { useState, useEffect, useCallback } from 'react';
import { Search, Filter } from 'lucide-react';
import { PageContainer } from '../components/layout/PageContainer';
import { Input } from '../components/ui/Input';
import { Select } from '../components/ui/Select';
import { TutorCard } from '../components/features/TutorCard';
import { useAuth } from '../hooks/useAuth';
import { Page, TutoringOffer } from '../types';
import { getOffers, getSubjects, createSession } from '../api';

interface FindTutoringPageProps {
  onNavigate: (page: Page) => void;
}

export function FindTutoringPage({ onNavigate }: FindTutoringPageProps) {
  const { currentUser } = useAuth();
  const [offers, setOffers] = useState<TutoringOffer[]>([]);
  const [subjects, setSubjects] = useState<{ value: string; label: string }[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [subjectFilter, setSubjectFilter] = useState('all');
  const [modeFilter, setModeFilter] = useState('all');
  const [loading, setLoading] = useState(true);
  const [requesting, setRequesting] = useState<string | null>(null);

  useEffect(() => {
    getSubjects().then(subs => {
      setSubjects([
        { value: 'all', label: 'All Subjects' },
        ...subs.map(s => ({ value: s.name, label: s.name })),
      ]);
    });
  }, []);

  const fetchOffers = useCallback(() => {
    setLoading(true);
    getOffers({ search: searchTerm, subject: subjectFilter, mode: modeFilter })
      .then(setOffers)
      .finally(() => setLoading(false));
  }, [searchTerm, subjectFilter, modeFilter]);

  useEffect(() => {
    const t = setTimeout(fetchOffers, 300);
    return () => clearTimeout(t);
  }, [fetchOffers]);

  const handleRequest = async (offer: TutoringOffer) => {
    if (!currentUser) return;
    if (offer.tutorId === currentUser.id) {
      alert("You can't request tutoring from yourself.");
      return;
    }
    setRequesting(offer.id);
    try {
      await createSession({
        seekerId: currentUser.id,
        seekerName: currentUser.name,
        tutorId: offer.tutorId,
        tutorName: offer.tutorName,
        subjectId: offer.subjectId,
        subjectName: offer.subjectName,
        status: 'open',
        date: new Date(Date.now() + 7 * 86400000).toISOString(),
        mode: offer.mode,
      });
      alert(`Tutoring request sent to ${offer.tutorName}!`);
    } catch (e) {
      alert('Failed to send request. Please try again.');
    } finally {
      setRequesting(null);
    }
  };

  return (
    <PageContainer
      currentPage="find-tutoring"
      onNavigate={onNavigate}
      title="Find a Tutor"
      description="Browse available peer tutors in your vocational group."
    >
      <div className="space-y-6">
        {/* Filters */}
        <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
          <div className="grid grid-cols-1 md:grid-cols-12 gap-4">
            <div className="md:col-span-6">
              <Input
                placeholder="Search by subject, tutor name..."
                leftIcon={<Search className="w-4 h-4" />}
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            <div className="md:col-span-3">
              <Select
                options={subjects}
                value={subjectFilter}
                onChange={(e) => setSubjectFilter(e.target.value)}
              />
            </div>
            <div className="md:col-span-3">
              <Select
                options={[
                  { value: 'all', label: 'Any Mode' },
                  { value: 'online', label: 'Online' },
                  { value: 'onsite', label: 'On-site' },
                ]}
                value={modeFilter}
                onChange={(e) => setModeFilter(e.target.value)}
              />
            </div>
          </div>
        </div>

        {/* Results */}
        {loading ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {[1, 2, 3].map(i => (
              <div key={i} className="h-48 bg-slate-100 rounded-xl animate-pulse" />
            ))}
          </div>
        ) : offers.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {offers.map((offer) => (
              <TutorCard
                key={offer.id}
                offer={offer}
                onRequest={() => handleRequest(offer)}
                isRequesting={requesting === offer.id}
              />
            ))}
          </div>
        ) : (
          <div className="text-center py-16 bg-white rounded-xl border border-slate-200 border-dashed">
            <div className="mx-auto w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mb-4">
              <Filter className="w-8 h-8 text-slate-400" />
            </div>
            <h3 className="text-lg font-medium text-slate-900">No tutors found</h3>
            <p className="text-slate-500 max-w-sm mx-auto mt-2">
              Try adjusting your filters or search terms. New tutors join EULO every day!
            </p>
          </div>
        )}
      </div>
    </PageContainer>
  );
}
