import React from 'react';
interface CardProps {
  children: React.ReactNode;
  className?: string;
  onClick?: () => void;
  hoverable?: boolean;
}
export function Card({
  children,
  className = '',
  onClick,
  hoverable = false
}: CardProps) {
  return (
    <div
      className={`
        bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden
        ${hoverable || onClick ? 'transition-all hover:shadow-md hover:border-teal-200 cursor-pointer' : ''}
        ${className}
      `}
      onClick={onClick}>

      {children}
    </div>);

}
export function CardHeader({
  children,
  className = ''



}: {children: React.ReactNode;className?: string;}) {
  return (
    <div className={`px-6 py-4 border-b border-slate-100 ${className}`}>
      {children}
    </div>);

}
export function CardTitle({
  children,
  className = ''



}: {children: React.ReactNode;className?: string;}) {
  return (
    <h3 className={`text-lg font-semibold text-slate-900 ${className}`}>
      {children}
    </h3>);

}
export function CardContent({
  children,
  className = ''



}: {children: React.ReactNode;className?: string;}) {
  return <div className={`p-6 ${className}`}>{children}</div>;
}
export function CardFooter({
  children,
  className = ''



}: {children: React.ReactNode;className?: string;}) {
  return (
    <div
      className={`px-6 py-4 bg-slate-50 border-t border-slate-100 ${className}`}>

      {children}
    </div>);

}