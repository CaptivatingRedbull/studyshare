import * as React from "react";
import { cn } from "@/lib/utils";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { AlertCircle } from "lucide-react"; // Default error icon

interface FormErrorAlertProps {
  message: string | null; 
  title?: string;         
  className?: string;     
}

export const FormErrorAlert: React.FC<FormErrorAlertProps> = ({
  message,
  title = "Error", 
  className,
}) => {
  if (!message) {
    return null; 
  }

  return (
    <Alert variant="destructive" className={cn("mb-4", className)}>
      <AlertCircle className="h-4 w-4" /> {/* Default icon */}
      <AlertTitle>{title}</AlertTitle>
      <AlertDescription>{message}</AlertDescription>
    </Alert>
  );
};
