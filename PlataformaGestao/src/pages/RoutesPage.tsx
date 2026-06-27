import { useEffect, useState, useMemo } from "react";
import { supabase } from "../supabaseClient";
import { Bike, MapPin, Clock, ArrowLeft } from "lucide-react";

import {
  DndContext,
  closestCenter,
  PointerSensor,
  useSensor,
  useSensors
} from "@dnd-kit/core";

import {
  SortableContext,
  verticalListSortingStrategy,
  useSortable,
  arrayMove
} from "@dnd-kit/sortable";

import { CSS } from "@dnd-kit/utilities";

/* ================= TYPES ================= */

interface Motorcycle {
  id: number;
  name: string;
  status: string;
}

interface Task {
  id: number;
  title: string;
  date: string;
  time: string;
  priority: "ALTA" | "MÉDIA" | "BAIXA";
  motorcycleid: number | null;
  clientid: number | null;

  street?: string | null;
  door_number?: string | null;
  floor?: string | null;
  postal_code?: string | null;
  city?: string | null;
}




interface RoutePoint {
  id: number;
  stop_order: number;
  task: Task;
}

/* ================= SORTABLE TASK ================= */

function SortableTask({
  task,
  index,
  isSaved
}: {
  task: Task;
  index: number;
  isSaved: boolean;
}): JSX.Element {

  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition
  } = useSortable({ id: task.id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition
  };

  const address = task.street
    ? `${task.street} ${task.door_number ?? ""} · ${task.city ?? ""}`
    : "Sem morada";

    function getPriorityColor(priority: Task["priority"]) {
  switch (priority) {
    case "ALTA":
      return "bg-red-500 text-white";
    case "MÉDIA":
      return "bg-orange-500 text-white";
    case "BAIXA":
      return "bg-green-500 text-white";
    default:
      return "bg-gray-200";
  }
}


  return (
    <div
      ref={setNodeRef}
      style={style}
      {...attributes}
      {...listeners}
      className={`p-4 rounded shadow cursor-grab ${
        isSaved
          ? "bg-green-50 border border-green-300"
          : "bg-white dark:bg-gray-800"
      }`}
    >
      <div className="flex justify-between items-center">

        <div className="flex items-center gap-3">
          <div className="w-7 h-7 rounded-full bg-black text-white text-xs flex items-center justify-center">
            {index + 1}
          </div>

          <p className="font-medium">{task.title}</p>
        </div>

       <span
  className={`text-xs px-2 py-1 rounded-full ${getPriorityColor(task.priority)}`}
>
  {task.priority}
</span>

      </div>

      <div className="text-sm text-gray-600 dark:text-gray-300 mt-1 flex items-center gap-1">
        <MapPin size={14} />
        {address}
      </div>
    </div>
  );
}


/* ================= COMPONENT ================= */

