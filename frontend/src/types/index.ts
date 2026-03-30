export type UserRole = 'student' | 'teacher' | 'admin';

export interface User {
  id: string;
  name: string;
  email: string;
  role: UserRole;
  vocationalGroup?: string;
  avatarUrl?: string;
  bio?: string;
  subjectsTutored?: string[];
  sessionsCompleted?: number;
}

export interface VocationalGroup {
  id: string;
  name: string;
  description: string;
  subjects: Subject[];
}

export interface Subject {
  id: string;
  name: string;
  category: 'vocational' | 'general';
  tutorCount: number;
}

export type TutoringMode = 'online' | 'onsite' | 'both';

export interface TutoringOffer {
  id: string;
  tutorId: string;
  tutorName: string;
  tutorAvatar?: string;
  subjectId: string;
  subjectName: string;
  mode: TutoringMode;
  description: string;
  availability: string;
}

export type SessionStatus = 'open' | 'matched' | 'completed' | 'cancelled';

export interface Session {
  id: string;
  seekerId: string;
  seekerName: string;
  tutorId?: string;
  tutorName?: string;
  subjectId: string;
  subjectName: string;
  status: SessionStatus;
  date: string;
  mode: TutoringMode;
  notes?: string;
}

export type Page =
'dashboard' |
'find-tutoring' |
'offer-tutoring' |
'my-sessions' |
'profile';