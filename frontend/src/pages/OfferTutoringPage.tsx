import React, { useState, useEffect } from 'react';
import { Plus, Check, Trash2 } from 'lucide-react';
import { PageContainer } from '../components/layout/PageContainer';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Select } from '../components/ui/Select';
import { Badge } from '../components/ui/Badge';
import { useAuth } from '../hooks/useAuth';
import { Page, TutoringOffer, Subject } from '../types';
import { getSubjects, getOffersByTutor, createOffer, deleteOffer } from '../api';

interface OfferTutoringPageProps {
  onNavigate: (page: Page) => void;
}

export function OfferTutoringPage({ onNavigate }: OfferTutoringPageProps) {
  const { currentUser } = useAuth();
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [myOffers, setMyOffers] = useState<TutoringOffer[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showSuccess, setShowSuccess] = useState(false);
  const [deletingId, setDeletingId] = useState<string | null>(null);

  const [subjectId, setSubjectId] = useState('');
  const [subjectName, setSubjectName] = useState('');
  const [mode, setMode] = useState<'online' | 'onsite' | 'both'>('online');
  const [availability, setAvailability] = useState('');
  const [description, setDescription] = useState('');

  useEffect(() => {
    getSubjects().then(setSubjects);
  }, []);

  useEffect(() => {
    if (currentUser) {
      getOffersByTutor(currentUser.id).then(setMyOffers);
    }
  }, [currentUser]);

  const handleSubjectChange = (val: string) => {
    const sub = subjects.find(s => s.id === val);
    setSubjectId(val);
    setSubjectName(sub?.name ?? '');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUser || !subjectId) return;
    setIsSubmitting(true);
    try {
      const offer = await createOffer({
        tutorId: currentUser.id,
        tutorName: currentUser.name,
        tutorAvatar: currentUser.avatarUrl,
        subjectId,
        subjectName,
        mode,
        availability,
        description,
      });
      setMyOffers(prev => [...prev, offer]);
      setShowSuccess(true);
      setSubjectId('');
      setSubjectName('');
      setAvailability('');
      setDescription('');
      setTimeout(() => setShowSuccess(false), 3000);
    } catch {
      alert('Failed to publish offer. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (id: string) => {
    setDeletingId(id);
    try {
      await deleteOffer(id);
      setMyOffers(prev => prev.filter(o => o.id !== id));
    } catch {
      alert('Failed to remove offer.');
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <PageContainer
      currentPage="offer-tutoring"
      onNavigate={onNavigate}
      title="Offer Tutoring"
      description="Share your knowledge and help your peers succeed."
    >
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Form Section */}
        <div className="lg:col-span-2">
          <Card>
            <CardHeader>
              <CardTitle>Create New Offer</CardTitle>
            </CardHeader>
            <CardContent>
              {showSuccess ? (
                <div className="bg-green-50 border border-green-200 rounded-lg p-6 text-center">
                  <div className="mx-auto w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mb-4">
                    <Check className="w-6 h-6 text-green-600" />
                  </div>
                  <h3 className="text-lg font-medium text-green-900">Offer Published!</h3>
                  <p className="text-green-700 mt-2">Your tutoring offer is now visible to other students.</p>
                  <Button variant="outline" className="mt-4 bg-white" onClick={() => setShowSuccess(false)}>
                    Create Another Offer
                  </Button>
                </div>
              ) : (
                <form onSubmit={handleSubmit} className="space-y-6">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <Select
                      label="Subject"
                      options={[
                        { value: '', label: 'Select a subject...' },
                        ...subjects.map(s => ({ value: s.id, label: s.name })),
                      ]}
                      value={subjectId}
                      onChange={(e) => handleSubjectChange(e.target.value)}
                      required
                    />
                    <Select
                      label="Mode"
                      options={[
                        { value: 'online', label: 'Online Only' },
                        { value: 'onsite', label: 'On-site Only' },
                        { value: 'both', label: 'Both Online & On-site' },
                      ]}
                      value={mode}
                      onChange={(e) => setMode(e.target.value as 'online' | 'onsite' | 'both')}
                      required
                    />
                  </div>

                  <Input
                    label="Availability"
                    placeholder="e.g., Mondays & Wednesdays after 4 PM"
                    helperText="Be specific about when you can help."
                    value={availability}
                    onChange={(e) => setAvailability(e.target.value)}
                    required
                  />

                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Description</label>
                    <textarea
                      className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-teal-500 min-h-[120px]"
                      placeholder="Describe what you can help with. Mention specific topics, your teaching style, or prerequisites."
                      value={description}
                      onChange={(e) => setDescription(e.target.value)}
                      required
                    />
                  </div>

                  <div className="flex justify-end">
                    <Button type="submit" isLoading={isSubmitting} leftIcon={<Plus className="w-4 h-4" />}>
                      Publish Offer
                    </Button>
                  </div>
                </form>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Sidebar Info */}
        <div className="space-y-6">
          <Card className="bg-teal-50 border-teal-100">
            <CardContent className="p-6">
              <h3 className="font-bold text-teal-900 mb-2">Why Tutor?</h3>
              <ul className="space-y-3 text-sm text-teal-800">
                <li className="flex items-start"><span className="mr-2">•</span>Reinforce your own learning by teaching others</li>
                <li className="flex items-start"><span className="mr-2">•</span>Earn community service hours for your school record</li>
                <li className="flex items-start"><span className="mr-2">•</span>Build your resume and communication skills</li>
                <li className="flex items-start"><span className="mr-2">•</span>Help build a supportive school community</li>
              </ul>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="text-base">Your Active Offers</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              {myOffers.length === 0 ? (
                <p className="text-sm text-slate-500 text-center py-4">No active offers yet.</p>
              ) : (
                myOffers.map(offer => (
                  <div key={offer.id} className="p-3 border border-slate-100 rounded-lg bg-slate-50">
                    <div className="flex justify-between items-start mb-2">
                      <span className="font-medium text-slate-900">{offer.subjectName}</span>
                      <Badge variant="success">Active</Badge>
                    </div>
                    <p className="text-xs text-slate-500 mb-2 capitalize">{offer.mode} • {offer.availability}</p>
                    <button
                      onClick={() => handleDelete(offer.id)}
                      disabled={deletingId === offer.id}
                      className="text-xs text-red-600 font-medium hover:underline disabled:opacity-50 flex items-center gap-1"
                    >
                      <Trash2 className="w-3 h-3" />
                      {deletingId === offer.id ? 'Removing...' : 'Remove'}
                    </button>
                  </div>
                ))
              )}
            </CardContent>
          </Card>
        </div>
      </div>
    </PageContainer>
  );
}
