import React from "react";
import { toast, type ExternalToast } from "sonner";
import { Info, AlertCircle, CheckCircle, Loader2 } from "lucide-react";

// Helper component for consistent toast content structure
// This component is not exported and is used internally by the toast functions.
interface ToastContentProps {
  title?: string;
  message: string;
  // No iconClassName needed here as we will apply it directly in the toast functions
}

const ToastContentInternal: React.FC<ToastContentProps> = ({
  title,
  message,
}) => (
  <div
    className="flex items-center justify-center w-auto text-center"
    data-testid="toast-content-internal"
  >
    <div className="flex flex-col items-start">
      {title && <p className="font-semibold text-sm leading-tight">{title}</p>}
      <p className="text-sm opacity-90 leading-tight">{message}</p>
    </div>
  </div>
);

// --- Toast Info ---
interface ToastInfoParams {
  message: string;
  title?: string;
  options?: ExternalToast; // Sonner's options (duration, etc.)
}

/**
 * Displays an informational toast.
 * @param message - The main message to display.
 * @param title - Optional title for the toast. Defaults to "Information".
 * @param options - Optional Sonner toast options.
 */
export const toastInfo = ({
  message,
  title = "Information",
  options,
}: ToastInfoParams) => {
  toast(<ToastContentInternal title={title} message={message} />, {
    ...options,
  });
};

// --- Toast Error ---
interface ToastErrorParams {
  message: string;
  title?: string;
  options?: ExternalToast;
}

/**
 * Displays an error toast.
 * @param message - The main error message to display.
 * @param title - Optional title for the toast. Defaults to "Error".
 * @param options - Optional Sonner toast options.
 */
export const toastError = ({
  message,
  title = "Error",
  options,
}: ToastErrorParams) => {
  toast.error(<ToastContentInternal title={title} message={message} />, {
    ...options,
  });
};

// --- Toast Success ---
interface ToastSuccessParams {
  message: string;
  title?: string;
  options?: ExternalToast;
}

/**
 * Displays a success toast.
 * @param message - The main success message to display.
 * @param title - Optional title for the toast. Defaults to "Success".
 * @param options - Optional Sonner toast options.
 */
export const toastSuccess = ({
  message,
  title = "Success",
  options,
}: ToastSuccessParams) => {
  toast.success(<ToastContentInternal title={title} message={message} />, {
    ...options,
  });
};

// --- Toast Warning ---
interface ToastWarningParams {
  message: string;
  title?: string;
  options?: ExternalToast;
}

/**
 * Displays a warning toast.
 * @param message - The main warning message to display.
 * @param title - Optional title for the toast. Defaults to "Warning".
 * @param options - Optional Sonner toast options.
 */
export const toastWarning = ({
  message,
  title = "Warning",
  options,
}: ToastWarningParams) => {
  toast.warning(<ToastContentInternal title={title} message={message} />, {
    ...options,
  });
};

// --- Toast Loading ---
interface ToastLoadingParams {
  message: string;
  title?: string;
  options?: ExternalToast;
}

/**
 * Displays a simple loading-styled toast.
 * For promises or dynamic updates, use sonner's `toast.promise()` or `toast.loading()` directly.
 * @param message - The message to display while loading.
 * @param title - Optional title for the toast. Defaults to "Loading...".
 * @param options - Optional Sonner toast options.
 */
export const toastLoading = ({
  message,
  title = "Loading...",
  options,
}: ToastLoadingParams) => {
  toast(
    // Using the generic toast for a loading *style*
    <ToastContentInternal title={title} message={message} />,
    {
      duration: options?.duration || 100000, // Default to a long duration for loading toasts
      ...options,
    }
  );
};
