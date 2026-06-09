/** Matches backend Jakarta @Email / @NotBlank messages for login. */
export const AUTH_EMAIL_MESSAGE = "Email must be a valid address (e.g. user@example.com)";

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export function validateLoginFields(
  email: string,
  password: string
): Record<string, string> | null {
  const errors: Record<string, string> = {};
  const trimmedEmail = email.trim();

  if (!trimmedEmail) {
    errors.email = "Email is required";
  } else if (!EMAIL_PATTERN.test(trimmedEmail)) {
    errors.email = AUTH_EMAIL_MESSAGE;
  }

  if (!password) {
    errors.password = "Password is required";
  }

  return Object.keys(errors).length > 0 ? errors : null;
}

export function summarizeFieldErrors(fieldErrors: Record<string, string>): string {
  return Object.values(fieldErrors).join("; ");
}
