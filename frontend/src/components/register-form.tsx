import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { register } from "@/api/authApi";
import { toastSuccess, toastError, toastWarning } from "@/components/ui/sonner";
import { useAuth } from "@/context/AuthContext";


export function RegisterForm({
    className,
    ...props
}: React.ComponentProps<"div">) {
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [passwordRepeat, setPasswordRepeat] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [registerError, setRegisterError] = useState<string | null>(null);
    const [registerInfo, setRegisterInfo] = useState<string | null>(null);


    const validatePassword = (pw: string) => {
        // At least 8 chars, one uppercase, one lowercase, one number, one special char
        const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/;
        return regex.test(pw);
    };

    const navigate = useNavigate();
    const { login: contextLogin } = useAuth();

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setIsLoading(true);
        setRegisterError(null);
        setRegisterInfo(null);

        if (password !== passwordRepeat) {
            setIsLoading(false);
            toastWarning({ title: "Passwort ", message: "Die Passwörter stimmen nicht überein." })
            return;
        }
        if (!validatePassword(password)) {
            setIsLoading(false);
            toastWarning({ title: "Passwort zu schwach!", message: "Das Passwort muss 8 Zeichen lang sein und Groß- und Kleinbuchstaben, Zahlen und Sonderzeichen enthalten."});
            return;
        }

        try {
            const registerData = {
                firstName: firstName,
                lastName: lastName,
                email: email,
                username: username,
                password: password
            }

            const response = await register(registerData);
            contextLogin(response);

            toastSuccess({ message: `Willkommen, ${response.username}!`, title: "Erfolgreich registriert!" });
            console.log("Erfolgreich registriert: ", registerInfo)

            setTimeout(() => {
                navigate("/exchange");
            }, 2000);
        } catch (err: any) {
            const errorMessage = err.response?.data?.message || err.response?.data?.error || err.message || "Registrierung fehlgeschlagen. Versuche es erneut.";
            setRegisterError(errorMessage);
            console.error("Login error: ", registerError);
            toastError({ message: errorMessage })
            setIsLoading(false);
        }
    };

    return (
        <div className={cn("flex flex-col gap-8 w-full max-w-md", className)} {...props}>
            <form onSubmit={handleSubmit}>
                <div className="flex flex-col gap-6">
                    <div className="grid gap-2">
                        <Label htmlFor="firstname">Vorname</Label>
                        <Input
                            id="firstName"
                            type="text"
                            value={firstName}
                            onChange={(e) => setFirstName(e.target.value)}
                            required
                            disabled={isLoading}
                        />
                    </div>
                    <div className="grid gap-2">
                        <Label htmlFor="lastName">Nachname</Label>
                        <Input
                            id="lastName"
                            type="text"
                            value={lastName}
                            onChange={(e) => setLastName(e.target.value)}
                            required
                            disabled={isLoading}
                        />
                    </div>
                    <div className="grid gap-2">
                        <Label htmlFor="email">E-mail Adresse</Label>
                        <Input
                            id="email"
                            type="text"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            disabled={isLoading}
                        />
                    </div>
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
                        <Label htmlFor="password">Passwort</Label>
                        <Input
                            id="password"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            disabled={isLoading}
                        />
                    </div>
                    <div className="grid gap-2">
                        <Label htmlFor="passwordRepeat">Passwort wiederholen</Label>
                        <Input
                            id="passwordRepeat"
                            type="password"
                            value={passwordRepeat}
                            onChange={(e) => setPasswordRepeat(e.target.value)}
                            required
                            disabled={isLoading}
                        />
                    </div>
                </div>
                <div className="flex flex-col gap-4 mt-2">
                    <Button type="submit" className="w-full" disabled={isLoading}>
                        {isLoading ? "Registrieren..." : "Registrieren"}
                    </Button>
                </div>
            </form>
        </div>
    )

}
