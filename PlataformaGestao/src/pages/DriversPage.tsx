import React, { useState, useEffect } from "react";
import { User, Plus, X } from "lucide-react";
import { supabase } from "../supabaseClient";
import { useMotorcycleStore } from "../types/motorcycle";
type NewDriver = {
  name: string;
  license: string;
  phone: string;
  email: string;
  status: string;
  company_id: number | null;
};

const DriversPage: React.FC = () => {
  const [drivers, setDrivers] = useState<any[]>([]);
  const [companies, setCompanies] = useState<any[]>([]);
  const { motorcycles } = useMotorcycleStore();
  const [selectedDriver, setSelectedDriver] = useState<number | null>(null);
  const [showAddModal, setShowAddModal] = useState(false);
  const [editingDriverId, setEditingDriverId] = useState<number | null>(null);
const [driverTasks, setDriverTasks] = useState<any[]>([]);
const [showHistoryModal, setShowHistoryModal] = useState(false);

  const [newDriver, setNewDriver] = useState<NewDriver>({
  name: "",
  license: "",
  phone: "",
  email: "",
  status: "active",
  company_id: null,
});




 useEffect(() => {
  async function loadData() {
    const { data: driversData } = await supabase.from("drivers").select("*");
    const { data: companiesData } = await supabase.from("companies").select("*");

    setDrivers(driversData || []);
    setCompanies(companiesData || []);
  }

  loadData();
}, []);


 
 const handleAddDriver = async () => {
 
  if (
    !newDriver.name ||
    !newDriver.license ||
    !newDriver.phone ||
    !newDriver.email ||
    !newDriver.company_id
  ) {
    alert("Preenche todos os campos obrigatórios!");
    return;
  }

  
  if (editingDriverId) {
    const { error } = await supabase
      .from("drivers")
      .update({
        name: newDriver.name,
        license: newDriver.license,
        phone: newDriver.phone,
        email: newDriver.email,
        status: newDriver.status,
        company_id: newDriver.company_id,
      })
      .eq("id", editingDriverId);

    if (error) {
      console.error(error);
      alert("Erro ao atualizar condutor");
      return;
    }

   
    setDrivers((prev) =>
      prev.map((driver) =>
        driver.id === editingDriverId
          ? { ...driver, ...newDriver }
          : driver
      )
    );
  } 
  
  else {
    const { data, error } = await supabase
      .from("drivers")
      .insert([
        {
          name: newDriver.name,
          license: newDriver.license,
          phone: newDriver.phone,
          email: newDriver.email,
          status: "active",
          company_id: newDriver.company_id,
        },
      ])
      .select()
      .single();

    if (error || !data) {
      console.error(error);
      alert("Erro ao adicionar condutor");
      return;
    }

    
    setDrivers((prev) => [...prev, data]);
  }

 
  setNewDriver({
    name: "",
    license: "",
    phone: "",
    email: "",
    status: "active",
    company_id: null,
  });

  setEditingDriverId(null);
  setShowAddModal(false);
};
const handleDeleteDriver = async (id: number) => {
  if (!confirm("Tens a certeza que queres eliminar este condutor?")) return;

  const { error } = await supabase
    .from("drivers")
    .delete()
    .eq("id", id);

  if (error) {
    alert("Erro ao eliminar condutor");
    return;
  }

  setDrivers((prev) => prev.filter((d) => d.id !== id));
};
const toggleStatus = async (driver: any) => {
  const newStatus = driver.status === "active" ? "inactive" : "active";

  const { error } = await supabase
    .from("drivers")
    .update({ status: newStatus })
    .eq("id", driver.id);

  if (error) {
    alert("Erro ao alterar estado");
    return;
  }

  setDrivers((prev) =>
    prev.map((d) =>
      d.id === driver.id ? { ...d, status: newStatus } : d
    )
  );
};

const getCompanyName = (companyId: number | null) => {
  const company = companies.find((c) => c.id === companyId);
  return company ? company.name : "—";
};

async function loadDriverTasks(driverId: number) {
  const { data, error } = await supabase
    .from("tasks")
    .select(`
      id,
      title,
      date,
      street,
      city,
      motorcycleid,
      vehicles (
        assigneddriverid
      )
    `)
    .not("motorcycleid", "is", null);

  if (error || !data) return;

  const filtered = data.filter(
    (t: any) => t.vehicles?.assigneddriverid === driverId
  );

  setDriverTasks(filtered);
}
  return (
    <div className="flex flex-col h-full bg-[#d6d6d6] p-6">
      {/* LISTA DE CONDUTORES */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-6">
        {drivers.map((driver) => (
  <div
    key={driver.id}
    className={`bg-white rounded-lg p-6 shadow-sm hover:shadow-md transition-shadow cursor-pointer ${
      driver.status === "inactive" ? "opacity-50" : ""
    }`}
onClick={() => {
  setSelectedDriver(driver.id);
  loadDriverTasks(driver.id);
  setShowHistoryModal(true);
}}  >
    <div className="flex items-center gap-4 mb-4">
      <div className="bg-gray-100 rounded-full p-4">
        <User size={32} className="text-gray-600" />
      </div>

      <div>
        <h3 className="font-semibold text-lg">{driver.name}</h3>
        <p className="text-sm text-gray-500">Licença: {driver.license}</p>
      </div>
    </div>
    <p className="text-sm text-gray-600">
  Empresa: {getCompanyName(driver.company_id)}
</p>

    <p className="text-sm text-gray-600">Email: {driver.email}</p>
    <p className="text-sm text-gray-600">Telefone: {driver.phone}</p>

    <div className="mt-4 flex items-center justify-between">
      <span
        className={`px-3 py-1 rounded-full text-sm ${
          driver.status === "active"
            ? "bg-green-100 text-green-800"
            : "bg-red-100 text-red-800"
        }`}
      >
        {driver.status === "active" ? "Ativo" : "Inativo"}
      </span>

      {/* BOTÕES */}
      <div className="flex gap-3 text-sm">
        {/* EDITAR */}
        <button
          onClick={(e) => {
            e.stopPropagation(); 
            setNewDriver({
              name: driver.name,
              license: driver.license,
              phone: driver.phone,
              email: driver.email,
              status: driver.status,
              company_id: driver.company_id,
            });
            setEditingDriverId(driver.id);
            setShowAddModal(true);
          }}
          className="text-blue-600 hover:underline"
        >
          Editar
        </button>

        {/* ATIVAR / DESATIVAR */}
        <button
          onClick={(e) => {
            e.stopPropagation();
            toggleStatus(driver);
          }}
          className="text-yellow-600 hover:underline"
        >
          {driver.status === "active" ? "Desativar" : "Ativar"}
        </button>

        {/* ELIMINAR */}
        <button
          onClick={(e) => {
            e.stopPropagation();
            handleDeleteDriver(driver.id);
          }}
          className="text-red-600 hover:underline"
        >
          Eliminar
        </button>
      </div>
    </div>
  </div>
))}

      </div>

      {/* BOTÃO "ADICIONAR" */}
      <button
  onClick={() => {
    setNewDriver({
      name: "",
      license: "",
      phone: "",
      email: "",
      status: "active",
      company_id: null,
    });
    setEditingDriverId(null); 
    setShowAddModal(true);
  }}
  className="fixed bottom-6 right-6 bg-[#333333] text-white p-4 rounded-full"
>
  +
</button>


      {/* MODAL ADICIONAR DRIVER */}
      {showAddModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-lg w-full max-w-md">
            <div className="flex justify-between items-center p-4 border-b">
              <h3 className="text-xl font-semibold">
  {editingDriverId ? "Editar Condutor" : "Adicionar Novo Condutor"}
</h3>

              <button onClick={() => setShowAddModal(false)}>
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="p-6 space-y-4">
              <div>
                <label className="block text-sm text-gray-600 mb-1">Nome *</label>
                <input
                  type="text"
                  value={newDriver.name}
                  onChange={(e) =>
                    setNewDriver({ ...newDriver, name: e.target.value })
                  }
                  className="w-full border p-2 rounded"
                />
              </div>
<div>
  <label className="block text-sm text-gray-600 mb-1">
    Empresa *
  </label>
  <select
    value={newDriver.company_id || ""}
    onChange={(e) =>
      setNewDriver({
        ...newDriver,
        company_id: e.target.value
          ? Number(e.target.value)
          : null,
      })
    }
    className="w-full border p-2 rounded"
  >
    <option value="">Selecionar empresa</option>
    {companies.map((company) => (
      <option key={company.id} value={company.id}>
        {company.name}
      </option>
    ))}
  </select>
</div>

              <div>
                <label className="block text-sm text-gray-600 mb-1">
                  Licença *
                </label>
                <input
                  type="text"
                  value={newDriver.license}
                  onChange={(e) =>
                    setNewDriver({ ...newDriver, license: e.target.value })
                  }
                  className="w-full border p-2 rounded"
                />
              </div>

              <div>
                <label className="block text-sm text-gray-600 mb-1">
                  Telefone *
                </label>
                <input
                  type="text"
                  value={newDriver.phone}
                  onChange={(e) =>
                    setNewDriver({ ...newDriver, phone: e.target.value })
                  }
                  className="w-full border p-2 rounded"
                />
              </div>

              <div>
                <label className="block text-sm text-gray-600 mb-1">
                  Email *
                </label>
                <input
                  type="email"
                  value={newDriver.email}
                  onChange={(e) =>
                    setNewDriver({ ...newDriver, email: e.target.value })
                  }
                  className="w-full border p-2 rounded"
                />
              </div>

              <button
                className="w-full mt-4 bg-[#333333] text-white py-2 rounded font-semibold disabled:opacity-50"
                onClick={handleAddDriver}
               disabled={
  !newDriver.name ||
  !newDriver.company_id ||
  !newDriver.license ||
  !newDriver.phone ||
  !newDriver.email
}

              >
                {editingDriverId ? "Guardar Alterações" : "Adicionar"}
              </button>
            </div>
          </div>
        </div>
      )}

      {showHistoryModal && selectedDriver && (
  <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
    <div className="bg-white rounded-xl w-full max-w-lg p-6">

      <div className="flex justify-between mb-4">
        <h3 className="font-semibold text-lg">
          Histórico de Tarefas
        </h3>

        <button onClick={() => setShowHistoryModal(false)}>
          <X />
        </button>
      </div>

      {driverTasks.length === 0 ? (
        <p className="text-gray-500 text-sm">
          Nenhuma tarefa encontrada para este motorista.
        </p>
      ) : (
        <div className="space-y-3 max-h-[60vh] overflow-y-auto">
          {driverTasks.map((task) => (
            <div
              key={task.id}
              className="border rounded p-3"
            >
              <div className="font-medium">
                {task.title}
              </div>

              <div className="text-sm text-gray-500">
                {task.date}
              </div>

              <div className="text-sm text-gray-500">
                {task.street} · {task.city}
              </div>
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

export default DriversPage;
