import React, { useEffect, useState } from "react";
import { supabase } from "../supabaseClient";
import { Plus, X, Pencil, Clock } from "lucide-react";

const ClientsPage: React.FC = () => {
  const [clients, setClients] = useState<Client[]>([]);
const [editClient, setEditClient] = useState<Client | null>(null);

  const [loading, setLoading] = useState(true);

  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showHistoryModal, setShowHistoryModal] = useState(false);

  const [selectedClient, setSelectedClient] = useState<any>(null);

  const [history, setHistory] = useState<any[]>([]);
  interface Client {
  id: number;
  name: string;
  nif: string;
  phone: string;
  email: string;
  street: string;
  door_number: string;
  floor?: string | null;
  postal_code: string;
  city: string;
}


 const [newClient, setNewClient] = useState({

  name: "",
  nif: "",
  phone: "",
  email: "",
  street: "",
  door_number: "",
  floor: "",
  postal_code: "",
  city: "",
});


  

  // CARREGAR CLIENTES
  useEffect(() => {
    async function load() {
      const { data, error } = await supabase.from("clients").select("*");

      if (error) console.error(error);
      else setClients(data);

      setLoading(false);
    }

    load();
  }, []);

  // ADICIONAR CLIENTE
  const addClient = async () => {
    if (!newClient.name || !newClient.nif) {
      alert("O nome e o NIF é obrigatório!");
      return;
    }

    const { data, error } = await supabase
      .from("clients")
      .insert([{
  name: newClient.name,
  nif: newClient.nif,
  phone: newClient.phone,
  email: newClient.email,
  street: newClient.street,
  door_number: newClient.door_number,
  floor: newClient.floor || null,
  postal_code: newClient.postal_code,
  city: newClient.city,
}])
      .select();

    if (error) {
      console.error(error);
      alert("Erro ao adicionar cliente.");
      return;
    }

    setClients((prev) => [...prev, data[0]]);
    setShowAddModal(false);

    setNewClient({
      name: "",
      nif:"",
      phone: "",
      email: "",
      street: "",
  door_number: "",
  floor: "",
  postal_code: "",
  city: "",
    });
  };

  // ELIMINAR CLIENTE
  const deleteClient = async (id: number) => {
    if (!confirm("Queres mesmo eliminar este cliente?")) return;

    const { error } = await supabase.from("clients").delete().eq("id", id);

    if (error) {
      console.error(error);
      alert("Erro ao eliminar.");
      return;
    }

    setClients((prev) => prev.filter((c) => c.id !== id));
  };

  // ABRIR MODAL DE EDITAR
  const openEdit = (client: Client) => {
  setEditClient(client);
  setShowEditModal(true);
};


  // GUARDAR EDIÇÃO
  const saveEdit = async () => {
  if (!editClient) return;

  const { error } = await supabase
    .from("clients")
    .update({
      name: editClient.name,
      nif: editClient.nif,
      phone: editClient.phone,
      email: editClient.email,
      street: editClient.street,
      door_number: editClient.door_number,
      floor: editClient.floor || null,
      postal_code: editClient.postal_code,
      city: editClient.city,
    })
    .eq("id", editClient.id);

  if (error) {
    console.error(error);
    alert("Erro ao guardar alterações.");
    return;
  }

  setClients((prev) =>
    prev.map((c) => (c.id === editClient.id ? editClient : c))
  );

  setShowEditModal(false);
  setEditClient(null);
};


  // ABRIR HISTÓRICO DE TAREFAS
  const openHistory = async (client: any) => {
    setSelectedClient(client);

    const { data, error } = await supabase
      .from("tasks")
      .select("id, title, status, date, driver_name")
      .eq("client_id", client.id);

    if (error) console.error(error);
    else setHistory(data);

    setShowHistoryModal(true);
  };

  return (
    <div className="p-6 bg-gray-100 h-full">
      <h2 className="text-xl font-semibold mb-4">Gestão de Clientes</h2>

      {loading ? (
        <p>A carregar...</p>
      ) : (
        <div className="space-y-4">
          {clients.map((client) => (
            <div
              key={client.id}
              className="bg-white p-4 rounded-lg shadow-md border flex justify-between items-center"
            >
              <div onClick={() => openHistory(client)} className="cursor-pointer">
                <h3 className="font-semibold text-lg">{client.name}</h3>
                <p className="text-sm text-gray-500">NIF: {client.nif}</p>
                <p className="text-sm text-gray-500">{client.email}</p>
                <p className="text-sm text-gray-500">{client.phone}</p>
                <p className="text-sm text-gray-400"> {client.street}, {client.door_number}
                {client.floor && `, ${client.floor}`} – {client.postal_code} {client.city} </p>

              </div>

              <div className="flex gap-2">
                {/* EDITAR */}
                <button
                  className="px-3 py-2 bg-blue-500 text-white rounded flex items-center gap-2"
                  onClick={() => openEdit(client)}
                >
                  <Pencil size={16} />
                  Editar
                </button>

                {/* ELIMINAR */}
                <button
                  className="px-3 py-2 bg-red-500 text-white rounded"
                  onClick={() => deleteClient(client.id)}
                >
                  Eliminar
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* BOTÃO ADICIONAR */}
      <button
        onClick={() => setShowAddModal(true)}
        className="fixed bottom-6 right-6 bg-black text-white p-4 rounded-full shadow-lg hover:bg-gray-700"
      >
        <Plus size={24} />
      </button>

   
      {/* MODAL ADICIONAR CLIENTE */}
      
      {showAddModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg w-full max-w-md p-6">
            <div className="flex justify-between items-center mb-4">
              <h3 className="font-semibold text-lg">Adicionar Cliente</h3>
              <button onClick={() => setShowAddModal(false)}>
                <X />
              </button>
            </div>

            <ClientForm state={newClient} setState={setNewClient} />

            <button
              className="w-full bg-black text-white py-2 rounded mt-4"
              onClick={addClient}
            >
              Guardar
            </button>
          </div>
        </div>
      )}

      
      {/* MODAL EDITAR CLIENTE */}
      
      {showEditModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg w-full max-w-md p-6">
            <div className="flex justify-between items-center mb-4">
              <h3 className="font-semibold text-lg">Editar Cliente</h3>
              <button onClick={() => setShowEditModal(false)}>
                <X />
              </button>
            </div>
{editClient && (
  <ClientForm state={editClient} setState={setEditClient} />
)}


            <button
              className="w-full bg-blue-600 text-white py-2 rounded mt-4"
              onClick={saveEdit}
            >
              Guardar Alterações
            </button>
          </div>
        </div>
      )}

     
      {/* MODAL HISTÓRICO DE TAREFAS */}
    
      {showHistoryModal && selectedClient && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg w-full max-w-2xl p-6">
            <div className="flex justify-between items-center mb-4">
              <h3 className="font-semibold text-xl">
                Histórico de Tarefas – {selectedClient.name}
              </h3>
              <button onClick={() => setShowHistoryModal(false)}>
                <X />
              </button>
            </div>

            {history.length === 0 ? (
              <p className="text-gray-500">Nenhuma tarefa encontrada.</p>
            ) : (
              <div className="space-y-3 max-h-96 overflow-y-auto">
                {history.map((task) => (
                  <div
                    key={task.id}
                    className="border p-3 rounded-lg bg-gray-50 flex justify-between"
                  >
                    <div>
                      <p className="font-semibold">{task.title}</p>
                      <p className="text-sm">{task.driver_name}</p>
                      <p className="text-xs text-gray-600">{task.date}</p>
                    </div>

                    <span
                      className={`text-sm font-medium px-3 py-1 rounded ${
                        task.status === "completed"
                          ? "bg-green-100 text-green-600"
                          : task.status === "active"
                          ? "bg-blue-100 text-blue-600"
                          : "bg-red-100 text-red-600"
                      }`}
                    >
                      {task.status}
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default ClientsPage;


const ClientForm = ({ state, setState }: any) => (
  <div className="space-y-3">
    <input
      type="text"
      placeholder="Nome *"
      className="w-full border p-2 rounded"
      value={state.name}
      onChange={(e) => setState({ ...state, name: e.target.value })}
    />
<input
      type="text"
      placeholder="NIF *"
      className="w-full border p-2 rounded"
      value={state.nif}
      onChange={(e) => setState({ ...state, nif: e.target.value })}
    />
    <input
      type="text"
      placeholder="Telefone"
      className="w-full border p-2 rounded"
      value={state.phone}
      onChange={(e) => setState({ ...state, phone: e.target.value })}
    />

    <input
      type="email"
      placeholder="Email"
      className="w-full border p-2 rounded"
      value={state.email}
      onChange={(e) => setState({ ...state, email: e.target.value })}
    />
<input
  type="text"
  placeholder="Rua *"
  className="w-full border p-2 rounded"
  value={state.street}
  onChange={(e) => setState({ ...state, street: e.target.value })}
/>

<input
  type="text"
  placeholder="Número *"
  className="w-full border p-2 rounded"
  value={state.door_number}
  onChange={(e) => setState({ ...state, door_number: e.target.value })}
/>

<input
  type="text"
  placeholder="Andar (opcional)"
  className="w-full border p-2 rounded"
  value={state.floor}
  onChange={(e) => setState({ ...state, floor: e.target.value })}
/>

<input
  type="text"
  placeholder="Código Postal *"
  className="w-full border p-2 rounded"
  value={state.postal_code}
  onChange={(e) => setState({ ...state, postal_code: e.target.value })}
/>

<input
  type="text"
  placeholder="Localidade *"
  className="w-full border p-2 rounded"
  value={state.city}
  onChange={(e) => setState({ ...state, city: e.target.value })}
/>

    

  </div>
);
