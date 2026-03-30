import React from 'react';
import { Mail, Briefcase, Award, BookOpen, Edit2 } from 'lucide-react';
import { PageContainer } from '../components/layout/PageContainer';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/Card';
import { Avatar } from '../components/ui/Avatar';
import { Button } from '../components/ui/Button';
import { Badge } from '../components/ui/Badge';
import { useAuth } from '../hooks/useAuth';
import { Page } from '../types';
interface ProfilePageProps {
  onNavigate: (page: Page) => void;
}
export function ProfilePage({ onNavigate }: ProfilePageProps) {
  const { currentUser } = useAuth();
  if (!currentUser) return null;
  return (
    <PageContainer
      currentPage="profile"
      onNavigate={onNavigate}
      title="My Profile">

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Main Profile Info */}
        <div className="lg:col-span-2 space-y-6">
          <Card>
            <CardContent className="p-8">
              <div className="flex flex-col sm:flex-row items-start">
                <Avatar
                  name={currentUser.name}
                  src={currentUser.avatarUrl}
                  size="xl"
                  className="mb-4 sm:mb-0 sm:mr-6 w-24 h-24 text-2xl" />

                <div className="flex-1">
                  <div className="flex items-center justify-between mb-2">
                    <h2 className="text-2xl font-bold text-slate-900">
                      {currentUser.name}
                    </h2>
                    <Button
                      variant="outline"
                      size="sm"
                      leftIcon={<Edit2 className="w-3 h-3" />}>

                      Edit
                    </Button>
                  </div>
                  <div className="flex items-center text-slate-500 mb-4">
                    <Briefcase className="w-4 h-4 mr-2" />
                    <span>{currentUser.vocationalGroup}</span>
                  </div>
                  <p className="text-slate-600 mb-6">{currentUser.bio}</p>

                  <div className="flex flex-wrap gap-2">
                    {currentUser.subjectsTutored?.map((subject) =>
                    <Badge key={subject} variant="info">
                        {subject}
                      </Badge>
                    )}
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Contact Information</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center p-3 bg-slate-50 rounded-lg">
                <Mail className="w-5 h-5 text-slate-400 mr-3" />
                <div>
                  <p className="text-xs text-slate-500 uppercase font-medium">
                    School Email
                  </p>
                  <p className="text-sm font-medium text-slate-900">
                    {currentUser.email}
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Stats Sidebar */}
        <div className="space-y-6">
          <Card className="bg-gradient-to-br from-slate-900 to-slate-800 text-white border-none">
            <CardContent className="p-6">
              <h3 className="font-bold mb-6 flex items-center">
                <Award className="w-5 h-5 mr-2 text-amber-400" />
                Impact Score
              </h3>

              <div className="grid grid-cols-2 gap-4">
                <div className="text-center p-4 bg-white/10 rounded-xl">
                  <p className="text-3xl font-bold mb-1">
                    {currentUser.sessionsCompleted}
                  </p>
                  <p className="text-xs text-slate-400">Sessions</p>
                </div>
                <div className="text-center p-4 bg-white/10 rounded-xl">
                  <p className="text-3xl font-bold mb-1">4.9</p>
                  <p className="text-xs text-slate-400">Rating</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="text-base">Achievements</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div className="flex items-center">
                  <div className="w-10 h-10 rounded-full bg-amber-100 flex items-center justify-center mr-3">
                    <Award className="w-5 h-5 text-amber-600" />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-slate-900">
                      Early Adopter
                    </p>
                    <p className="text-xs text-slate-500">
                      Joined in first month
                    </p>
                  </div>
                </div>
                <div className="flex items-center">
                  <div className="w-10 h-10 rounded-full bg-teal-100 flex items-center justify-center mr-3">
                    <BookOpen className="w-5 h-5 text-teal-600" />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-slate-900">
                      Knowledge Sharer
                    </p>
                    <p className="text-xs text-slate-500">
                      Completed 10+ sessions
                    </p>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </PageContainer>);

}