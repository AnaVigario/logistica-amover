import React, { useState, useEffect, useMemo } from 'react';

import { MapPin, Clock, AlertCircle, Plus, X, Filter, Calendar, ChevronUp, ChevronDown } from 'lucide-react';
import { supabase } from '../supabaseClient';
type RecurrenceType = "Pontual" | "Diária" | "Semanal" | "Mensal";
function formatDateYYYYMMDD(date: Date) {
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, "0");
  const d = String(date.getDate()).padStart(2, "0");
  return `${y}-${m}-${d}`;
}
interface TasksPageProps {
  onOpenAssignment?: () => void;
}



interface Task {
  id: number;
  title: string;
  date: string;
  priority: 'ALTA' | 'MÉDIA' | 'BAIXA';
  time: string;
  clientid: number | null;
  company_id: number | null;
  serviceid: number | null;
  recurrence: RecurrenceType;
  status: 'POR CONCLUIR' | 'CONCLUÍDO';
  notes: string | null;
  instructions: string| null;
start_time?: string;
end_time?: string;
  street?: string | null;
  door_number?: string | null;
  floor?: string | null;
  postal_code?: string | null;
  city?: string | null;
}



interface Client {
  id: number;
  name: string;
  nif:string;
  street: string;
  door_number: string;
  floor?: string | null;
  postal_code: string;
  city: string;
}


interface Service {
  id: number;
  description: string;
  company_id: number;
}

const TasksPage: React.FC<TasksPageProps> = ({ onOpenAssignment }) => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [clients, setClients] = useState<Client[]>([]);
  const [services, setServices] = useState<Service[]>([]);
const [clientSearch, setClientSearch] = useState("");
const [showClientDropdown, setShowClientDropdown] = useState(false);
const [showNewClientModal, setShowNewClientModal] = useState(false);
const [searchTaskName, setSearchTaskName] = useState("");
const [showAddressConfirm, setShowAddressConfirm] = useState(false);
const [pendingClient, setPendingClient] = useState<Client | null>(null);

  const [showNewTaskForm, setShowNewTaskForm] = useState(false);
  const [showFilters, setShowFilters] = useState(false);
  const [showPastTasks, setShowPastTasks] = useState(false);
const [companies, setCompanies] = useState<any[]>([]);
const [selectedCompany, setSelectedCompany] = useState<string>("");

  const [selectedTask, setSelectedTask] = useState<Task | null>(null);
  const [editingTaskId, setEditingTaskId] = useState<number | null>(null); 
  const [selectedPriority, setSelectedPriority] = useState<'ALTA' | 'MÉDIA' | 'BAIXA' | 'ALL'>('ALL');
  const [selectedDate, setSelectedDate] = useState('');
  const [collapsedDates, setCollapsedDates] = useState<Set<string>>(new Set());


const [newTask, setNewTask] = useState<{
  title: string;
  date: string;
  priority: "ALTA" | "MÉDIA" | "BAIXA";
  time: string;
  clientid: string;
  serviceid: string;
  company_id:string;
  recurrence: RecurrenceType;
  notes: string;
  instructions: string;
start_time?: string;
end_time?: string;
  street: string;
  door_number: string;
  floor: string;
  postal_code: string;
  city: string;
}>({
  title: "",
  date: "",
  priority: "MÉDIA",
  time: "",
  clientid: "",
  serviceid: "",
  company_id: "",
  recurrence: "Pontual",
  notes: "",
  instructions: "",
start_time: "",
end_time: "",
  street: "",
  door_number: "",
  floor: "",
  postal_code: "",
  city: "",
});
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
const filteredServices = services.filter(
  s => String(s.company_id) === selectedCompany
);

const filteredClients = clients.filter(c => {
  const search = clientSearch.toLowerCase();

  return (
    c.name.toLowerCase().includes(search) ||
    c.nif.includes(search)
  );
});


  const priorityWeight: Record<string, number> = {
  ALTA: 3,
  MÉDIA: 2,
  BAIXA: 1,
};

