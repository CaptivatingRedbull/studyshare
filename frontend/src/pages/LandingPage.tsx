import { LoginForm } from "@/components/login-form";
import { RegisterForm } from "@/components/register-form";
import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { IconCloudShare } from "@tabler/icons-react";
import React from "react";

export function LandingPage() {
    const [isLoginOpen, setIsLoginOpen] = React.useState(false);
    const [isRegisterOpen, setIsRegisterOpen] = React.useState(false);

    return (
        <div className="text-center space-y-4">
            <div className="flex flex-row space-x-4">
                <IconCloudShare className="!size-10"/>
                <h1 className="text-4xl">Willkommen bei StudyShare!</h1>
                
            </div>

            <div className="space-x-4">
                <Button onClick={() => setIsLoginOpen(true)} className="btn">Anmelden</Button>
                <Dialog open={isLoginOpen} onOpenChange={setIsLoginOpen}>
                    <DialogContent>
                        <DialogHeader>
                            <DialogTitle>Anmelden</DialogTitle>
                        </DialogHeader>
                        <LoginForm />
                    </DialogContent>
                </Dialog>


                <Button onClick={() => setIsRegisterOpen(true)} className="btn-outline">Registrieren</Button>
                <Dialog open={isRegisterOpen} onOpenChange={setIsRegisterOpen}>
                    <DialogContent>
                        <DialogHeader>
                            <DialogTitle>Registrieren</DialogTitle>
                        </DialogHeader>
                        <RegisterForm />
                    </DialogContent>
                </Dialog>
            </div>
        </div>
    );
}