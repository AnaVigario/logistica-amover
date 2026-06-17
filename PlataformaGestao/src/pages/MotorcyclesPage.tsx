import React, { useState, useEffect } from 'react';
import { Plus } from 'lucide-react';
import { supabase } from '../supabaseClient';
import { X , AlertCircle} from "lucide-react";


const MotorcyclesPage: React.FC = () => {
  const [dbMotorcycles, setDbMotorcycles] = useState<any[]>([]);
  const [assignments, setAssignments] = useState<any[]>([]);
  const [dbDrivers, setDbDrivers] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedMotoMenu, setSelectedMotoMenu] = useState<any>(null);



  const [showAddModal, setShowAddModal] = useState(false);
  const [showAssignModal, setShowAssignModal] = useState(false);
  const [selectedMotoForAssignment, setSelectedMotoForAssignment] = useState<any>(null);
  const [showEditModal, setShowEditModal] = useState(false);
const [selectedMotoForEdit, setSelectedMotoForEdit] = useState<any>(null);
const [selectedMotoForMaintenance, setSelectedMotoForMaintenance] = useState<any>(null);
const [maintenanceReason, setMaintenanceReason] = useState("");



  // FORM da mota nova
  const [newVehicle, setNewVehicle] = useState({
  matricula: '',
  name: '',
  marca: '',
  modelo: '',
  status: 'Disponível',
  battery_capacity: null as number | null,
  cargo_capacity: null as number | null,
});



  useEffect(() => {
    async function loadData() {
      // 1 - MOTAS
      const { data: motos, error: err1 } = await supabase
        .from('vehicles')
        .select('*');

      if (!err1 && motos) setDbMotorcycles(motos);

      const { data: assigns, error: err2 } = await supabase
        .from('motorcycle_assignments')
        .select('*')
        .is('enddate', null);

      if (!err2 && assigns) setAssignments(assigns);

      // 3 - DRIVERS
      const { data: driversData, error: err3 } = await supabase
        .from('drivers')
        .select('*');

      if (!err3 && driversData) setDbDrivers(driversData);

      setLoading(false);
    }

    loadData();
  }, []);

  // Obter assignment ativo para uma mota
  const getActiveAssignment = (motorcycleId: number) =>
    assignments.find((a) => a.motorcycleid === motorcycleId && a.enddate === null) || null;

  const openAssignModal = (moto: any) => {
    setSelectedMotoForAssignment(moto);
    setShowAssignModal(true);
  };

  // Drivers disponíveis = sem assignment ativo
  const availableDrivers = dbDrivers.filter(
  (d) =>
    d.status === "active" &&
    !assignments.some((a) => a.driverid === d.id && a.enddate === null)
);


 
  const assignMotorcycle = async (moto: any, driver: any) => {
    // 1) criar registo em motorcycle_assignments
    const { data, error } = await supabase
      .from('motorcycle_assignments')
      .insert({
        motorcycleid: moto.id,
        driverid: driver.id,
        startdate: new Date().toISOString(),
        enddate: null,
      })
      .select();

    if (error || !data) {
      console.error(error);
      alert('Erro ao atribuir mota.');
      return;
    }

    const newAssignment = data[0];

    // 2) atualizar status da mota na TABELA motorcycles
    const { error: updError } = await supabase
      .from('vehicles')
      .update({
        status: 'Em uso',         
        assigneddriverid: driver.id,
      })
      .eq('id', moto.id);

    if (updError) {
      console.error(updError);
      alert('Assignment criado, mas falhou ao atualizar o estado da mota.');
      return;
    }

    // 3) atualizar estado local
    setAssignments((prev) => [...prev, newAssignment]);
    setDbMotorcycles((prev) =>
      prev.map((m) =>
        m.id === moto.id
          ? { ...m, status: 'Em uso', assigneddriverid: driver.id }
          : m
      )
    );

    setShowAssignModal(false);
    setSelectedMotoForAssignment(null);
  };


  const unassignMotorcycle = async (motoId: number) => {
    // 1) procurar assignment ativa
    const active = assignments.find(
      (a) => a.motorcycleid === motoId && a.enddate === null
    );

    if (!active) {
      console.warn('Não existe assign ativo para esta mota.');
      return;
    }

    // 2) fechar assignment na BD
    const { error } = await supabase
      .from('motorcycle_assignments')
      .update({ enddate: new Date().toISOString() })
      .eq('id', active.id);

    if (error) {
      console.error('Erro ao desatribuir:', error);
      alert('Erro ao desatribuir.');
      return;
    }

    // 3) voltar mota a Disponível na tabela motorcycles
    const { error: updError } = await supabase
      .from('vehicles')
      .update({
        status: 'Disponível',
        assigneddriverid: null,
      })
      .eq('id', motoId);

    if (updError) {
      console.error('Erro ao atualizar mota:', updError);
      alert('Assignment fechado, mas falhou ao atualizar o estado da mota.');
      return;
    }

    // 4) atualizar estado local
    setAssignments((prev) => prev.filter((a) => a.id !== active.id));
    setDbMotorcycles((prev) =>
      prev.map((m) =>
        m.id === motoId ? { ...m, status: 'Disponível', assigneddriverid: null } : m
      )
    );
  };

 
  const addMotorcycle = async () => {
    if (!newVehicle.matricula || !newVehicle.marca || !newVehicle.modelo) {
      alert('Preenche todos os campos!');
      return;
    }

    const { data, error } = await supabase
      .from('vehicles')
      .insert([
  {
    name: newVehicle.name,
    marca: newVehicle.marca,
    modelo: newVehicle.modelo,
    matricula: newVehicle.matricula,
    status: 'Disponível',
    assigneddriverid: null,
    maintenancereason: null,
    maintenancedate: null,
    battery_capacity: newVehicle.battery_capacity,
    cargo_capacity: newVehicle.cargo_capacity,
  },
])
.select()
.single();


    if (error || !data) {
      console.error(error);
      alert('Erro ao guardar a mota.');
      return;
    }

    setDbMotorcycles((prev) => [...prev, data]);
    setShowAddModal(false);

    setNewVehicle({
     matricula: '',
  name: '',
  marca: '',
  modelo: '',
  status: 'Disponível',
  battery_capacity: null as number | null,
  cargo_capacity: null as number | null,
    });
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Disponível':
        return 'text-green-600';
      case 'Em uso': 
        return 'text-orange-500';
      case 'Manutenção':
        return 'text-red-600';
      default:
        return 'text-gray-600';
    }
  };

  
  const getStatusLabel = (status: string) =>
    status === 'Em uso' ? 'Em Uso' : status;
