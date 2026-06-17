import React from 'react';
import { User } from 'lucide-react';

interface UserProfileProps {
  name: string;
  onClick?: () => void;
}

const UserProfile: React.FC<UserProfileProps> = ({ name, onClick }) => {
  return (
    <button 
      className="flex items-center gap-3 w-full px-2 py-1.5 rounded-lg hover:bg-gray-50 transition-colors"
      onClick={onClick}
    >
      <div className="bg-gray-100 rounded-full p-2 w-10 h-10 flex items-center justify-center">
        <User size={20} className="text-gray-600" />
      </div>
      <span className="text-sm font-medium text-gray-900 truncate">{name}</span>
    </button>
  );
};

export default UserProfile;