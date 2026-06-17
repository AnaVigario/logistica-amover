export type UserRole = 'manager' | 'admin';

export interface User {
  id: string;      // <-- adicionar isto!
  name: string;
  email: string;
  role: "admin" | "manager";
}


