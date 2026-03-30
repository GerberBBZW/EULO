import React from 'react';
interface AvatarProps {
  src?: string;
  name: string;
  size?: 'sm' | 'md' | 'lg' | 'xl';
  className?: string;
}
export function Avatar({
  src,
  name,
  size = 'md',
  className = ''
}: AvatarProps) {
  const sizes = {
    sm: 'h-8 w-8 text-xs',
    md: 'h-10 w-10 text-sm',
    lg: 'h-12 w-12 text-base',
    xl: 'h-16 w-16 text-lg'
  };
  const getInitials = (name: string) => {
    return name.
    split(' ').
    map((n) => n[0]).
    join('').
    toUpperCase().
    substring(0, 2);
  };
  return (
    <div
      className={`relative inline-block rounded-full overflow-hidden bg-teal-100 text-teal-800 flex-shrink-0 ${sizes[size]} ${className}`}>

      {src ?
      <img src={src} alt={name} className="h-full w-full object-cover" /> :

      <div className="h-full w-full flex items-center justify-center font-bold">
          {getInitials(name)}
        </div>
      }
    </div>);

}