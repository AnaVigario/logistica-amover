import { useState, useEffect } from 'react';
import Header from './components/Header';
import MainMenu from './components/MainMenu';
import DriversPage from './pages/DriversPage';
import SettingsPage from './pages/SettingsPage';
import AssistancePage from './pages/AssistancePage';
import CalendarPage from './pages/CalendarPage';
import TasksPage from './pages/TasksPage';
import ProfilePage from './pages/ProfilePage';
import LoginPage from './pages/LoginPage';
import MotorcyclesPage from './pages/MotorcyclesPage';
import MaintenancePage from './pages/MaintenancePage';
import TaskAssignmentPage from "./pages/TaskAssignmentPage";
import ClientsPage from './pages/ClientsPage';
import UsersPage from './pages/UsersPage';
import CompaniesPage from './pages/CompaniesPage';
import RoutesPage from './pages/RoutesPage';


import { supabase } from './supabaseClient';
import { User } from './types/auth';
import { TaskProvider } from './context/TaskContext';

import { Home, ClipboardCheck, Map, Calendar, Settings, LogOut, User as UserIcon } from 'lucide-react';
import { assignTask } from './api/tasksApi';

function App() {
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [selectedVehicleId, setSelectedVehicleId] = useState<number | null>(null);
  const [selectedRouteDate, setSelectedRouteDate] = useState<Date | null>(null);


  const [currentTime, setCurrentTime] = useState<string>('');
  const [currentPage, setCurrentPage] = useState<
  'main' | 'routes' | 'settings' | 'assistance' | 'calendar' |
  'tasks'   | 'taskAssignment'
| 'profile' | 'motorcycles' |
  'maintenance' | 'drivers' | 'clients' | 'users' | 'companies'
>('main');


  const navItems = [
    { id: 1, icon: <Home size={22} />, label: 'Main Page', page: 'main' as const },
    { id: 2, icon: <ClipboardCheck size={22} />, label: 'Tarefas', page: 'tasks' as const },
    { id: 3, icon: <ClipboardCheck size={22} />, label: 'Atribuição de Tarefas', page: 'taskAssignment' },
    { id: 4, icon: <Map size={22} />, label: 'Rotas', page: 'routes' as const },
    { id: 5, icon: <Calendar size={22} />, label: 'Calendário', page: 'calendar' as const },
    { id: 6, icon: <Settings size={22} />, label: 'Definições', page: 'settings' as const },
  ] as const;

  const adminNavItems = [
    { id: 7, icon: <UserIcon size={22} />, label: 'Gestão Utilizadores', page: 'users' as const },
  ];

  const visibleNavItems = currentUser?.role === 'admin'
    ? [...navItems, ...adminNavItems]
    : navItems;

  // ⏱️ Atualizar Hora
  useEffect(() => {
    const update = () => {
      const now = new Date();
      setCurrentTime(
        `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
      );
    };
    update();
    const interval = setInterval(update, 60000);
    return () => clearInterval(interval);
  }, []);

  // 🔄 Restaurar Sessão Supabase
  useEffect(() => {
    const restoreSession = async () => {
      const { data: sessionData } = await supabase.auth.getSession();
      const user = sessionData?.session?.user;
      if (!user) return;

      const { data: profile } = await supabase
        .from('profiles')
        .select('*')
        .eq('id', user.id)
        .single();

      if (profile) {
        setCurrentUser({
          id: user.id,
          name: profile.name,
          email: user.email ?? '',
          role: profile.role,
        });
      }
    };

    restoreSession();
  }, []);

  // 🚪 Logout
  const handleLogout = async () => {
    await supabase.auth.signOut();
    setCurrentUser(null);
    setCurrentPage('main');
  };

  const getPageTitle = () => ({
    main: 'Main Page',
    routes: 'Rotas',
    settings: 'Definições',
    assistance: 'Assistência',
    calendar: 'Calendário',
    tasks: 'Tarefas',
    taskAssignment: 'Atribuir Tarefas',
    profile: 'Perfil',
    motorcycles: 'Gestão de Motas',
    maintenance: 'Manutenções',
    drivers: 'Condutores',
    clients: 'Clientes',
   companies: 'Empresas & Serviços',
    users: 'Gestão de Utilizadores'
  }[currentPage]);

  // 📌 Mostrar Login enquanto não há User
  if (!currentUser) {
    return (
      <LoginPage
        onLogin={(user) => setCurrentUser(user)}
      />
    );
  }

  return (
    <TaskProvider>
      <div className="flex h-screen bg-background dark:bg-transparent">
        
        {/* Sidebar */}
        <div className="w-64 bg-white dark:bg-gray-800 border-r border-gray-200 dark:border-gray-700">
          <div className="flex justify-center items-center p-6 border-b">
            <button onClick={() => setCurrentPage('main')}>
              <img src="https://i.imgur.com/BdLgac3.png" className="h-12 object-contain" />
            </button>
          </div>

          <nav className="p-4 flex flex-col gap-1">
            {visibleNavItems.map(item => (
              <button
                key={item.id}
                onClick={() => setCurrentPage(item.page)}
                className={`flex items-center gap-3 px-4 py-2.5 rounded-lg transition-colors ${
                  currentPage === item.page 
                    ? 'bg-gray-100 dark:bg-gray-700 text-gray-900 dark:text-white'
                    : 'text-gray-600 dark:text-gray-300 hover:bg-gray-50 dark:bg-gray-700 hover:text-gray-900 dark:text-white'
                }`}
              >
                {item.icon}
                <span className="text-sm font-medium">{item.label}</span>
              </button>
            ))}
          </nav>

          <div className="mt-auto p-4 border-t">
            <button onClick={handleLogout} className="flex items-center gap-3 px-4 py-2.5 text-gray-600 dark:text-gray-300 hover:bg-gray-50 dark:bg-gray-700 hover:text-gray-900 dark:text-white w-full">
              <LogOut size={22} />
              <span className="text-sm font-medium">Sair</span>
            </button>
          </div>
        </div>

        {/* Conteúdo Principal */}
        <div className="flex-1 flex flex-col">
          <Header
            time={currentTime}
            title={getPageTitle()}
            user={currentUser}
            onProfileClick={() => setCurrentPage('profile')}
          />

          <main className="flex-1 overflow-auto p-6">

            {currentPage === 'main' && <MainMenu onMenuClick={setCurrentPage} />}
            {currentPage === 'taskAssignment' && (
  <TaskAssignmentPage
    onOpenRoute={(vehicleId, date) => {
      setSelectedVehicleId(vehicleId);
      setSelectedRouteDate(date);
      setCurrentPage("routes");
    }}
  />
)}
{currentPage === 'routes' && (
  <RoutesPage
    vehicleId={selectedVehicleId}
    date={selectedRouteDate}
  />
)}

            {currentPage === 'settings' && <SettingsPage onMenuClick={setCurrentPage} />}
            {currentPage === 'assistance' && <AssistancePage />}
            {currentPage === 'calendar' && <CalendarPage />}
            {currentPage === 'tasks' && (
  <TasksPage onOpenAssignment={() => setCurrentPage("taskAssignment")} />
)}


            {currentPage === 'profile' && <ProfilePage />}
            {currentPage === 'motorcycles' && <MotorcyclesPage />}
            {currentPage === 'maintenance' && <MaintenancePage />}
            {currentPage === 'drivers' && <DriversPage />}
            {currentPage === 'clients' && <ClientsPage />}
            {currentPage === 'companies' && <CompaniesPage />}
            { currentPage === 'users' && currentUser.role === 'admin' && <UsersPage />}
          </main>
        </div>

      </div>
    </TaskProvider>
  );
}

export default App;
