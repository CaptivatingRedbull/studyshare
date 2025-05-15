import * as React from "react";
import navData from "@/data/dashboard/NavMain.json";

import {
  IconCamera,
  IconChartBar,
  IconCloudShare,
  IconDashboard,
  IconFileAi,
  IconFileDescription,
  IconFolder,
  IconHelp,
  IconListDetails,
  IconSearch,
  IconSettings,
  IconUsers,
} from "@tabler/icons-react";

import { NavMain } from "@/components/nav-main";
import { NavSecondary } from "@/components/nav-secondary";
import { NavUser } from "@/components/nav-user";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar";
import { useAuth } from "@/context/AuthContext";

const data = {
  user: {
    name: "mmustermann",
    email: "m@example.com",
  },
  navMain: [
    {
      title: "Inhalte durchstöbern",
      url: "#",
      icon: IconDashboard,
    },
    {
      title: "Gemeldeten Inhalte",
      url: "#",
      icon: IconListDetails,
    },
    {
      title: "User Verwalten",
      url: "#",
      icon: IconChartBar,
    },
    {
      title: "Kurse Struktur Verwalten",
      url: "#",
      icon: IconFolder,
    },
    {
      title: "Team",
      url: "#",
      icon: IconUsers,
    },
  ],
  navClouds: [
    {
      title: "Capture",
      icon: IconCamera,
      isActive: true,
      url: "#",
      items: [
        {
          title: "Active Proposals",
          url: "#",
        },
        {
          title: "Archived",
          url: "#",
        },
      ],
    },
    {
      title: "Proposal",
      icon: IconFileDescription,
      url: "#",
      items: [
        {
          title: "Active Proposals",
          url: "#",
        },
        {
          title: "Archived",
          url: "#",
        },
      ],
    },
    {
      title: "Prompts",
      icon: IconFileAi,
      url: "#",
      items: [
        {
          title: "Active Proposals",
          url: "#",
        },
        {
          title: "Archived",
          url: "#",
        },
      ],
    },
  ],
  navSecondary: [
    {
      title: "Settings",
      url: "#",
      icon: IconSettings,
    },
    {
      title: "Get Help",
      url: "#",
      icon: IconHelp,
    },
    {
      title: "Search",
      url: "#",
      icon: IconSearch,
    },
  ],
};

const iconMap: Record<string, React.FC<any>> = {
  IconDashboard,
  IconListDetails,
  IconChartBar,
  IconFolder,
  IconUsers,
};

function NavProvider() {
  const { user } = useAuth();
  const { adminNavMain, studentNavMain } = navData;
  const nav = !user
    ? []
    : user.role === "ADMIN"
    ? adminNavMain
    : user.role === "STUDENT"
    ? studentNavMain
    : [];
  return nav.map((item: any) => ({
    ...item,
    icon: iconMap[item.icon] || undefined,
  }));
}

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  return (
    <Sidebar collapsible="offcanvas" {...props}>
      <SidebarHeader>
        <SidebarMenu>
          <SidebarMenuItem>
            <SidebarMenuButton
              asChild
              className="data-[slot=sidebar-menu-button]:!p-1.5"
            >
              <a href="#">
                <IconCloudShare className="!size-5" />
                <span className="text-base font-semibold">StudyShare</span>
              </a>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarHeader>
      <SidebarContent>
        <NavMain items={NavProvider()} />
        <NavSecondary items={data.navSecondary} className="mt-auto" />
      </SidebarContent>
      <SidebarFooter>
        <NavUser user={data.user} />
      </SidebarFooter>
    </Sidebar>
  );
}
