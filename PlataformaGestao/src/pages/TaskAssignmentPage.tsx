import {
  MapPin, Clock, ChevronLeft, ChevronRight,
  Filter, X, Trash2
} from "lucide-react";
import { supabase } from "../supabaseClient";
import React, { useEffect, useState, useMemo } from "react";


interface Task {
  id: number;
  title: string;
  date: string;
  priority: string;
  time: string;
  serviceid: number | null;
  motorcycleid: number | null;
  clientid: number | null;

  street?: string | null;
  door_number?: string | null;
  floor?: string | null;
  postal_code?: string | null;
  city?: string | null;
}


interface Vehicle {
  id: number;
  name: string;
  status: string;
  assigneddriverid: number | null;
  driver: {
    id: number;
    status: string;
    company_id: number;
    company: {
      id: number;
      name: string;
      services: {
        id: number;
      }[];
    };
  } | null;
}


interface Driver {
  id: number;
  name: string;
}


interface Props {
  onOpenRoute: (vehicleId: number, date: Date) => void;
}



const TaskAssignmentPage: React.FC<Props> = ({ onOpenRoute }) => {

  const [selectedDate, setSelectedDate] = useState(new Date());
  const [tasks, setTasks] = useState<Task[]>([]);
  const [tasksBackup, setTasksBackup] = useState<Task[]>([]);
  const [motorcycles, setMotorcycles] = useState<Vehicle[]>([]);
  const [drivers, setDrivers] = useState<Driver[]>([]);
  const [selectedTask, setSelectedTask] = useState<Task | null>(null);
  const [showAssignModal, setShowAssignModal] = useState(false);





  async function loadTasks() {
    const { data } = await supabase.from("tasks").select("*");
    setTasks(data || []);
    setTasksBackup(data || []);
  }

async function loadVehicles() {
  const { data, error } = await supabase
  .from("vehicles")
  .select(`
    id,
    name,
    status,
    assigneddriverid,
    driver:drivers!fk_vehicle_driver (
      id,
      status,
      company_id,
      company:companies (
        id,
        name,
        services (
          id
        )
      )
    )
  `)
  .eq("status", "Em uso")
  .returns<Vehicle[]>();

  if (error) {
    console.error("Erro loadVehicles:", error);
    return;
  }

  setMotorcycles(data ?? []);
}

  async function loadDrivers() {
    const { data } = await supabase
      .from("drivers")
      .select("id,name");

    setDrivers(data || []);
  }

  useEffect(() => {
    loadTasks();
    loadVehicles();
    loadDrivers();

  }, []);


  // ------------------- HELPERS -------------------

// ------------------- HELPERS -------------------
const formatDate = (d: Date) => {
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const filteredTasks = tasks.filter(t => t.date === formatDate(selectedDate));
 


  const getMotorcycleTasks = (motoId: number) =>
    tasks.filter(t =>
      t.date === formatDate(selectedDate) &&
      t.motorcycleid === motoId
    );

  const getDriverName = (motoId: number) => {
    const moto = motorcycles.find(m => m.id === motoId);
    if (!moto?.assigneddriverid) return null;
    return drivers.find(d => d.id === moto.assigneddriverid)?.name || null;
  };

  const getPriorityColor = (p: string) =>
    p === "ALTA" ? "bg-red-500" :
    p === "MÉDIA" ? "bg-orange-500" :
    "bg-green-500";

 const availableVehicles = useMemo(() => {
  if (!selectedTask?.serviceid) return [];

  return motorcycles.filter((vehicle) => {
    const driver = vehicle.driver;
    if (!driver) return false;

    if (driver.status !== "active") return false;

    const company = driver.company;
    if (!company) return false;

    return company.services.some(
      (service) => service.id === selectedTask.serviceid
    );
  });
}, [motorcycles, selectedTask]);




  // ------------------- ASSIGN TASK -------------------
  async function assignTask(motoId: number) {
    if (!selectedTask) return;

    const { error } = await supabase
      .from("tasks")
      .update({ motorcycleid: motoId })
      .eq("id", selectedTask.id);

    if (error) {
      console.error(error);
      alert("Erro ao atribuir tarefa");
      return;
    }

    setShowAssignModal(false);
    setSelectedTask(null);
    loadTasks(); 
  }


  async function removeTask(motoId: number , taskId: number) {
    const { error } = await supabase
      .from("tasks")
      .update({ motorcycleid: null })
      .eq("id", taskId);

    if (error) {
      console.error(error);
      return;
    }

    loadTasks();
  }

function sortTasksSmart(tasks: Task[]) {
  return [...tasks].sort((a, b) => {
    const aAssigned = a.motorcycleid !== null;
    const bAssigned = b.motorcycleid !== null;

    if (aAssigned !== bAssigned) {
      return aAssigned ? 1 : -1;
    }

    if (!a.time && !b.time) return 0;
    if (!a.time) return 1;
    if (!b.time) return -1;

    return a.time.localeCompare(b.time);
  });
}


  // ------------------- UI -------------------
  const daysInMonth = new Date(selectedDate.getFullYear(), selectedDate.getMonth() + 1, 0).getDate();
  const firstDayOfMonth = new Date(selectedDate.getFullYear(), selectedDate.getMonth(), 1).getDay();


  return (
    <div className="flex h-full">

      {/* LEFT - Tarefas */}
      <div className="w-1/3 bg-white dark:bg-gray-800 border-r p-4 flex flex-col">
        <h2 className="text-xl font-semibold mb-4">
          Tarefas — {selectedDate.toLocaleDateString("pt-PT")}
        </h2>

        <div className="flex-1 overflow-y-auto space-y-3">
          {sortTasksSmart(filteredTasks).map(task => {

            const assigned = task.motorcycleid !== null;
            return (
              <div key={task.id}
                className={`border rounded-lg p-3 cursor-pointer hover:shadow ${assigned ? "bg-green-50 border-green-300" : "border-gray-200 dark:border-gray-700"}`}
                onClick={() => setSelectedTask(task)}
              >
                <div className="flex justify-between">
                  <span>{task.title}</span>
                  <span className={`text-white text-xs px-2 py-1 rounded-full ${getPriorityColor(task.priority)}`}>
                    {task.priority}
                  </span>
                </div>
                {task.street && (
  <div className="text-sm text-gray-500 dark:text-gray-400 mt-1 space-y-1">


  {/* Morada */}
  {task.street && (
    <div className="flex items-center gap-1">
      <MapPin size={14} />
      {task.street} {task.door_number} · {task.city}
    </div>
  )}

</div>

)}   
              </div>
            )
          })}
        </div>
      </div>


      {/* MID - Motos Atribuídas */}
      <div className="w-1/3 bg-white dark:bg-gray-800 border-r p-4 overflow-y-auto">
<h2 className="text-xl font-semibold mb-4">
  Tarefas Atribuídas
</h2>

        {motorcycles.map(moto => {
          const list = getMotorcycleTasks(moto.id);
          const driverName = getDriverName(moto.id);

          return (
            <div key={moto.id} className="mb-6">
              <div className="flex justify-between items-start">
                <div>
                  <h3 className="font-medium">{moto.name}</h3>
                  <p className="text-sm text-blue-600">
                    {driverName ? `Condutor: ${driverName}` : "Sem condutor"}
                  </p>
                </div>

    {/*  <button
  onClick={() => onOpenRoute(moto.id, selectedDate)}
  className="mt-2 px-3 py-1 text-sm bg-black text-white rounded hover:bg-gray-800"
>
  Calcular rota
</button>*/}



                <span className="px-3 py-1 bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-300 rounded-full text-xs">
                  {list.length > 0 ? "Em uso" : "Livre"}
                </span>
              </div>

              {list.length > 0 ? (
  list.map((t, i) => (
    <div
      key={t.id}
      className="bg-white dark:bg-gray-800 border rounded-lg p-3 mt-3 shadow-sm"
    >
      <div className="flex justify-between items-center">
                <div className="flex items-center gap-3">
          <span className="w-6 h-6 flex items-center justify-center bg-black text-white text-xs rounded-full">
            {i + 1}
          </span>
          <h4 className="font-semibold text-sm">{t.title}</h4>
          
        </div>

        <div className="flex items-center gap-2">
          <span className="text-sm text-gray-600 dark:text-gray-300">{t.time}</span>
          <button onClick={() => removeTask(moto.id, t.id)}>
            <Trash2 size={18} className="text-red-500 hover:text-red-700" />
          </button>
        </div>
      </div>

     
      
    </div>
  ))
) : (

                <p className="mt-2 text-sm text-gray-500 dark:text-gray-400 border rounded p-2 text-center">
                  Nenhuma tarefa atribuída
                </p>
              )}
            </div>
          );
        })}

      </div>


      <div className="w-1/3 bg-white dark:bg-gray-800 flex flex-col">

        <div className="p-4 border-b flex justify-between items-center">
          <button onClick={() => setSelectedDate(new Date(selectedDate.getFullYear(), selectedDate.getMonth() - 1, 1))}>
            <ChevronLeft/>
          </button>
          <h3 className="font-semibold">
            {selectedDate.toLocaleString("pt-PT", {month:"long"})} de {selectedDate.getFullYear()}
          </h3>
          <button onClick={() => setSelectedDate(new Date(selectedDate.getFullYear(), selectedDate.getMonth() + 1, 1))}>
            <ChevronRight/>
          </button>
        </div>

        <div className="grid grid-cols-7 p-4 gap-1">
          {Array.from({ length: firstDayOfMonth }).map((_, i) =>
            <div key={i} className="aspect-square"/>
          )}

          {Array.from({ length: daysInMonth }).map((_, i) => {
            const d = i + 1;
            const dt = new Date(selectedDate.getFullYear(), selectedDate.getMonth(), d);
            const hasTasks = tasks.some(t => t.date === formatDate(dt));

            return (
              <button key={d}
                className={`aspect-square border p-1 text-sm flex flex-col justify-between ${
                  dt.toDateString() === selectedDate.toDateString()
                    ? "bg-black text-white"
                    : "hover:bg-gray-100 dark:bg-gray-700"
                }`}
                onClick={() => setSelectedDate(dt)}
              >
                <span>{d}</span>
                {hasTasks && <div className="w-2 h-2 rounded-full bg-blue-500 self-center mb-1"/>}
              </button>
            );
          })}
        </div>

        {selectedTask && (
          <div className="border-t p-4">
            <div className="flex justify-between">
              <h3 className="font-semibold">{selectedTask.title}</h3>
              <X className="cursor-pointer" onClick={() => setSelectedTask(null)}/>
            </div>
            

            <button
              className="w-full mt-3 bg-black text-white py-2 rounded"
              onClick={() => setShowAssignModal(true)}
            >
              Atribuir
            </button>
          </div>
        )}

      </div>


      {showAssignModal && selectedTask && (
        <div className="fixed inset-0 bg-black/50 flex justify-center items-center">
          <div className="bg-white dark:bg-gray-800 p-4 rounded w-80">
            <h3 className="font-semibold mb-3">Selecionar mota</h3>

            {availableVehicles.length > 0 ? (
            availableVehicles.map((vehicle) => (
           <button
            key={vehicle.id}
            className="w-full border p-2 mb-2 rounded hover:bg-gray-100 dark:bg-gray-700 text-left"
            onClick={() => assignTask(vehicle.id)}
            >
            <div className="font-semibold">{vehicle.name}</div>
             <div className="text-sm text-gray-500 dark:text-gray-400">
              Empresa: {vehicle.driver?.company.name}
              </div>

           </button>
  ))
) : (
  <p className="text-sm text-gray-500 dark:text-gray-400 text-center">
    Nenhuma mota compatível com o serviço desta tarefa
  </p>
)}
            <button className="w-full mt-2 py-2 bg-gray-300 rounded"
              onClick={() => setShowAssignModal(false)}
            >
              Cancelar
            </button>
          </div>
        </div>
      )}

    </div>
  );
};

export default TaskAssignmentPage;
