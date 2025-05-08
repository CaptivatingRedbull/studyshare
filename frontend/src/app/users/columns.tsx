import { ColumnDef } from "@tanstack/react-table"
import { User } from "@/lib/types.ts"
export const columns: ColumnDef<User>[] = [
    {
      accessorKey: "firstname",
      header: "First Name",
    },
    {
      accessorKey: "lastname",
      header: "Last Name",
    },
    {
      accessorKey: "email",
      header: "Email",
    },
  ]