const RoutesPage = ({ vehicleId, date }: any) => {

  const [motorcycles, setMotorcycles] = useState<Motorcycle[]>([]);
  const [selectedMotorcycle, setSelectedMotorcycle] = useState<Motorcycle | null>(null);

  const [selectedDate, setSelectedDate] = useState(
    date ? date.toISOString().split("T")[0] : new Date().toISOString().split("T")[0]
  );

  const [tasks, setTasks] = useState<Task[]>([]);
  const [draftTasks, setDraftTasks] = useState<Task[]>([]);

  const [route, setRoute] = useState<any>(null);
  const [routePoints, setRoutePoints] = useState<RoutePoint[]>([]);

  const sensors = useSensors(useSensor(PointerSensor));

  /* ================= LOAD DATA ================= */

  useEffect(() => {
    loadMotorcycles();
  }, []);

  useEffect(() => {
    if (!selectedMotorcycle) return;

    loadTasks();
    loadRoute();
  }, [selectedMotorcycle, selectedDate]);

  useEffect(() => {
    if (!vehicleId || motorcycles.length === 0) return;

    const moto = motorcycles.find(m => m.id === vehicleId);
    if (moto) setSelectedMotorcycle(moto);
  }, [vehicleId, motorcycles]);

  async function loadMotorcycles() {
    const { data } = await supabase
      .from("vehicles")
      .select("id,name,status");

    setMotorcycles(data || []);
  }

  

  async function loadTasks() {
    if (!selectedMotorcycle) return;

    const { data } = await supabase
      .from("tasks")
      .select("*")
      .eq("motorcycleid", selectedMotorcycle.id)
      .eq("date", selectedDate);

    if (data) {
      setTasks(data);
      setDraftTasks(data);
    }
  }

  async function loadRoute() {
    if (!selectedMotorcycle) return;

    const { data } = await supabase
      .from("routes")
      .select(`
        id,
        route_points (
          id,
          stop_order,
          task:tasks (*)
        )
      `)
      .eq("vehicle_id", selectedMotorcycle.id)
      .eq("route_date", selectedDate)
      .maybeSingle();

    if (!data) {
      setRoute(null);
      setRoutePoints([]);
      return;
    }

    setRoute(data);
    const normalized: RoutePoint[] = (data.route_points || []).map((rp: any) => ({
  id: rp.id,
  stop_order: rp.stop_order,
  task: Array.isArray(rp.task) ? rp.task[0] : rp.task
}));

setRoutePoints(normalized);

  }

  /* ================= TASK SOURCE ================= */

  const tasksForMotorcycle = useMemo(() => {

    if (routePoints.length > 0) {
      return [...routePoints]
        .sort((a,b)=>a.stop_order-b.stop_order)
        .map(rp => rp.task);
    }

    return draftTasks;

  }, [routePoints, draftTasks]);

  /* ================= DRAG & DROP ================= */

  async function handleDragEnd(event:any) {

    const { active, over } = event;
    if (!over || active.id === over.id) return;

    const oldIndex = tasksForMotorcycle.findIndex(t => t.id === active.id);
    const newIndex = tasksForMotorcycle.findIndex(t => t.id === over.id);

    const reordered = arrayMove(tasksForMotorcycle, oldIndex, newIndex);

    if (route) {

      for (let i = 0; i < reordered.length; i++) {
        const rp = routePoints.find(r => r.task.id === reordered[i].id);

        await supabase
          .from("route_points")
          .update({ stop_order: i + 1 })
          .eq("id", rp?.id);
      }

      loadRoute();
    }

    else {
      setDraftTasks(reordered);
    }
  }

  /* ================= SAVE ROUTE ================= */

  async function handleCalculateRoute() {

    if (!selectedMotorcycle || tasksForMotorcycle.length === 0) return;

    let routeId = route?.id;

    if (!routeId) {
      const { data: newRoute } = await supabase
        .from("routes")
        .insert({
          vehicle_id: selectedMotorcycle.id,
          route_date: selectedDate
        })
        .select()
        .single();

      routeId = newRoute.id;
    }

    await supabase
      .from("route_points")
      .delete()
      .eq("route_id", routeId);

    for (let i = 0; i < tasksForMotorcycle.length; i++) {
      await supabase.from("route_points").insert({
        route_id: routeId,
        task_id: tasksForMotorcycle[i].id,
        stop_order: i + 1
      });
    }

    loadRoute();
  }

  /* ================= UI ================= */

  return (
    <div className="bg-gray-100 dark:bg-gray-700 p-6 h-full">

      {!selectedMotorcycle && (
        <>
          <h2 className="text-xl font-semibold mb-4">Rotas por Mota</h2>

          <input
            type="date"
            value={selectedDate}
            onChange={e => setSelectedDate(e.target.value)}
            className="border rounded px-2 py-1 mb-4"
          />

          <div className="grid grid-cols-3 gap-4">
            {motorcycles.map(moto => (
              <div
                key={moto.id}
                onClick={() => setSelectedMotorcycle(moto)}
                className="bg-white dark:bg-gray-800 p-4 rounded shadow cursor-pointer"
              >
                <Bike /> {moto.name}
              </div>
            ))}
          </div>
        </>
      )}

      {selectedMotorcycle && (
        <>
          <div className="flex items-center gap-3 mb-4">
            <button onClick={() => setSelectedMotorcycle(null)}>
              <ArrowLeft size={16}/> Voltar
            </button>

            <h2 className="text-xl font-semibold">
              Rota — {selectedMotorcycle.name}
            </h2>
          </div>

          <button
            onClick={handleCalculateRoute}
            className="mb-4 bg-black text-white px-4 py-2 rounded"
          >
            Guardar rota
          </button>

          {tasksForMotorcycle.length === 0 ? (
            <div className="bg-white dark:bg-gray-800 p-6 rounded shadow text-center text-gray-500 dark:text-gray-400">
              Nenhuma tarefa atribuída.
            </div>
          ) : (
            <DndContext
              sensors={sensors}
              collisionDetection={closestCenter}
              onDragEnd={handleDragEnd}
            >
              <SortableContext
                items={tasksForMotorcycle.map(t => t.id)}
                strategy={verticalListSortingStrategy}
              >
                <div className="space-y-3">
                  {tasksForMotorcycle.map((task, index) => (
                    <SortableTask
                      key={task.id}
                      task={task}
                      index={index}
                      isSaved={routePoints.length > 0}
                      
                    />
                  ))}
                </div>
              </SortableContext>
            </DndContext>
          )}
        </>
      )}
    </div>
  );
};

export default RoutesPage;
