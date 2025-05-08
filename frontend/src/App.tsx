import "./App.css";
import { ThemeProvider } from "./components/theme-provider.tsx";
import Page from "./dashboard.tsx";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
        <Page />
      </ThemeProvider>
    </QueryClientProvider>
  );
}

export default App;
