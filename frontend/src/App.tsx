import { Routes, Route, Navigate, Outlet, Link } from "react-router-dom";
import "./App.css";
import Page from "./dashboard";

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

function LandingPage() {
  return (
    <div className="text-center space-y-4">
      <h1 className="text-4xl">Welcome to StudyShare</h1>
      <div className="space-x-4">
        <Link to="/login" className="btn">Log in</Link>
        <Link to="/register" className="btn-outline">Register</Link>
      </div>
    </div>
  );
}

function LoginPage() {
  return <div><Page/></div>; 
}

function RegisterPage() {
  return <div>Registration Form Here</div>;
}

function ExchangePage() {
  return <div>Upload/download UI here</div>;
}

export default function App() {
  return (
    <Routes>
      {/* Public */}
      <Route element={<PublicLayout />}>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
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
