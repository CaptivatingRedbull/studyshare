import { IconCirclePlusFilled, type Icon } from "@tabler/icons-react"

import {
    SidebarGroup,
    SidebarGroupContent,
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem,
} from "@/components/ui/sidebar"
import { Link, useLocation } from "react-router-dom"
import { useState } from "react"
import { Dialog, DialogContent, DialogTitle } from "@/components/ui/dialog";
import { DialogHeader } from "@/components/ui/dialog";
import { ContentCreateForm } from "./content-create-form";



export function NavMain({
    items,
}: {
    items: {
        title: string
        url: string
        icon?: Icon
    }[]
}) {
    const [uploadOpen, setUploadOpen] = useState(false);
    const location = useLocation();
    return (
        <SidebarGroup>
            <SidebarGroupContent className="flex flex-col gap-2">
                <SidebarMenu>
                    <SidebarMenuItem className="flex items-center gap-2">
                        <SidebarMenuButton
                            tooltip="Quick Create"
                            className="bg-primary text-primary-foreground hover:bg-primary/90 hover:text-primary-foreground active:bg-primary/90 active:text-primary-foreground min-w-8 duration-200 ease-linear"
                            onClick={() => setUploadOpen(true)}
                        >
                            <IconCirclePlusFilled />
                            <span>Inhalt hochladen</span>
                        </SidebarMenuButton>
                        <Dialog open={uploadOpen} onOpenChange={setUploadOpen}>
                            <DialogContent>
                                <DialogHeader>
                                    <DialogTitle>Neuen Inhalt hochladen</DialogTitle>
                                </DialogHeader>
                                <ContentCreateForm/>
                            </DialogContent>
                        </Dialog>
                    </SidebarMenuItem>
                </SidebarMenu>
                <SidebarMenu>
                    {items.map((item) => (
                        <SidebarMenuItem key={item.title} >
                            <SidebarMenuButton tooltip={item.title} isActive={location.pathname === item.url}>
                                <Link to={item.url} className="flex items-center gap-2">
                                    {item.icon && <item.icon />}
                                    <span>{item.title}</span>
                                </Link>
                            </SidebarMenuButton>
                        </SidebarMenuItem>
                    ))}
                </SidebarMenu>
            </SidebarGroupContent>
        </SidebarGroup>
    )
}