const visibleTasks = useMemo(() => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  if (searchTaskName) {
    return tasks.filter((t) =>
      t.title?.toLowerCase().includes(searchTaskName.toLowerCase())
    );
  }

  const future = tasks.filter(
    (t) => new Date(t.date) >= today
  );

  const past = tasks.filter(
    (t) => new Date(t.date) < today
  );

  return showPastTasks ? past : future;
}, [tasks, showPastTasks, searchTaskName]);

{/*function generateRecurringDates(
  startDate: string,
  recurrence: 'Pontual' | 'Diária' | 'Semanal' | 'Mensal'
): string[] {
  const dates: string[] = [];

  const start = new Date(startDate);
  start.setHours(0, 0, 0, 0);

  const end = new Date(start);
  end.setFullYear(end.getFullYear() + 1); // ⏱ 1 ano

  let current = new Date(start);

  while (current <= end) {
    dates.push(current.toISOString().split("T")[0]);

    if (recurrence === 'Diária') {
      current.setDate(current.getDate() + 1);
    } else if (recurrence === 'Semanal') {
      current.setDate(current.getDate() + 7);
    } else if (recurrence === 'Mensal') {
      current.setMonth(current.getMonth() + 1);
    } else {
      break; // Pontual
    }
  }

  return dates;
} */}


function resetForm() {
  setNewTask({
    title:'',
    date: '',
    priority: 'MÉDIA',
    time: '',
    clientid: '',
    serviceid: '',
    company_id:'',
    recurrence: 'Pontual',
    notes: "",
    instructions: "",

    street: "",
    door_number: "",
    floor: "",
    postal_code: "",
    city: "",
  });
}

function resetTaskForm() {
  resetForm();              // limpa newTask
  setClientSearch("");      // limpa texto cliente
  setShowClientDropdown(false);
  setPendingClient(null);
  setShowAddressConfirm(false);
  setSelectedCompany("");
}

  useEffect(() => {
    loadTasks();
    loadClients();
    loadServices();
    loadCompanies();

  }, []);

  async function loadTasks() {
    const { data, error } = await supabase
      .from('tasks')
      .select('*')
      .order('date', { ascending: true });

    if (!error && data) {
      setTasks(data as Task[]);
    }
  }
async function loadCompanies() {
  const { data } = await supabase
    .from("companies")
    .select("*")
    .order("name");

  setCompanies(data || []);
}

  async function loadClients() {
    const { data, error } = await supabase
      .from('clients')
      .select(`
  id,
  name,
  nif,
  street,
  door_number,
  floor,
  postal_code,
  city
`)

      .order('name', { ascending: true });

    if (!error && data) setClients(data as Client[]);
  }

async function loadServices() {
  const { data, error } = await supabase
    .from("services")
    .select("id, description, company_id")   
    .order("description");

  if (!error && data) setServices(data);
}

