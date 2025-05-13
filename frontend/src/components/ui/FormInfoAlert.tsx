import * as React from "react";
import { cn } from "@/lib/utils";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Info } from "lucide-react"; // Default info icon

interface FormInfoAlertProps {
  message: string | null; // The informational message to display
  title?: string;         // Optional title for the alert, defaults to "Information"
  className?: string;     // Optional additional class names
}

export const FormInfoAlert: React.FC<FormInfoAlertProps> = ({
  message,
  title = "Information", // Default title for info alerts
  className,
}) => {
  if (!message) {
    return null; // Don't render anything if there's no message
  }

  return (
    <Alert variant="default" className={cn("mb-4", className)}> {/* Uses default variant */}
      <Info className="h-4 w-4" /> {/* Default icon for info */}
      <AlertTitle>{title}</AlertTitle>
      <AlertDescription>{message}</AlertDescription>
    </Alert>
  );
};