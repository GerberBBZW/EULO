import React from 'react';
import { Briefcase, ChevronDown } from 'lucide-react';
import { Card } from '../ui/Card';
interface VocationalGroupSelectorProps {
  selectedGroup: string;
  onChange: (group: string) => void;
}
const GROUPS = [
{
  id: 'it',
  name: 'Information Technology',
  icon: '💻'
},
{
  id: 'business',
  name: 'Business Administration',
  icon: '📊'
},
{
  id: 'healthcare',
  name: 'Healthcare',
  icon: '🩺'
},
{
  id: 'engineering',
  name: 'Engineering',
  icon: '⚙️'
}];

export function VocationalGroupSelector({
  selectedGroup,
  onChange
}: VocationalGroupSelectorProps) {
  const current = GROUPS.find((g) => g.name === selectedGroup) || GROUPS[0];
  return (
    <div className="relative group">
      <Card className="flex items-center justify-between p-4 cursor-pointer hover:border-teal-300 transition-colors">
        <div className="flex items-center">
          <div className="flex items-center justify-center w-10 h-10 rounded-full bg-teal-50 text-xl mr-3">
            {current.icon}
          </div>
          <div>
            <p className="text-xs font-medium text-slate-500 uppercase tracking-wider">
              Vocational Group
            </p>
            <p className="text-sm font-bold text-slate-900">{current.name}</p>
          </div>
        </div>
        <ChevronDown className="w-5 h-5 text-slate-400" />
      </Card>

      {/* Dropdown would go here in a real implementation, simplified for this demo */}
      <div className="absolute top-full left-0 right-0 mt-2 bg-white rounded-xl shadow-xl border border-slate-100 overflow-hidden z-10 hidden group-hover:block">
        {GROUPS.map((group) =>
        <button
          key={group.id}
          onClick={() => onChange(group.name)}
          className={`w-full text-left px-4 py-3 flex items-center hover:bg-slate-50 transition-colors ${selectedGroup === group.name ? 'bg-teal-50 text-teal-900' : 'text-slate-700'}`}>

            <span className="mr-3 text-lg">{group.icon}</span>
            <span className="font-medium">{group.name}</span>
          </button>
        )}
      </div>
    </div>);

}