async function handleAddClientFromTask() {
  if (!newClient.name || !newClient.nif) {
    alert("O nome e o NIF são obrigatórios!");
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
    .select()
    .single();

  if (error || !data) {
    alert("Erro ao adicionar cliente.");
    return;
  }

  setClients(prev => [...prev, data]);

  setNewTask(prev => ({
    ...prev,
    clientid: String(data.id)
  }));

  setClientSearch(`${data.name} — ${data.nif}`);

  setNewClient({
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

  setShowNewClientModal(false);
}


async function handleUpdateTask() {
  if (!editingTaskId) return;

  const { error } = await supabase
    .from("tasks")
    .update({
      date: newTask.date,
      priority: newTask.priority,
      time: newTask.time,
      clientid: newTask.clientid ? Number(newTask.clientid) : null,
      serviceid: newTask.serviceid ? Number(newTask.serviceid) : null,
      recurrence: newTask.recurrence,
      notes: newTask.notes,
      start_time: newTask.start_time,
end_time: newTask.end_time,
      instructions: newTask.instructions,
      street: newTask.street || null,
      door_number: newTask.door_number || null,
      floor: newTask.floor || null,
      postal_code: newTask.postal_code || null,
      city: newTask.city || null,
    })
    .eq("id", editingTaskId);

  if (error) {
    alert("Erro ao editar tarefa");
    return;
  }

  setShowNewTaskForm(false);
  setEditingTaskId(null);
  resetTaskForm();
  loadTasks();
}


async function handleAddTask() {
  if (!newTask.date) {
    alert("Seleciona uma data");
    return;
  }

  const baseTask = {
    title: "",
    priority: newTask.priority,
    time: newTask.time,
    clientid: newTask.clientid ? Number(newTask.clientid) : null,
    serviceid: newTask.serviceid ? Number(newTask.serviceid) : null,
    status: "POR CONCLUIR" as const,
    recurrence: newTask.recurrence,
      start_time: newTask.start_time,
  end_time: newTask.end_time,
    recurrence_type:
      newTask.recurrence === "Pontual" ? null : newTask.recurrence,
    notes: newTask.notes,
    instructions: newTask.instructions,
    street: newTask.street || null,
    door_number: newTask.door_number || null,
    floor: newTask.floor || null,
    postal_code: newTask.postal_code || null,
    city: newTask.city || null,
  };

  const { data, error } = await supabase
    .from("tasks")
    .insert({
      ...baseTask,
      date: newTask.date,
    })
    .select()
    .single();

  if (error || !data) {
    alert("Erro ao criar tarefa");
    return;
  }

  await supabase
    .from("tasks")
    .update({ title: `Tarefa-${data.id}` })
    .eq("id", data.id);

  setShowNewTaskForm(false);
  resetTaskForm();
  loadTasks();
}


 const filteredTasks = visibleTasks.filter((task) => {
  const matchPrio =
    selectedPriority === "ALL" || task.priority === selectedPriority;

  const matchDate =
    !selectedDate || task.date === selectedDate;

  return matchPrio && matchDate;
});
  const grouped = filteredTasks.reduce((groups, t) => {
    const d = new Date(t.date).toLocaleDateString('pt-PT');
    if (!groups[d]) groups[d] = [];
    groups[d].push(t);
    return groups;
  }, {} as Record<string, Task[]>);

  const sortedDates = Object.keys(grouped).sort(
    (a, b) =>
      new Date(a.split('/').reverse().join('-')).getTime() -
      new Date(b.split('/').reverse().join('-')).getTime()
  );

  const getPriorityColor = (p: string) => {
    switch (p) {
      case 'ALTA':
        return 'bg-red-500';
      case 'MÉDIA':
        return 'bg-orange-500';
      default:
        return 'bg-green-600';
    }
  };

  function toggleDate(date: string) {
    const newSet = new Set(collapsedDates);
    newSet.has(date) ? newSet.delete(date) : newSet.add(date);
    setCollapsedDates(newSet);
  }

  return (
    
<div className="bg-gray-100 p-6 max-h-[75vh] overflow-y-auto">

    {/* TOPO BOTÕES */}
    <div className="flex justify-between mb-4">

      <div className="flex gap-2">
        <button
          onClick={() => setShowNewTaskForm(true)}
          className="bg-[#333] text-white px-4 py-2 rounded flex items-center gap-2"
        >
          Adicionar Tarefa
        </button>

        <button
          onClick={() => setShowFilters(true)}
          className="bg-[#333] text-white px-4 py-2 rounded flex items-center gap-2"
        >
          Filtros
        </button>
      </div>

      
   <button
  onClick={() => {
    
    onOpenAssignment?.();
  }}
  className="bg-[#333] text-white px-4 py-2 rounded"
>
  Atribuição de tarefas
</button>




    </div>


 
      {/* LISTA */}
      <div className="flex-1 overflow-y-auto">
  {sortedDates.map((d) => (
    <div key={d} className="mb-6">
      <button
        onClick={() => toggleDate(d)}
        className="w-full bg-white px-4 py-2 rounded font-semibold flex justify-between items-center"
      >
        {d}
        {collapsedDates.has(d) ? <ChevronDown /> : <ChevronUp />}
      </button>

      {!collapsedDates.has(d) &&
        grouped[d].map((task) => {
          const client = clients.find(c => c.id === task.clientid);

          return (
            <div
              key={task.id}
              className="bg-white p-4 mt-2 rounded shadow cursor-pointer hover:bg-gray-50"
              onClick={() => setSelectedTask(task)}
            >
              {/* TÍTULO + PRIORIDADE */}
              <div className="flex justify-between items-center">
                <h3 className="font-medium">{task.title}</h3>
                <span
                  className={`px-3 py-1 text-white text-sm rounded-full ${getPriorityColor(
                    task.priority
                  )}`}
                >
                  {task.priority}
                </span>
              </div>

              {/* MORADA RESUMIDA */}
             <p className="text-sm text-gray-500 mt-1">
  {task.street
    ? `${task.street} ${task.door_number || ""}${task.floor ? `, ${task.floor}` : ""} · ${task.city}`
    : client
      ? `${client.street} ${client.door_number}${client.floor ? `, ${client.floor}` : ""} · ${client.city}`
      : ""}
</p>


             {/* CLIENTE */}
{client && (
  <p className="text-xs text-gray-500 mt-1">
    Cliente: <span className="font-medium">{client.name}</span>
  </p>
)}

            </div>
          );
        })}
    </div>
  ))}

  {sortedDates.length === 0 && (
    <div className="text-center p-6 bg-white rounded">
      Nenhuma tarefa encontrada.
    </div>
  )}
</div>


{/* MODAL NOVA TAREFA */}
{showNewTaskForm && (
  <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">

    <div className="bg-white rounded-xl w-full max-w-lg h-[85vh] flex flex-col shadow-xl">

      {/* HEADER */}
      <div className="flex justify-between items-center p-4 border-b">
        <h3 className="font-semibold text-lg">
          {editingTaskId ? "Editar Tarefa" : "Nova Tarefa"}
        </h3>

        <X
          className="cursor-pointer"
          onClick={() => {
            setShowNewTaskForm(false);
            setEditingTaskId(null);
            resetTaskForm();
          
          }}
        />
      </div>

      
      <div className="flex-1 overflow-y-auto p-4 space-y-3">

      <div>

  <div className="flex justify-between items-center mb-1">
    <label className="text-sm text-gray-600">
      Cliente
    </label>

    <button
      type="button"
      className="text-sm text-blue-600 hover:underline"
      onClick={() => setShowNewClientModal(true)}
    >
      Novo cliente? Registar
    </button>
  </div>

  {/* AUTOCOMPLETE */}
  <div className="relative">
    <input
      className="w-full border p-2 rounded"
      placeholder="Pesquisar por nome ou NIF..."
      value={clientSearch}
      onChange={(e) => {
        setClientSearch(e.target.value);
        setShowClientDropdown(true);
        setNewTask({ ...newTask, clientid: "" });
      }}
      onFocus={() => setShowClientDropdown(true)}
    />

    {showClientDropdown && clientSearch && (
      <div className="absolute z-50 bg-white border w-full max-h-48 overflow-y-auto rounded shadow">
        {filteredClients.map((c) => (
          <div
            key={c.id}
            className="p-2 hover:bg-gray-100 cursor-pointer"
            onClick={() => {
  setPendingClient(c);
  setShowAddressConfirm(true);

  setNewTask(prev => ({
    ...prev,
    clientid: String(c.id)
  }));

  setClientSearch(`${c.name} — ${c.nif}`);
  setShowClientDropdown(false);
}}

          >
            <div className="font-medium">
              {c.name} — {c.nif}
            </div>
            <div className="text-xs text-gray-500">
              {c.street} {c.door_number} · {c.city}
            </div>
          </div>
        ))}
      </div>
    )}
  </div>
</div>



        {/* TITULO 
        <input
          className="w-full border p-2 rounded"
          placeholder="Título da tarefa *"
          value={newTask.title}
          onChange={(e) =>
            setNewTask({ ...newTask, title: e.target.value })
          }
        />
        */}

        {/* DATA */}
       <div>
  <label className="text-sm text-gray-600 mb-1 block">
    Data
  </label>

  <input
    type="date"
    className="w-full border p-2 rounded"
    value={newTask.date}
    onChange={(e) =>
      setNewTask({ ...newTask, date: e.target.value })
    }
  />
</div>
<div className="grid grid-cols-2 gap-3">

  <div>
    <label className="text-sm text-gray-600 mb-1 block">
      Hora Início
    </label>

    <input
      type="time"
      className="w-full border p-2 rounded"
      value={newTask.start_time}
      onChange={(e) =>
        setNewTask({
          ...newTask,
          start_time: e.target.value
        })
      }
    />
  </div>

  <div>
    <label className="text-sm text-gray-600 mb-1 block">
      Hora Fim
    </label>

    <input
      type="time"
      className="w-full border p-2 rounded"
      value={newTask.end_time}
      onChange={(e) =>
        setNewTask({
          ...newTask,
          end_time: e.target.value
        })
      }
    />
  </div>

</div>

        {/* MORADA */}
        <div>
  <label className="text-sm text-gray-600 mb-2 block">
    Morada
  </label>

  <div className="bg-gray-50 border rounded-lg p-3 space-y-2">

    <input
      className="w-full border p-2 rounded"
      placeholder="Rua"
      value={newTask.street}
      onChange={(e) =>
        setNewTask({ ...newTask, street: e.target.value })
      }
    />

    <div className="grid grid-cols-2 gap-2">
      <input
        className="w-full border p-2 rounded"
        placeholder="Número"
        value={newTask.door_number}
        onChange={(e) =>
          setNewTask({ ...newTask, door_number: e.target.value })
        }
      />

      <input
        className="w-full border p-2 rounded"
        placeholder="Andar"
        value={newTask.floor}
        onChange={(e) =>
          setNewTask({ ...newTask, floor: e.target.value })
        }
      />
    </div>

    <div className="grid grid-cols-2 gap-2">
      <input
        className="w-full border p-2 rounded"
        placeholder="Código Postal"
        value={newTask.postal_code}
        onChange={(e) =>
          setNewTask({ ...newTask, postal_code: e.target.value })
        }
      />

      <input
        className="w-full border p-2 rounded"
        placeholder="Cidade"
        value={newTask.city}
        onChange={(e) =>
          setNewTask({ ...newTask, city: e.target.value })
        }
      />
    </div>

  </div>
</div>


        {/* EMPRESA */}
        <select
          className="w-full border p-2 rounded"
          value={selectedCompany}
          onChange={(e) => {
            setSelectedCompany(e.target.value);
            setNewTask({ ...newTask, serviceid: "" });
          }}
        >
          <option value="">Selecionar empresa</option>
          {companies.map((c) => (
            <option key={c.id} value={c.id}>
              {c.name}
            </option>
          ))}
        </select>

        {/* SERVIÇOS */}
        <select
          className="w-full border p-2 rounded"
          value={newTask.serviceid}
          onChange={(e) =>
            setNewTask({ ...newTask, serviceid: e.target.value })
          }
          disabled={!selectedCompany}
        >
          <option value="">Selecionar serviço</option>

          {filteredServices.map((s) => (
            <option key={s.id} value={s.id}>
              {s.description}
            </option>
          ))}
        </select>

        {/* NOTAS */}
        <textarea
          className="w-full border p-2 rounded"
          placeholder="Notas"
          value={newTask.notes}
          onChange={(e) =>
            setNewTask({ ...newTask, notes: e.target.value })
          }
        />

        {/* INSTRUÇÕES */}
        <textarea
          className="w-full border p-2 rounded"
          placeholder="Instruções"
          value={newTask.instructions}
          onChange={(e) =>
            setNewTask({ ...newTask, instructions: e.target.value })
          }
        />

        {/* RECORRÊNCIA 
        <select
          className="w-full border p-2 rounded"
          value={newTask.recurrence}
          onChange={(e) =>
            setNewTask({
              ...newTask,
              recurrence: e.target.value as RecurrenceType,
            })
          }
        >
          <option value="Pontual">Pontual</option>
          <option value="Diária">Diária</option>
          <option value="Semanal">Semanal</option>
          <option value="Mensal">Mensal</option>
        </select>
*/}
        {/* PRIORIDADE */}
        <div>
  <label className="text-sm text-gray-600 mb-1 block">
    Prioridade da Tarefa
  </label>
 <select
          className="w-full border p-2 rounded"
          value={newTask.priority}
          onChange={(e) =>
            setNewTask({
              ...newTask,
              priority: e.target.value as "ALTA" | "MÉDIA" | "BAIXA",
            })
          }
        >
          <option value="ALTA">Alta</option>
          <option value="MÉDIA">Média</option>
          <option value="BAIXA">Baixa</option>
        </select>
  
</div>
        
        
       
      </div>

      {/* FOOTER FIXO */}
      <div className="p-4 border-t">
        <button
          className="w-full bg-[#333] text-white py-2 rounded"
          onClick={editingTaskId ? handleUpdateTask : handleAddTask}
        >
          {editingTaskId ? "Guardar Alterações" : "Adicionar"}
        </button>
      </div>

    </div>
  </div>
)}

 

        
{showFilters && (
  <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
    <div className="bg-white rounded-lg p-6 w-full max-w-sm space-y-4">

      {/* HEADER */}
      <div className="flex justify-between items-center">
        <h3 className="font-semibold text-lg">Filtros</h3>
        <button onClick={() => setShowFilters(false)}>
          <X size={20} />
        </button>
      </div>

      {/* FILTRO POR DATA */}

      <div>
  <label className="block text-sm text-gray-600 mb-1">
    Nome da tarefa
  </label>
  <input
    type="text"
    placeholder="Pesquisar pelo nome..."
    value={searchTaskName}
    onChange={(e) => setSearchTaskName(e.target.value)}
    className="w-full border rounded p-2"
  />
</div>
      <div>
        <label className="block text-sm text-gray-600 mb-1">
          Data
        </label>
        <input
          type="date"
          value={selectedDate}
          onChange={(e) => setSelectedDate(e.target.value)}
          className="w-full border rounded p-2"
        />
      </div>

      <label className="flex items-center gap-2 text-sm">
        <input
          type="checkbox"
          checked={showPastTasks}
          onChange={(e) => setShowPastTasks(e.target.checked)}
        />
        Mostrar tarefas passadas
      </label>

      {/* AÇÕES */}
      <div className="flex gap-2 pt-4">
        <button
          onClick={() => {
            setSelectedDate('');
            setShowPastTasks(false);
            setSearchTaskName("");

          }}
          className="flex-1 border rounded p-2"
        >
          Limpar
        </button>

        <button
          onClick={() => setShowFilters(false)}
          className="flex-1 bg-black text-white rounded p-2"
        >
          Aplicar
        </button>
      </div>
    </div>
  </div>
)}

      {/* MODAL DETALHES NO FUTURO */}
{selectedTask && (
  <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
    <div className="bg-white w-full max-w-md rounded-lg p-4 relative">

      {/* BOTÃO FECHAR */}
      <button
        className="absolute top-3 right-3 text-gray-500 hover:text-black"
        onClick={() => setSelectedTask(null)}
      >
        <X size={20} />
      </button>

      {/* CONTEÚDO */}
      <div className="bg-white p-4 mt-2 rounded shadow">
        <h3 className="font-semibold text-lg">
          {selectedTask.title}
        </h3>

        {selectedTask.clientid && (() => {
          const client = clients.find(c => c.id === selectedTask.clientid);
          if (!client) return null;

          return (
            <p className="text-sm text-gray-500 mt-1">
              {client.street} {client.door_number} · {client.city}
            </p>
          );
        })()}
      </div>

      {/* BOTÕES */}
      <div className="mt-5 space-y-2">
        <button
          className="w-full bg-yellow-500 text-white py-2 rounded"
          onClick={() => {
           setNewTask({
  title: selectedTask.title,
  date: selectedTask.date,
  priority: selectedTask.priority,
  time: selectedTask.time,
  clientid: selectedTask.clientid
    ? String(selectedTask.clientid)
    : "",
     company_id: selectedTask.company_id
    ? String(selectedTask.company_id)
    : "",
  serviceid: selectedTask.serviceid
    ? String(selectedTask.serviceid)
    : "",
  recurrence: selectedTask.recurrence,
  notes: selectedTask.notes
    ? String(selectedTask.notes)
    : "",
  instructions: selectedTask.instructions
    ? String(selectedTask.instructions)
    : "",
  street: selectedTask.street || "",
  door_number: selectedTask.door_number || "",
  floor: selectedTask.floor || "",
  postal_code: selectedTask.postal_code || "",
  city: selectedTask.city || "",
});


            setEditingTaskId(selectedTask.id);
            setShowNewTaskForm(true);
            setSelectedTask(null);
          }}
        >
          Editar
        </button>

        <button
          className="w-full bg-red-600 text-white py-2 rounded"
          onClick={async () => {
            if (!confirm("Eliminar esta tarefa?")) return;

            const { error } = await supabase
              .from("tasks")
              .delete()
              .eq("id", selectedTask.id);

            if (!error) {
              setSelectedTask(null);
              loadTasks();
            }
          }}
        >
          Eliminar
        </button>
      </div>
    </div>
  </div>
)}

{showNewClientModal && (
  <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
    <div className="bg-white rounded-lg w-full max-w-md p-6">

      <div className="flex justify-between items-center mb-4">
        <h3 className="font-semibold text-lg">Adicionar Cliente</h3>
        <button onClick={() => setShowNewClientModal(false)}>
          <X />
        </button>
      </div>

      <ClientForm state={newClient} setState={setNewClient} />

      <button
        className="w-full bg-black text-white py-2 rounded mt-4"
        onClick={handleAddClientFromTask}
      >
        Guardar Cliente
      </button>

    </div>
  </div>
)}
{showAddressConfirm && pendingClient && (
  <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
    <div className="bg-white rounded-lg p-6 w-full max-w-sm">

      <h3 className="font-semibold text-lg mb-3">
        Usar morada do cliente?
      </h3>

      <p className="text-sm text-gray-600 mb-5">
        Pretende preencher automaticamente a morada com os dados do cliente selecionado?
      </p>

      <div className="flex gap-3">
        <button
          className="flex-1 bg-gray-200 rounded py-2"
          onClick={() => {
            setShowAddressConfirm(false);
            setPendingClient(null);
          }}
        >
          Inserir manualmente
        </button>

        <button
          className="flex-1 bg-black text-white rounded py-2"
          onClick={() => {
            setNewTask(prev => ({
              ...prev,
              street: pendingClient.street || "",
              door_number: pendingClient.door_number || "",
              floor: pendingClient.floor || "",
              postal_code: pendingClient.postal_code || "",
              city: pendingClient.city || "",
            }));

            setShowAddressConfirm(false);
            setPendingClient(null);
          }}
        >
          Usar morada
        </button>
      </div>

    </div>
  </div>
)}

    </div>
  );
};

export default TasksPage;
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

 