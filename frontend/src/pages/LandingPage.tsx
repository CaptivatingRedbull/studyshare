import { LoginForm } from "@/components/login-form";
import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import React from "react";

export function LandingPage() {
  const [isLoginOpen, setIsLoginOpen] = React.useState(false);
  const [isRegisterOpen, setIsRegisterOpen] = React.useState(false);

  return (
    <div className="text-center space-y-4">
      <h1 className="text-4xl">Welcome to StudyShare</h1>
      <div className="space-x-4">
        {/* Login Button and Modal */}
        <Button onClick={() => setIsLoginOpen(true)} className="btn">Log in</Button>
        <Dialog open={isLoginOpen} onOpenChange={setIsLoginOpen}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Login</DialogTitle>
            </DialogHeader>
            <LoginForm /> {/* Use your existing login form component */}
          </DialogContent>
        </Dialog>

        {/* Register Button and Modal */}
        <Button onClick={() => setIsRegisterOpen(true)} className="btn-outline">Register</Button>
        <Dialog open={isRegisterOpen} onOpenChange={setIsRegisterOpen}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Register</DialogTitle>
            </DialogHeader>
            {/* <RegisterForm /> You would create and use your register form component here */}
             <div>Registration Form Here</div>
          </DialogContent>
        </Dialog>
      </div>
    </div>
  );
}