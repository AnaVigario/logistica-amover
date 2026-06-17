import React from 'react';
import UserProfile from './UserProfile';
import { User } from '../types/auth';

interface HeaderProps {
  time: string;
  title: string;
  user: User | null;
  onProfileClick: () => void;
}

const Header: React.FC<HeaderProps> = ({ time, title, user, onProfileClick }) => {
  return (
    <div className="bg-white border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-6 py-4 flex justify-between items-center">
        <div className="flex items-center gap-4">
          <h1 className="text-2xl font-semibold text-gray-900">{title}</h1>
        </div>
        <div className="flex items-center gap-6">
          <UserProfile name={user?.name ?? ""} onClick={onProfileClick} />

          <div className="text-gray-600 font-medium">{time}</div>
        </div>
      </div>
    </div>
  );
};

export default Header;