const updateMotorcycle = async () => {
  if (!selectedMotoForEdit) return;

  const { error } = await supabase
    .from("vehicles")
    .update({
      name: selectedMotoForEdit.name,
      marca: selectedMotoForEdit.marca,
      modelo: selectedMotoForEdit.modelo,
      matricula: selectedMotoForEdit.matricula,
      battery_capacity: selectedMotoForEdit.battery_capacity,
      cargo_capacity: selectedMotoForEdit.cargo_capacity,
    })
    .eq("id", selectedMotoForEdit.id);

  if (error) {
    console.error(error);
    alert("Erro ao atualizar mota.");
    return;
  }

  setDbMotorcycles(prev =>
    prev.map(m =>
      m.id === selectedMotoForEdit.id ? selectedMotoForEdit : m
    )
  );

  setShowEditModal(false);
  setSelectedMotoForEdit(null);
};
const deleteMotorcycle = async (moto: any) => {
  if (moto.status === "Em uso") {
    alert("Não podes apagar uma mota que está em uso.");
    return;
  }

  if (!confirm("Tens a certeza que queres apagar esta mota?")) return;

  const { error } = await supabase
    .from("vehicles")
    .delete()
    .eq("id", moto.id);

  if (error) {
    console.error(error);
    alert("Erro ao apagar mota.");
    return;
  }

  setDbMotorcycles(prev => prev.filter(m => m.id !== moto.id));
};
async function confirmSendToMaintenance() {
  if (!selectedMotoForMaintenance || !maintenanceReason) return;

  // criar registo maintenance
  await supabase.from("maintenance").insert({
    motorcycleid: selectedMotoForMaintenance.id,
    description: maintenanceReason,
    date: new Date().toISOString(),
    resolved: false,
  });

  // atualizar mota
  await supabase
    .from("vehicles")
    .update({ status: "Manutenção", assigneddriverid: null })
    .eq("id", selectedMotoForMaintenance.id);

  // atualizar UI local
  setDbMotorcycles((prev) =>
    prev.map((m) =>
      m.id === selectedMotoForMaintenance.id
        ? { ...m, status: "Manutenção", assigneddriverid: null }
        : m
    )
  );

  setSelectedMotoForMaintenance(null);
  setMaintenanceReason("");
}

  
return (
  
 <div className="flex flex-col h-full bg-background p-6">

    {/* HEADER */}
    <div className="flex justify-between items-center mb-6">
      <h1 className="text-2xl font-bold">Motas</h1>

      <button
        onClick={() => setShowAddModal(true)}
        className="flex items-center gap-2 bg-black text-white px-4 py-2 rounded-lg"
      >
        <Plus size={18} />
        Adicionar Mota
      </button>
    </div>

    {/* LISTA */}
    <div className="flex-1 space-y-4 overflow-y-auto">
      {loading && <p>A carregar...</p>}

      {!loading && dbMotorcycles.length === 0 && (
        <p>Nenhuma mota encontrada.</p>
      )}

      {dbMotorcycles.map((moto) => {
        const active = getActiveAssignment(moto.id);
        const driverName = active
          ? dbDrivers.find((d) => d.id === active.driverid)?.name
          : null;

        return (
          <div
            key={moto.id}
            className="relative bg-white rounded-lg p-6 shadow-sm border"
          >
            {/*  MENU ICON */}
            <div className="absolute top-3 right-3">
              <button
  onClick={() => setSelectedMotoMenu(moto)}
  className="p-1 rounded hover:bg-gray-100"
>
  ⚙️
</button>


            
              
            </div>

            {/* INFO */}
            <h3 className="text-lg font-semibold">{moto.name}</h3>
            <p>{moto.marca} {moto.modelo}</p>

            <p className="text-sm text-gray-500">
              Matrícula: {moto.matricula}
            </p>

            {driverName && (
              <p className="text-blue-600 text-sm font-medium mt-1">
                Atribuída a {driverName}
              </p>
            )}

            {/* STATUS + ATRIBUIR */}
            <div className="flex justify-between items-center mt-4">
              <span className={`font-semibold ${getStatusColor(moto.status)}`}>
                {getStatusLabel(moto.status)}
              </span>

              {moto.status === "Manutenção" ? (
                <span className="px-3 py-1 bg-gray-200 text-gray-600 rounded text-sm">
                  Indisponível
                </span>
              ) : active ? (
                <button
                  className="px-4 py-2 bg-orange-500 text-white rounded-lg"
                  onClick={() => unassignMotorcycle(moto.id)}
                >
                  Desatribuir
                </button>
              ) : (
                <button
                  className="px-4 py-2 bg-black text-white rounded-lg"
                  onClick={() => openAssignModal(moto)}
                >
                  Atribuir Condutor
                </button>
              )}
            </div>
          </div>
        );
      })}
    </div>

{selectedMotoForEdit && (
  <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
    <div className="bg-white rounded-xl w-full max-w-md p-6 space-y-4">
      <div className="flex justify-between items-center">
        <h3 className="font-semibold text-lg">Editar Mota</h3>
        <X
          className="cursor-pointer"
          onClick={() => setSelectedMotoForEdit(null)}
        />
      </div>

      <input
        className="w-full border p-2 rounded"
        value={selectedMotoForEdit.name || ""}
        onChange={(e) =>
          setSelectedMotoForEdit({
            ...selectedMotoForEdit,
            name: e.target.value,
          })
        }
        placeholder="Nome"
      />

      <input
        className="w-full border p-2 rounded"
        value={selectedMotoForEdit.marca || ""}
        onChange={(e) =>
          setSelectedMotoForEdit({
            ...selectedMotoForEdit,
            marca: e.target.value,
          })
        }
        placeholder="Marca"
      />

      <input
        className="w-full border p-2 rounded"
        value={selectedMotoForEdit.modelo || ""}
        onChange={(e) =>
          setSelectedMotoForEdit({
            ...selectedMotoForEdit,
            modelo: e.target.value,
          })
        }
        placeholder="Modelo"
      />

      <input
        className="w-full border p-2 rounded"
        value={selectedMotoForEdit.matricula || ""}
        onChange={(e) =>
          setSelectedMotoForEdit({
            ...selectedMotoForEdit,
            matricula: e.target.value,
          })
        }
        placeholder="Matrícula"
      />

      <input
        type="number"
        className="w-full border p-2 rounded"
        value={selectedMotoForEdit.battery_capacity ?? ""}
        onChange={(e) =>
          setSelectedMotoForEdit({
            ...selectedMotoForEdit,
            battery_capacity: e.target.value
              ? Number(e.target.value)
              : null,
          })
        }
        placeholder="Capacidade bateria"
      />

      <input
        type="number"
        className="w-full border p-2 rounded"
        value={selectedMotoForEdit.cargo_capacity ?? ""}
        onChange={(e) =>
          setSelectedMotoForEdit({
            ...selectedMotoForEdit,
            cargo_capacity: e.target.value
              ? Number(e.target.value)
              : null,
          })
        }
        placeholder="Capacidade carga"
      />

      <div className="flex gap-3 pt-2">
        <button
          className="flex-1 bg-gray-200 py-2 rounded"
          onClick={() => setSelectedMotoForEdit(null)}
        >
          Cancelar
        </button>

        <button
          className="flex-1 bg-black text-white py-2 rounded"
          onClick={updateMotorcycle}
        >
          Guardar
        </button>
      </div>
    </div>
  </div>
)}

{/* MODAL MENU MOTA */}
{selectedMotoMenu && (
  <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
    <div className="bg-white rounded-xl w-full max-w-sm p-5 space-y-3">

      <h3 className="font-semibold text-lg">
        {selectedMotoMenu.name}
      </h3>
<button
        className="w-full border p-3 rounded hover:bg-orange-50"
        onClick={() => {
          setSelectedMotoForMaintenance(selectedMotoMenu);
          setSelectedMotoMenu(null);
        }}
      >
        Enviar para manutenção
      </button>
      <button
        className="w-full border p-3 rounded hover:bg-gray-100"
        onClick={() => {
          setSelectedMotoForEdit(selectedMotoMenu);
          setSelectedMotoMenu(null);
        }}
      >
        Editar
      </button>

      <button
        className="w-full border p-3 rounded text-red-600 hover:bg-red-50"
        onClick={() => {
          deleteMotorcycle(selectedMotoMenu);
          setSelectedMotoMenu(null);
        }}
      >
        Apagar
      </button>

      

      <button
        className="w-full bg-gray-200 p-3 rounded"
        onClick={() => setSelectedMotoMenu(null)}
      >
        Cancelar
      </button>
    </div>
  </div>
)}

    {/* MODAL MANUTENÇÃO */}
    {selectedMotoForMaintenance && (
      <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
        <div className="bg-white rounded-xl w-full max-w-md">
          
          <div className="flex justify-between p-4 border-b">
            <h3 className="font-semibold">Enviar para Manutenção</h3>
            <X
              className="cursor-pointer"
              onClick={() => setSelectedMotoForMaintenance(null)}
            />
          </div>

          <div className="p-6">
            <div className="mb-6">
              <div className="flex items-center gap-2 mb-2">
                <AlertCircle className="text-orange-500" size={20} />
                <span className="text-sm text-gray-600">
                  Está a enviar para manutenção:
                </span>
              </div>

              <div className="bg-gray-50 p-3 rounded-lg">
                <h4 className="font-medium">
                  {selectedMotoForMaintenance.name}
                </h4>
                <p className="text-sm text-gray-500">
                  Matrícula: {selectedMotoForMaintenance.matricula}
                </p>
              </div>
            </div>

            <textarea
              placeholder="Descreva o motivo..."
              className="w-full border rounded-lg p-3 h-28"
              value={maintenanceReason}
              onChange={(e) => setMaintenanceReason(e.target.value)}
            />

            <div className="flex gap-3 mt-6">
              <button
                className="flex-1 bg-gray-200 py-2 rounded-lg"
                onClick={() => setSelectedMotoForMaintenance(null)}
              >
                Cancelar
              </button>

              <button
                className="flex-1 bg-[#333] text-white py-2 rounded-lg disabled:opacity-50"
                disabled={!maintenanceReason}
                onClick={confirmSendToMaintenance}
              >
                Confirmar
              </button>
            </div>
          </div>
        </div>
      </div>
    )}
{/* MODAL ADICIONAR MOTA */}
{showAddModal && (
  <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
    <div className="bg-white rounded-xl w-full max-w-md p-6 space-y-4">

      <div className="flex justify-between items-center">
        <h3 className="text-lg font-semibold">Adicionar Mota</h3>

        <X
          className="cursor-pointer"
          onClick={() => setShowAddModal(false)}
        />
      </div>

      <input
        className="w-full border p-2 rounded"
        placeholder="Nome"
        value={newVehicle.name}
        onChange={(e) =>
          setNewVehicle({ ...newVehicle, name: e.target.value })
        }
      />

      <input
        className="w-full border p-2 rounded"
        placeholder="Marca"
        value={newVehicle.marca}
        onChange={(e) =>
          setNewVehicle({ ...newVehicle, marca: e.target.value })
        }
      />

      <input
        className="w-full border p-2 rounded"
        placeholder="Modelo"
        value={newVehicle.modelo}
        onChange={(e) =>
          setNewVehicle({ ...newVehicle, modelo: e.target.value })
        }
      />

      <input
        className="w-full border p-2 rounded"
        placeholder="Matrícula"
        value={newVehicle.matricula}
        onChange={(e) =>
          setNewVehicle({ ...newVehicle, matricula: e.target.value })
        }
      />

      <input
        type="number"
        className="w-full border p-2 rounded"
        placeholder="Capacidade bateria"
        value={newVehicle.battery_capacity ?? ""}
        onChange={(e) =>
          setNewVehicle({
            ...newVehicle,
            battery_capacity: e.target.value
              ? Number(e.target.value)
              : null,
          })
        }
      />

      <input
        type="number"
        className="w-full border p-2 rounded"
        placeholder="Capacidade carga"
        value={newVehicle.cargo_capacity ?? ""}
        onChange={(e) =>
          setNewVehicle({
            ...newVehicle,
            cargo_capacity: e.target.value
              ? Number(e.target.value)
              : null,
          })
        }
      />

      <div className="flex gap-3 pt-2">
        <button
          className="flex-1 bg-gray-200 py-2 rounded"
          onClick={() => setShowAddModal(false)}
        >
          Cancelar
        </button>

        <button
          className="flex-1 bg-black text-white py-2 rounded"
          onClick={addMotorcycle}
        >
          Guardar
        </button>
      </div>
    </div>
  </div>
)}
    {/* MODAL ATRIBUIR CONDUTOR */}
{showAssignModal && selectedMotoForAssignment && (
  <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
    <div className="bg-white rounded-lg w-full max-w-md p-6">
      <h3 className="font-semibold mb-4">
        Atribuir "{selectedMotoForAssignment.name}"
      </h3>

      {availableDrivers.length === 0 ? (
        <p className="text-center text-gray-600">
          Nenhum condutor disponível
        </p>
      ) : (
        <div className="space-y-3">
          {availableDrivers.map((driver) => (
            <button
              key={driver.id}
              className="w-full p-3 border rounded-lg hover:bg-gray-100 text-left"
              onClick={() =>
                assignMotorcycle(selectedMotoForAssignment, driver)
              }
            >
              <div className="font-semibold">{driver.name}</div>
              <div className="text-sm text-gray-500">
                {driver.license}
              </div>
            </button>
          ))}
        </div>
      )}

      <button
        className="w-full mt-6 bg-gray-200 py-2 rounded"
        onClick={() => {
          setShowAssignModal(false);
          setSelectedMotoForAssignment(null);
        }}
      >
        Cancelar
      </button>
    </div>
  </div>
)}

  </div>
);

  
};

export default MotorcyclesPage;
