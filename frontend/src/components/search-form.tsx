import { Search } from "lucide-react"

import { Label } from "@/components/ui/label"
import {
  SidebarInput,
} from "@/components/ui/sidebar"

export function SearchForm({ ...props }: React.ComponentProps<"form">) {
  return (
    <form className="relative w-full" {...props}>
          <Label htmlFor="search" className="sr-only">
            Search
          </Label>
          <SidebarInput
            id="search"
            placeholder="Suche nach Inhalten..."
            className="pl-8 w-full"
          />
          <Search className="pointer-events-none absolute top-1/2 left-2 size-4 -translate-y-1/2 opacity-50 select-none" />
    </form>
  )
}
