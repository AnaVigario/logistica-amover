import React, { useState, useRef } from 'react';
import { User, PenSquare, Camera, X } from 'lucide-react';

interface ProfileData {
  name: string;
  driverLicense: string;
  citizenCard: string;
  phone: string;
  address: string;
  email: string;
  photo?: string;
}

const ProfilePage: React.FC = () => {
  const [isEditing, setIsEditing] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [profileData, setProfileData] = useState<ProfileData>({
    name: 'Nuno Melo',
    driverLicense: '',
    citizenCard: '',
    phone: '',
    address: '',
    email: '',
    photo: ''
  });

  const handleInputChange = (field: keyof ProfileData) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setProfileData(prev => ({
      ...prev,
      [field]: e.target.value
    }));
  };

  const handlePhotoClick = () => {
    fileInputRef.current?.click();
  };

  const handlePhotoChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setProfileData(prev => ({
          ...prev,
          photo: reader.result as string
        }));
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSave = () => {
    setIsEditing(false);
    console.log('Saving profile data:', profileData);
  };

  if (isEditing) {
    return (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
        <div className="bg-white dark:bg-gray-800 rounded-lg w-full max-w-md">
          <div className="flex justify-between items-center p-4 border-b">
            <h3 className="font-semibold">Editar Perfil</h3>
            <button onClick={() => setIsEditing(false)}>
              <X className="w-5 h-5" />
            </button>
          </div>

          <div className="p-6">
            <div className="flex flex-col items-center mb-8">
              <div className="relative">
                <div 
                  onClick={handlePhotoClick}
                  className="bg-[#333333] rounded-full w-24 h-24 flex items-center justify-center cursor-pointer overflow-hidden"
                >
                  {profileData.photo ? (
                    <img 
                      src={profileData.photo} 
                      alt="Profile" 
                      className="w-full h-full object-cover"
                    />
                  ) : (
                    <User size={48} color="white" />
                  )}
                </div>
                <button 
                  onClick={handlePhotoClick}
                  className="absolute bottom-0 right-0 bg-gray-200 rounded-full p-2 hover:bg-gray-300 transition-colors"
                >
                  <Camera size={20} />
                </button>
                <input
                  ref={fileInputRef}
                  type="file"
                  accept="image/*"
                  onChange={handlePhotoChange}
                  className="hidden"
                />
              </div>
              <input
                type="text"
                value={profileData.name}
                onChange={handleInputChange('name')}
                className="mt-4 text-center border-b border-gray-300 dark:border-gray-600 px-2 py-1"
              />
            </div>

            <div className="space-y-4">
              <div>
                <label className="block text-sm text-gray-600 dark:text-gray-300 mb-1">Carta de Condução:</label>
                <input
                  type="text"
                  value={profileData.driverLicense}
                  onChange={handleInputChange('driverLicense')}
                  className="w-full border-b border-gray-300 dark:border-gray-600 px-2 py-1"
                  placeholder="Digite o número da carta de condução"
                />
              </div>

              <div>
                <label className="block text-sm text-gray-600 dark:text-gray-300 mb-1">Cartão de Cidadão:</label>
                <input
                  type="text"
                  value={profileData.citizenCard}
                  onChange={handleInputChange('citizenCard')}
                  className="w-full border-b border-gray-300 dark:border-gray-600 px-2 py-1"
                  placeholder="Digite o número do cartão de cidadão"
                />
              </div>

              <div>
                <label className="block text-sm text-gray-600 dark:text-gray-300 mb-1">Telemóvel:</label>
                <input
                  type="tel"
                  value={profileData.phone}
                  onChange={handleInputChange('phone')}
                  className="w-full border-b border-gray-300 dark:border-gray-600 px-2 py-1"
                  placeholder="Digite o número de telemóvel"
                />
              </div>

              <div>
                <label className="block text-sm text-gray-600 dark:text-gray-300 mb-1">Morada:</label>
                <input
                  type="text"
                  value={profileData.address}
                  onChange={handleInputChange('address')}
                  className="w-full border-b border-gray-300 dark:border-gray-600 px-2 py-1"
                  placeholder="Digite a morada"
                />
              </div>

              <div>
                <label className="block text-sm text-gray-600 dark:text-gray-300 mb-1">Email:</label>
                <input
                  type="email"
                  value={profileData.email}
                  onChange={handleInputChange('email')}
                  className="w-full border-b border-gray-300 dark:border-gray-600 px-2 py-1"
                  placeholder="Digite o email"
                />
              </div>
            </div>

            <div className="flex gap-3 mt-8">
              <button
                className="flex-1 px-4 py-2 bg-gray-200 rounded-lg text-gray-800 dark:text-gray-100 font-medium hover:bg-gray-300 transition-colors"
                onClick={() => setIsEditing(false)}
              >
                Cancelar
              </button>
              <button
                className="flex-1 px-4 py-2 bg-[#333333] text-white rounded-lg font-medium hover:bg-gray-700 transition-colors"
                onClick={handleSave}
              >
                Guardar Alterações
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full bg-background dark:bg-transparent p-4">
      <div className="flex justify-end gap-4 mb-6">
        <button 
          className="text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:text-white flex items-center gap-2"
          onClick={() => setIsEditing(true)}
        >
          <PenSquare size={18} />
          <span>Editar Perfil</span>
        </button>
      </div>

      <div className="bg-white dark:bg-gray-800 rounded-lg p-6 flex-1">
        <h2 className="text-xl font-semibold mb-8">Perfil</h2>
        
        <div className="flex flex-col items-center mb-12">
          <div className="bg-[#333333] rounded-full w-24 h-24 flex items-center justify-center overflow-hidden mb-4">
            {profileData.photo ? (
              <img 
                src={profileData.photo} 
                alt="Profile" 
                className="w-full h-full object-cover"
              />
            ) : (
              <User size={48} color="white" />
            )}
          </div>
          <div className="bg-gray-200 px-4 py-1 rounded-md">
            <span className="text-lg">{profileData.name}</span>
          </div>
        </div>

        <div className="space-y-6">
          <div className="border-b border-gray-300 dark:border-gray-600 pb-2">
            <label className="block text-sm text-gray-600 dark:text-gray-300">Carta de Condução:</label>
            <div className="h-6 mt-1">{profileData.driverLicense || '—'}</div>
          </div>

          <div className="border-b border-gray-300 dark:border-gray-600 pb-2">
            <label className="block text-sm text-gray-600 dark:text-gray-300">Cartão de Cidadão:</label>
            <div className="h-6 mt-1">{profileData.citizenCard || '—'}</div>
          </div>

          <div className="border-b border-gray-300 dark:border-gray-600 pb-2">
            <label className="block text-sm text-gray-600 dark:text-gray-300">Telemóvel:</label>
            <div className="h-6 mt-1">{profileData.phone || '—'}</div>
          </div>

          <div className="border-b border-gray-300 dark:border-gray-600 pb-2">
            <label className="block text-sm text-gray-600 dark:text-gray-300">Morada:</label>
            <div className="h-6 mt-1">{profileData.address || '—'}</div>
          </div>

          <div className="border-b border-gray-300 dark:border-gray-600 pb-2">
            <label className="block text-sm text-gray-600 dark:text-gray-300">Email:</label>
            <div className="h-6 mt-1">{profileData.email || '—'}</div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;