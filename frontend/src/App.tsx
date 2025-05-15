import { Routes, Route, Navigate, Outlet, Link } from "react-router-dom";
import "./App.css";
import { LandingPage } from "./pages/LandingPage";
import Dashboard from "./dashboard"; // ggf. Pfad anpassen

// Layouts
function PublicLayout() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center">
      <Outlet />
    </div>
  );
}

function ProtectedLayout() {
  return (
    <div className="min-h-screen">
      <header className="p-4 border-b">
        <Link to="/">Home</Link> | <Link to="/exchange">Exchange</Link>
      </header>
      <main className="p-4">
        <Outlet />
      </main>
    </div>
  );
}

function RegisterPage() {
  return <div>Registration Form Here</div>;
}



function ExchangePage() {
  return <Dashboard />;
}


export default function App() {
  return (
    <Routes>
      {/* Public */}
      <Route element={<PublicLayout />}>
        <Route path="/" element={<LandingPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Route>

      {/* Main app */}
      <Route element={<ProtectedLayout />}>
        <Route path="/exchange" element={<ExchangePage />} />
      </Route>

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
