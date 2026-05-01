import React, { useState, useEffect } from 'react';
import { GraduationCap, ArrowRight, ArrowLeft } from 'lucide-react';
import { useAuth } from '../hooks/useAuth';
import { getVocationalGroups } from '../api';
import { VocationalGroup } from '../types';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Card, CardContent } from '../components/ui/Card';

interface SignUpPageProps {
  onSwitchToLogin: () => void;
}

export function SignUpPage({ onSwitchToLogin }: SignUpPageProps) {
  const { register } = useAuth();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [role, setRole] = useState<'student' | 'teacher'>('student');
  const [vocationalGroup, setVocationalGroup] = useState('');
  const [vocationalGroups, setVocationalGroups] = useState<VocationalGroup[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    getVocationalGroups()
      .then(setVocationalGroups)
      .catch(() => {});
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (password !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }

    setIsLoading(true);
    try {
      await register(name, email, password, role, vocationalGroup || undefined);
      // Auth context sets user → App renders dashboard automatically
    } catch (err: any) {
      const msg = err?.message ?? '';
      if (msg.includes('409') || msg.toLowerCase().includes('conflict')) {
        setError('An account with this email already exists.');
      } else if (msg.includes('400')) {
        setError('Please fill in all required fields correctly.');
      } else {
        setError('Registration failed. Please try again.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <div className="flex justify-center">
          <div className="flex items-center justify-center w-16 h-16 rounded-2xl bg-teal-600 text-white shadow-lg">
            <GraduationCap className="w-10 h-10" />
          </div>
        </div>
        <h2 className="mt-6 text-center text-3xl font-bold tracking-tight text-slate-900">EULO</h2>
        <p className="mt-2 text-center text-sm text-slate-600">
          Education Unlocked by Learning One-to-one
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <Card className="border-0 shadow-xl">
          <CardContent className="p-8">
            <div className="mb-6 text-center">
              <h3 className="text-lg font-medium text-slate-900">Create your account</h3>
              <p className="text-sm text-slate-500">Join EULO and start learning together</p>
            </div>

            {error && (
              <div className="mb-4 p-3 rounded-lg bg-red-50 border border-red-200 text-sm text-red-700">
                {error}
              </div>
            )}

            <form className="space-y-4" onSubmit={handleSubmit}>
              {/* Full Name */}
              <Input
                label="Full Name"
                type="text"
                placeholder="Alex Rivera"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />

              {/* Email */}
              <Input
                label="School Email"
                type="email"
                placeholder="student@school.edu"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />

              {/* Password */}
              <Input
                label="Password"
                type="password"
                placeholder="min. 6 characters"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />

              {/* Confirm Password */}
              <Input
                label="Confirm Password"
                type="password"
                placeholder="Repeat your password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
              />

              {/* Role */}
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  I am a…
                </label>
                <div className="grid grid-cols-2 gap-3">
                  {(['student', 'teacher'] as const).map((r) => (
                    <button
                      key={r}
                      type="button"
                      onClick={() => setRole(r)}
                      className={`py-2 px-4 rounded-lg border text-sm font-medium transition-colors ${
                        role === r
                          ? 'bg-teal-600 border-teal-600 text-white'
                          : 'bg-white border-slate-200 text-slate-700 hover:border-teal-400'
                      }`}
                    >
                      {r === 'student' ? '🎓 Student' : '🏫 Teacher'}
                    </button>
                  ))}
                </div>
              </div>

              {/* Vocational Group */}
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Vocational Group <span className="text-slate-400 font-normal">(optional)</span>
                </label>
                <select
                  value={vocationalGroup}
                  onChange={(e) => setVocationalGroup(e.target.value)}
                  className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent"
                >
                  <option value="">Select your field…</option>
                  {vocationalGroups.map((vg) => (
                    <option key={vg.id} value={vg.name}>
                      {vg.icon} {vg.name}
                    </option>
                  ))}
                </select>
              </div>

              <Button
                type="submit"
                className="w-full mt-2"
                size="lg"
                isLoading={isLoading}
                rightIcon={<ArrowRight className="w-4 h-4" />}
              >
                Create Account
              </Button>
            </form>

            <div className="mt-6 text-center">
              <button
                type="button"
                onClick={onSwitchToLogin}
                className="inline-flex items-center gap-1 text-sm text-teal-600 hover:text-teal-700 font-medium"
              >
                <ArrowLeft className="w-4 h-4" />
                Back to Sign In
              </button>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
