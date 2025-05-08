import { columns } from "./columns"
import { User } from "@/lib/types"
import { DataTable } from "./data-table"
import { fetchUsers } from "@/api/studyshareapi";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";


export default function DemoPage() {
  const { data: users = [], isLoading, error } = useQuery({
    queryKey: ["user"],
    queryFn: fetchUsers,
  });

  if (isLoading) return <p>Loading...</p>;
  if (error) return <p>Error loading users: {error.message}</p>;

  return (
    <div className="container mx-auto py-10">
      <DataTable columns={columns} data={users} />
    </div>
  );
}
