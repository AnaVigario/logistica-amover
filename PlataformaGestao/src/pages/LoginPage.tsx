import React, { useState, useEffect } from 'react';
import { supabase } from '../supabaseClient';
import { User } from '../types/auth';

interface LoginPageProps {
  onLogin: (user: User) => void;
  
}

const LoginPage: React.FC<LoginPageProps> = ({ onLogin }) => {
  const [identifier, setIdentifier] = useState('');
  const [password, setPassword] = useState('');
  const [rememberMe, setRememberMe] = useState(false);
  const [error, setError] = useState('');

  // Auto-login se email estiver guardado
  useEffect(() => {
    const savedEmail = localStorage.getItem('rememberEmail');
    if (savedEmail) {
      setIdentifier(savedEmail);
    }
  }, []);

  async function handleLogin(e: React.FormEvent) {
    e.preventDefault();
    setError("");

    // 1️⃣ Login no Supabase Auth
    const { data, error: loginError } = await supabase.auth.signInWithPassword({
      email: identifier,
      password,
    });

    if (loginError || !data.user) {
      setError("Credenciais inválidas");
      return;
    }

    const userId = data.user.id;

    // 2️⃣ Buscar o perfil
    const { data: profile, error: profileError } = await supabase
      .from("profiles")
      .select("*")
      .eq("id", userId)
      .single();

    if (profileError || !profile) {
      setError("Perfil não encontrado!");
      return;
    }

    // 3️⃣ Guardar sessão local
    const loggedUser: User = {
      id: userId,
      name: profile.name,
      email: identifier,
      role: profile.role,
    };

    if (rememberMe) {
      localStorage.setItem("rememberEmail", identifier);
    } else {
      localStorage.removeItem("rememberEmail");
    }

    onLogin(loggedUser);
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#717171]">
      <div className="w-full max-w-md p-8">
        <div className="flex justify-center mb-12">
          <img 
            src="https://imgur.com/cZ90AGv.png"
            alt="A-Mover Logo"
            className="h-40 object-contain"
          />
        </div>

        <form onSubmit={handleLogin} className="space-y-6">

          <input
            type="text"
            placeholder="Email"
            value={identifier}
            onChange={e => setIdentifier(e.target.value)}
            className="w-full px-4 py-3 bg-transparent border-b border-black text-black"
          />

          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            className="w-full px-4 py-3 bg-transparent border-b border-black text-black"
          />

          {error && <div className="text-red-600 text-sm text-center">{error}</div>}

          <div className="flex items-center">
            <input
              type="checkbox"
              id="remember"
              checked={rememberMe}
              onChange={(e) => setRememberMe(e.target.checked)}
              className="h-4 w-4 text-[#2EA043]"
            />
            <label htmlFor="remember" className="ml-2 text-sm text-black">
              Guardar Login
            </label>
          </div>

          <button type="submit" className="w-full py-3 bg-[#2EA043] text-white font-medium rounded">
            LOGIN
          </button>

          <button
  type="button"
  className="block w-full text-gray-400 cursor-not-allowed"
  disabled
>
  Recuperar Password (brevemente)
</button>


        </form>
      </div>
    </div>
  );
};

export default LoginPage;
