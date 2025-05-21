import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { login as apiLogin } from "@/api/authApi";
import { toastSuccess, toastError } from "@/components/ui/sonner";
import { Link } from "react-router-dom";

import { useAuth } from "@/context/AuthContext";

export function LoginForm({
    className,
    ...props
}: React.ComponentProps<"div">) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [loginError, setLoginError] = useState<string | null>(null);
    const [loginInfo, setLoginInfo] = useState<string | null>(null);

    const navigate = useNavigate();
    const { login: contextLogin } = useAuth();

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setIsLoading(true);
        setLoginError(null);
        setLoginInfo(null);

        try {
            const loginData = { username, password: password };

            const response = await apiLogin(loginData);
            contextLogin(response);

            toastSuccess({ message: `Willkommen zurück, ${response.username}!`, title: "Erfolgreich angemeldet!" });
            console.log("Erfolgreich angemeldet: ", loginInfo)


            setTimeout(() => {
                navigate("/exchange");
            }, 2000);

        } catch (err: any) {
            const errorMessage = err.response?.data?.message || err.response?.data?.error || err.message || "Login fehlgeschlagen. Versuche es erneut.";
            setLoginError(errorMessage);
            console.error("Login error: ", loginError);
            toastError({ message: errorMessage })
            setIsLoading(false);
        }

    };

    return (
        <div className={cn("flex flex-col gap-8 w-full max-w-md", className)} {...props}>
            <form onSubmit={handleSubmit}>
                <div className="flex flex-col gap-6">

                    <div className="grid gap-2">
                        <Label htmlFor="username">Benutzername</Label>
                        <Input
                            id="username"
                            type="text"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            disabled={isLoading}
                        />
                    </div>
                    <div className="grid gap-2">
                        <div className="flex items-center">
                            <Label htmlFor="password">Passwort</Label>
                            <a
                                href="#"
                                className="ml-auto inline-block text-sm underline-offset-4 hover:underline"
                            >
                                Passwort vergessen?
                            </a>
                        </div>
                        <Input
                            id="password"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            disabled={isLoading}
                        />
                    </div>

                    <div className="flex flex-col gap-3 mt-2">
                        <Button type="submit" className="w-full" disabled={isLoading}>
                            {isLoading ? "Anmelden..." : "Anmelden"}
                        </Button>
                    </div>
                </div>
                <div className="mt-6 text-center text-sm">
                    Noch keinen Account?{" "}
                    <Link to="/register" className="underline underline-offset-4 hover:text-primary">
                        Registrieren
                    </Link>
                </div>
            </form>
        </div>
    );
}
