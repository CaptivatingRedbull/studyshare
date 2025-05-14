import React from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import "@/index.css";
import App from "@/App.tsx";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ThemeProvider, useTheme } from "@/components/theme-provider.tsx";
import { AuthProvider } from "@/context/AuthContext.tsx";
import { Toaster } from "sonner";

const queryClient = new QueryClient();

function ToasterWrapper() {
  const theme = useTheme();
  return <Toaster richColors position="bottom-right" theme={theme.theme} />;
}

createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <ThemeProvider defaultTheme="system" storageKey="vite-ui-theme">
        <BrowserRouter>
          <AuthProvider>
            <App />
            <ToasterWrapper />
          </AuthProvider>
        </BrowserRouter>
      </ThemeProvider>
    </QueryClientProvider>
  </React.StrictMode>
);
