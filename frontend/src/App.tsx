import { Routes, Route, Navigate, Outlet } from "react-router-dom";
import "@/App.css";
import { LandingPage } from "@/pages/LandingPage";
import { BrowsePage } from "@/pages/BrowsePage";
import { AppSidebar } from "@/components/app-sidebar";
import { SidebarInset, SidebarProvider } from "./components/ui/sidebar";

function DashboardLayout() {
    return (
        <div className="flex h-screen bg-background">
            <SidebarProvider>
                <AppSidebar />
                <SidebarInset>
                    <div className="overflow-y-auto h-full">
                        <Outlet />
                    </div>
                </SidebarInset>
            </SidebarProvider>
        </div>
    );
}

export function App() {
    return (
        <Routes>
            {/* Public */}
            <Route path="/" element={<LandingPage />} />

            {/* Main app */}
            <Route path="/exchange" element={<DashboardLayout />}>
                <Route index element={<BrowsePage />} /> {/* Or some other default dashboard page */}
                <Route path="browse" element={<BrowsePage />} />
            </Route>

            {/* Fallback */}
            <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
    );
}
