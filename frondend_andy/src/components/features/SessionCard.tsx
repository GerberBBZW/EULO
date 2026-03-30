import React from 'react';
import {
  Calendar,
  Clock,
  MapPin,
  Monitor,
  MessageSquare,
  CheckCircle } from
'lucide-react';
import { Card, CardContent } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { Avatar } from '../ui/Avatar';
import { Button } from '../ui/Button';
import { Session } from '../../types';
interface SessionCardProps {
  session: Session;
  role: 'seeker' | 'tutor';
  onAction?: (action: string) => void;
}
export function SessionCard({ session, role, onAction }: SessionCardProps) {
  const isTutor = role === 'tutor';
  const otherPersonName = isTutor ? session.seekerName : session.tutorName;
  const statusColors = {
    open: 'warning',
    matched: 'info',
    completed: 'success',
    cancelled: 'neutral'
  } as const;
  return (
    <Card className="overflow-hidden">
      <div className="flex flex-col sm:flex-row">
        {/* Left side: Date & Time */}
        <div className="bg-slate-50 p-5 flex flex-row sm:flex-col items-center justify-center sm:w-32 border-b sm:border-b-0 sm:border-r border-slate-100">
          <div className="text-center mr-6 sm:mr-0">
            <p className="text-xs font-bold text-slate-400 uppercase tracking-wider">
              {new Date(session.date).toLocaleDateString('en-US', {
                month: 'short'
              })}
            </p>
            <p className="text-2xl font-bold text-slate-900">
              {new Date(session.date).getDate()}
            </p>
          </div>
          <div className="text-sm text-slate-500 flex items-center mt-0 sm:mt-2">
            <Clock className="w-3.5 h-3.5 mr-1" />
            {new Date(session.date).toLocaleTimeString('en-US', {
              hour: '2-digit',
              minute: '2-digit'
            })}
          </div>
        </div>

        {/* Right side: Details */}
        <div className="flex-1 p-5">
          <div className="flex flex-col sm:flex-row sm:items-start justify-between mb-4">
            <div>
              <div className="flex items-center mb-1">
                <h3 className="font-bold text-lg text-slate-900 mr-2">
                  {session.subjectName}
                </h3>
                <Badge variant={statusColors[session.status]}>
                  {session.status}
                </Badge>
              </div>
              <div className="flex items-center text-sm text-slate-500">
                {session.mode === 'online' ?
                <Monitor className="w-3.5 h-3.5 mr-1" /> :

                <MapPin className="w-3.5 h-3.5 mr-1" />
                }
                <span className="capitalize">{session.mode}</span>
              </div>
            </div>

            {session.status === 'matched' &&
            <div className="mt-3 sm:mt-0 flex space-x-2">
                <Button
                size="sm"
                variant="outline"
                onClick={() => onAction?.('message')}>

                  <MessageSquare className="w-4 h-4 mr-1" />
                  Chat
                </Button>
                <Button
                size="sm"
                variant="primary"
                onClick={() => onAction?.('complete')}>

                  <CheckCircle className="w-4 h-4 mr-1" />
                  Complete
                </Button>
              </div>
            }
          </div>

          <div className="flex items-center p-3 bg-slate-50 rounded-lg">
            <Avatar
              name={otherPersonName || 'Unknown'}
              size="sm"
              className="mr-3" />

            <div>
              <p className="text-xs text-slate-500 uppercase font-medium">
                {isTutor ? 'Student' : 'Tutor'}
              </p>
              <p className="text-sm font-medium text-slate-900">
                {otherPersonName}
              </p>
            </div>
          </div>
        </div>
      </div>
    </Card>);

}