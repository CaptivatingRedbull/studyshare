import { Routes, Route, Navigate } from "react-router-dom";
import "./App.css";
import { LandingPage } from "./pages/LandingPage";
import { dashboard } from "./pages/dashboard"; // ggf. Pfad anpassen


export function App() {
    return (
        <Routes>
            {/* Public */}
            <Route>
                <Route path="/" element={<LandingPage />} />
            </Route>

            {/* Main app */}
            <Route>
                <Route path="/exchange" element={dashboard()} />
            </Route>

            {/* Fallback */}
            <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
    );
}
