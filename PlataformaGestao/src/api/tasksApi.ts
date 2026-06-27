import { supabase } from "../supabaseClient";

export async function loadTasks() {
  return await supabase
    .from("tasks")
    .select("*")
    .order("date", { ascending: true });
}

export async function loadMotorcycles() {
  return await supabase
    .from("motorcycles")
    .select("*")
    .order("id", { ascending: true });
}

export async function loadTaskAssignments() {
  return await supabase
    .from("task_assignments")
    .select("*");
}

export async function assignTask(taskId: number, motorcycleId: number) {
  return await supabase
    .from("task_assignments")
    .insert([
      {
        taskid: taskId,
        motorcycleid: motorcycleId,
        startdate: new Date(),
      },
    ]);
}

export async function removeTask(taskId: number) {
  return await supabase
    .from("task_assignments")
    .delete()
    .eq("taskid", taskId);
}


export async function removeAllTasksFromMotorcycle(motorcycleId: number) {
  return await supabase
    .from("task_assignments")
    .delete()
    .eq("motorcycleid", motorcycleId);
}
