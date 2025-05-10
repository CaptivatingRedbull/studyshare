import React from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import "@radix-ui/themes/styles.css";
import "./index.css";
import App from "./App.tsx";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ThemeProvider } from "./components/theme-provider.tsx";

const queryClient = new QueryClient();

createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
        <BrowserRouter>
          <App />
        </BrowserRouter>
      </ThemeProvider>
    </QueryClientProvider>
  </React.StrictMode>
);
