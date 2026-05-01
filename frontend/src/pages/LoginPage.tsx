import React, { useState } from 'react';
import { GraduationCap, ArrowRight } from 'lucide-react';
import { useAuth } from '../hooks/useAuth';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Card, CardContent } from '../components/ui/Card';

interface LoginPageProps {
  onSwitchToSignUp: () => void;
}

export function LoginPage({ onSwitchToSignUp }: LoginPageProps) {
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);
    try {
      await login(email, password);
    } catch {
      setError('Invalid email or password. Please try again.');
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
              <h3 className="text-lg font-medium text-slate-900">Welcome Back!</h3>
              <p className="text-sm text-slate-500">Sign in to access your dashboard</p>
            </div>

            {error && (
              <div className="mb-4 p-3 rounded-lg bg-red-50 border border-red-200 text-sm text-red-700">
                {error}
              </div>
            )}

            <form className="space-y-6" onSubmit={handleSubmit}>
              <Input
                label="School Email"
                type="email"
                placeholder="student@school.edu"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
              <Input
                label="Password"
                type="password"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />

              <Button
                type="submit"
                className="w-full"
                size="lg"
                isLoading={isLoading}
                rightIcon={<ArrowRight className="w-4 h-4" />}
              >
                Sign in
              </Button>
            </form>

            <div className="mt-6">
              <div className="relative">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-slate-200" />
                </div>
                <div className="relative flex justify-center text-sm">
                  <span className="bg-white px-2 text-slate-500">New to EULO?</span>
                </div>
              </div>

              <div className="mt-4 text-center">
                <button
                  type="button"
                  onClick={onSwitchToSignUp}
                  className="text-sm font-medium text-teal-600 hover:text-teal-700 underline underline-offset-2"
                >
                  Create an account
                </button>
              </div>

            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
