import * as React from "react";
import navData from "@/data/dashboard/NavMain.json";

import {
  IconBinoculars,
  IconBrightnessDown,
  IconCloudShare,
  IconFileAlert,
  IconFolderOpen,
  IconHelp,
  IconListDetails,
  IconListTree,
  IconMail,
  IconMessageReport,
  IconMoon,
  IconReplaceUser,
  IconSettings,

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

import { SearchForm } from "./search-form";
import { useTheme } from "./theme-provider";
import { Button } from "./ui/button";
const data = {
  navSecondary: [
    {
      title: "Einstellungen",
      url: "/exchange/settings",
      icon: IconSettings,
    },
    {
      title: "Hilfe",
      url: "/exchange/help",
      icon: IconHelp,
    },
    {
      title: "Kontaktieren sie uns",
      url: "/exchange/contact",
      icon: IconMail,
    },
    {
      title: "Datenschutz",
      url: "/exchange/privacy",
      icon: IconListDetails,
    },
  ],
};

const iconMap: Record<string, React.FC<any>> = {

  IconBinoculars,
  IconFolderOpen,
  IconFileAlert,
  IconMessageReport,
  IconReplaceUser,
  IconListTree,

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

function UserProvider() {
  const { user } = useAuth();
  return user
    ? {
        name: user.username,
        email: user.email,
      }
    : {
        name: "",
        email: "",
      };
}

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  const { theme, setTheme } = useTheme();

  function handleThemeChange() {
    if (theme === "dark") {
      setTheme("light");
    } else {
      setTheme("dark");
    }
  }

  function themeIconProvider() {
    if (theme === "dark") {
      return <IconBrightnessDown />;
    } else {
      return <IconMoon />;
    }
  }

  return (
    <Sidebar collapsible="offcanvas" {...props}>
      <SidebarHeader>
        <SidebarMenu>
          <SidebarMenuItem>
            <div className="flex items-center gap-2">
              <SidebarMenuButton
                asChild
                className="data-[slot=sidebar-menu-button]:!p-1.5"
              >
                <a href="/exchange">
                  <IconCloudShare className="!size-5" />
                  <span className="text-base font-semibold">StudyShare</span>
                </a>
              </SidebarMenuButton>
              <Button variant="outline" size="icon" onClick={handleThemeChange}>
                {themeIconProvider()}
              </Button>
            </div>
          </SidebarMenuItem>
        </SidebarMenu>
        <SearchForm />
      </SidebarHeader>
      <SidebarContent>
        <NavMain items={NavProvider()} />
        <NavSecondary items={data.navSecondary} className="mt-auto" />
      </SidebarContent>
      <SidebarFooter>
        <NavUser user={UserProvider()} />
      </SidebarFooter>
    </Sidebar>
  );
}
