import { TutoringOffer, Session, Subject, VocationalGroup, User } from './types';

const BASE_URL = import.meta.env.VITE_API_URL ?? '/api';

const TOKEN_KEY = 'eulo_token';

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token);
}

export function clearToken(): void {
  localStorage.removeItem(TOKEN_KEY);
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = getToken();
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${BASE_URL}${path}`, { ...options, headers });

  if (res.status === 401) {
    clearToken();
    window.location.reload();
  }

  if (!res.ok) {
    const text = await res.text().catch(() => res.statusText);
    throw new Error(text || `HTTP ${res.status}`);
  }

  if (res.status === 204) return undefined as unknown as T;
  return res.json();
}

// Auth
export interface AuthResponse {
  token: string;
  user: User;
}

export async function login(email: string, password: string): Promise<AuthResponse> {
  return request<AuthResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  });
}

export async function register(
  name: string,
  email: string,
  password: string,
  role: string,
  vocationalGroup?: string
): Promise<AuthResponse> {
  return request<AuthResponse>('/auth/register', {
    method: 'POST',
    body: JSON.stringify({ name, email, password, role, vocationalGroup }),
  });
}

// Users
export async function getUser(id: string): Promise<User> {
  return request<User>(`/users/${id}`);
}

export async function updateUser(id: string, data: Partial<User>): Promise<User> {
  return request<User>(`/users/${id}`, { method: 'PUT', body: JSON.stringify(data) });
}

// Subjects
export async function getSubjects(): Promise<Subject[]> {
  return request<Subject[]>('/subjects');
}

// Vocational Groups
export async function getVocationalGroups(): Promise<VocationalGroup[]> {
  return request<VocationalGroup[]>('/vocational-groups');
}

// Tutoring Offers
export interface OfferFilters {
  search?: string;
  subject?: string;
  mode?: string;
}

export async function getOffers(filters?: OfferFilters): Promise<TutoringOffer[]> {
  const params = new URLSearchParams();
  if (filters?.search) params.set('search', filters.search);
  if (filters?.subject && filters.subject !== 'all') params.set('subject', filters.subject);
  if (filters?.mode && filters.mode !== 'all') params.set('mode', filters.mode);
  const qs = params.toString();
  return request<TutoringOffer[]>(`/offers${qs ? `?${qs}` : ''}`);
}

export async function getOffersByTutor(tutorId: string): Promise<TutoringOffer[]> {
  return request<TutoringOffer[]>(`/offers/tutor/${tutorId}`);
}

export async function createOffer(offer: Omit<TutoringOffer, 'id'>): Promise<TutoringOffer> {
  return request<TutoringOffer>('/offers', { method: 'POST', body: JSON.stringify(offer) });
}

export async function deleteOffer(id: string): Promise<void> {
  return request<void>(`/offers/${id}`, { method: 'DELETE' });
}

// Sessions
export async function getSessions(userId: string): Promise<Session[]> {
  return request<Session[]>(`/sessions?userId=${userId}`);
}

export async function createSession(session: Omit<Session, 'id'>): Promise<Session> {
  return request<Session>('/sessions', { method: 'POST', body: JSON.stringify(session) });
}

export async function updateSessionStatus(id: string, status: string): Promise<Session> {
  return request<Session>(`/sessions/${id}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ status }),
  });
}

// Dashboard
export interface DashboardStats {
  upcomingSessions: number;
  availableTutors: number;
  subjectsOffered: number;
  upcomingSession: Session | null;
}

export async function getDashboardStats(userId: string): Promise<DashboardStats> {
  return request<DashboardStats>(`/dashboard/stats?userId=${userId}`);
}
