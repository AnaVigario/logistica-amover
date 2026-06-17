import React, { useEffect, useState } from "react";
import { supabase } from "../supabaseClient";
import { Search, Trash2, XCircle, CheckCircle, Plus } from "lucide-react";

const UsersPage: React.FC = () => {
  const [users, setUsers] = useState<any[]>([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  const [showAddModal, setShowAddModal] = useState(false);
  const [newUser, setNewUser] = useState({
    name: "",
    email: "",
    password: "",
    nif: "",
  });

  
  const loadUsers = async () => {
    setLoading(true);

    const { data, error } = await supabase
      .from("profiles")
      .select("*")
      .eq("role", "manager")
      .order("created_at", { ascending: false });

    if (!error && data) setUsers(data);
    setLoading(false);
  };

  useEffect(() => {
    loadUsers();
  }, []);

 
  const createUser = async () => {
    const { name, email, password, nif } = newUser;

    if (!name || !email || !password) {
      alert("Preenche todos os campos obrigatórios");
      return;
    }

    //  Criar utilizador Auth
    const { data, error } = await supabase.auth.signUp({
      email,
      password,
    });

    if (error || !data.user) {
      console.error(error);
      alert("Erro ao criar conta!");
      return;
    }

    //  Criar perfil
    await supabase.from("profiles").insert({
      id: data.user.id,
      name,
      role: "manager",
      nif,
      is_active: true,
    });

    setShowAddModal(false);
    setNewUser({ name: "", email: "", password: "", nif: "" });
    loadUsers();
  };

 
  const toggleActive = async (user: any) => {
    await supabase
      .from("profiles")
      .update({ is_active: !user.is_active })
      .eq("id", user.id);

    loadUsers();
  };

 
  const deleteUser = async (user: any) => {
    if (!confirm("Tem a certeza que pretende eliminar este gestor?")) return;

    await supabase.from("profiles").delete().eq("id", user.id);
    loadUsers();
  };

  const filteredUsers = users.filter((u) =>
    u.name.toLowerCase().includes(search.toLowerCase())
  );

 
  return (
    <div className="p-6 space-y-4">

      {/* TOPO */}
      <div className="flex justify-between items-center">
        <div className="flex items-center gap-2 w-full max-w-md">
          <Search size={18} />
          <input
            className="border rounded px-3 py-2 w-full"
            placeholder="Pesquisar gestor..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        <button
          onClick={() => setShowAddModal(true)}
          className="flex items-center gap-2 bg-black text-white px-4 py-2 rounded"
        >
          <Plus size={18} />
          Criar Gestor
        </button>
      </div>

      
      {loading && <p>A carregar...</p>}

      {!loading && filteredUsers.length === 0 && (
        <p>Nenhum gestor encontrado.</p>
      )}

      <div className="space-y-3">
        {filteredUsers.map((u) => (
          <div
            key={u.id}
            className="bg-white border rounded-lg p-4 flex justify-between"
          >
            <div>
              <p className="font-medium">{u.name}</p>
              <p className="text-sm text-gray-500">{u.email}</p>
              {!u.is_active && (
                <p className="text-xs text-red-600 font-semibold">
                  Conta desativada
                </p>
              )}
            </div>

            <div className="flex gap-3">
              <button
                title={u.is_active ? "Desativar" : "Reativar"}
                className="p-2 text-blue-600 hover:text-blue-800"
                onClick={() => toggleActive(u)}
              >
                {u.is_active ? <XCircle size={22} /> : <CheckCircle size={22} />}
              </button>

              <button
                title="Eliminar"
                className="p-2 text-red-600 hover:text-red-800"
                onClick={() => deleteUser(u)}
              >
                <Trash2 size={22} />
              </button>
            </div>
          </div>
        ))}
      </div>

      {showAddModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center">
          <div className="bg-white rounded-lg w-full max-w-md p-6 space-y-4">
            <h3 className="font-semibold text-lg">Criar Novo Gestor</h3>

            <input
              placeholder="Nome"
              className="w-full border p-2 rounded"
              value={newUser.name}
              onChange={(e) =>
                setNewUser({ ...newUser, name: e.target.value })
              }
            />

            <input
              placeholder="Email"
              className="w-full border p-2 rounded"
              value={newUser.email}
              onChange={(e) =>
                setNewUser({ ...newUser, email: e.target.value })
              }
            />

            <input
              type="password"
              placeholder="Password"
              className="w-full border p-2 rounded"
              value={newUser.password}
              onChange={(e) =>
                setNewUser({ ...newUser, password: e.target.value })
              }
            />

            <input
              placeholder="NIF"
              className="w-full border p-2 rounded"
              value={newUser.nif}
              onChange={(e) =>
                setNewUser({ ...newUser, nif: e.target.value })
              }
            />

            <div className="flex gap-3">
              <button
                onClick={createUser}
                className="flex-1 bg-black text-white py-2 rounded"
              >
                Guardar
              </button>
              <button
                onClick={() => setShowAddModal(false)}
                className="flex-1 bg-gray-200 py-2 rounded"
              >
                Cancelar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default UsersPage;
