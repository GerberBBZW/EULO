import React from 'react';
import { Users, BookOpen } from 'lucide-react';
import { Card, CardContent } from '../ui/Card';
import { Subject } from '../../types';
interface SubjectCardProps {
  subject: Subject;
  onClick?: () => void;
}
export function SubjectCard({ subject, onClick }: SubjectCardProps) {
  return (
    <Card
      onClick={onClick}
      hoverable
      className="h-full border-l-4 border-l-teal-500">

      <CardContent className="p-5">
        <div className="flex justify-between items-start mb-3">
          <div
            className={`p-2 rounded-lg ${subject.category === 'vocational' ? 'bg-indigo-100 text-indigo-600' : 'bg-amber-100 text-amber-600'}`}>

            <BookOpen className="w-5 h-5" />
          </div>
          <span className="text-xs font-medium px-2 py-1 rounded-full bg-slate-100 text-slate-600">
            {subject.category === 'vocational' ? 'Vocational' : 'General'}
          </span>
        </div>

        <h3 className="font-bold text-slate-900 mb-1">{subject.name}</h3>

        <div className="flex items-center text-sm text-slate-500 mt-4">
          <Users className="w-4 h-4 mr-1.5" />
          <span>{subject.tutorCount} tutors available</span>
        </div>
      </CardContent>
    </Card>);

}