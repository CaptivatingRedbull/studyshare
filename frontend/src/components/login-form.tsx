// frontend/src/components/login-form.tsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { login as apiLogin } from "@/api/authApi";
import { toast } from "sonner";
import { Link } from "react-router-dom";
import { FormErrorAlert } from "@/components/ui/FormErrorAlert"; 
import { FormInfoAlert } from "@/components/ui/FormInfoAlert"; // Import the new FormInfoAlert
import { useAuth } from "@/context/authContext";

export function LoginForm({
  className,
  ...props
}: React.ComponentProps<"div">) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [loginError, setLoginError] = useState<string | null>(null);
  const [loginInfo, setLoginInfo] = useState<string | null>(null); // State for informational messages

  const navigate = useNavigate();
  const { login: contextLogin } = useAuth();

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setIsLoading(true);
    setLoginError(null);
    setLoginInfo(null); // Clear previous info messages

    try {
      const loginData = { username, password: password };
      
      const response = await apiLogin(loginData);
      contextLogin(response);
      
      
      setLoginInfo("Login successful! Redirecting to your dashboard...");
      toast.success(`Welcome back, ${response.username}!`); 

      
      setTimeout(() => {
        navigate("/dashboard");
      }, 5000);

    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.response?.data?.error || err.message || "Login failed. Please try again.";
      setLoginError(errorMessage);
      console.error("Login error:", err);
      setIsLoading(false);
    } 
    
  };

  return (
    <div className={cn("flex flex-col gap-6 w-full max-w-md", className)} {...props}>
      <Card>
        <CardHeader>
          <CardTitle>Login to your account</CardTitle>
          <CardDescription>
            Enter your username and password below to login.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit}>
            <div className="flex flex-col gap-4">
              
              <FormErrorAlert message={loginError} title="Login Error" />
              <FormInfoAlert message={loginInfo} title="Login Status" /> {/* Display info messages */}

              <div className="grid gap-2">
                <Label htmlFor="username">Username</Label>
                <Input
                  id="username"
                  type="text"
                  placeholder="Username hier eingeben"
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
                  placeholder="Passwort hier eingeben"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  disabled={isLoading}
                />
              </div>

              <div className="flex flex-col gap-3 mt-2">
                <Button type="submit" className="w-full" disabled={isLoading}>
                  {isLoading ? "Logging in..." : "Login"}
                </Button>
              </div>
            </div>
            <div className="mt-6 text-center text-sm">
              Don&apos;t have an account?{" "}
              <Link to="/register" className="underline underline-offset-4 hover:text-primary">
                Sign up
              </Link>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
