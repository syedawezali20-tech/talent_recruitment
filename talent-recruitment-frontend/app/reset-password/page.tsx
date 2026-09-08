"use client";

import { FormEvent, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { resetPassword } from "@/lib/api";

/**
 * Checks each password requirement.
 */
function getPasswordChecks(password: string) {
  return {
    length: password.length >= 12 && password.length <= 64,
    uppercase: /[A-Z]/.test(password),
    lowercase: /[a-z]/.test(password),
    number: /\d/.test(password),
    special: /[@$!%*?&]/.test(password),
  };
}

/**
 * Returns password strength based on how many
 * requirements are satisfied.
 */
function getPasswordStrength(password: string) {
  if (!password) {
    return "";
  }

  const checks = getPasswordChecks(password);

  const score = Object.values(checks).filter(Boolean).length;

  if (score <= 2) {
    return "Weak";
  }

  if (score <= 4) {
    return "Medium";
  }

  return "Strong";
}

/**
 * Generates a cryptographically random password
 * that satisfies our current password policy.
 */
function generateStrongPassword() {
  const upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  const lower = "abcdefghijklmnopqrstuvwxyz";
  const numbers = "0123456789";
  const special = "@$!%*?&";

  const all = upper + lower + numbers + special;

  const getRandomIndex = (max: number) => {
    const random = new Uint32Array(1);
    crypto.getRandomValues(random);
    return random[0] % max;
  };

  const getRandomChar = (characters: string) => {
    return characters[getRandomIndex(characters.length)];
  };

  // Guarantee at least one character from every required category.
  const passwordCharacters = [
    getRandomChar(upper),
    getRandomChar(lower),
    getRandomChar(numbers),
    getRandomChar(special),
  ];

  // Fill the remaining characters up to 16 characters.
  while (passwordCharacters.length < 16) {
    passwordCharacters.push(getRandomChar(all));
  }

  // Securely shuffle the generated password.
  for (let i = passwordCharacters.length - 1; i > 0; i--) {
    const j = getRandomIndex(i + 1);

    [passwordCharacters[i], passwordCharacters[j]] = [
      passwordCharacters[j],
      passwordCharacters[i],
    ];
  }

  return passwordCharacters.join("");
}

export default function ResetPasswordPage() {
  const router = useRouter();
  const searchParams = useSearchParams();

  const token = searchParams.get("token");

  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const passwordChecks = getPasswordChecks(newPassword);
  const passwordStrength = getPasswordStrength(newPassword);

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>
  ) {
    event.preventDefault();

    setMessage("");
    setError("");

    // Check whether reset token exists.
    if (!token) {
      setError("Invalid or missing password reset token.");
      return;
    }

    // Check password length.
    if (
      newPassword.length < 12 ||
      newPassword.length > 64
    ) {
      setError(
        "Password must be between 12 and 64 characters."
      );
      return;
    }

    // Check uppercase.
    if (!passwordChecks.uppercase) {
      setError(
        "Password must contain at least one uppercase letter."
      );
      return;
    }

    // Check lowercase.
    if (!passwordChecks.lowercase) {
      setError(
        "Password must contain at least one lowercase letter."
      );
      return;
    }

    // Check number.
    if (!passwordChecks.number) {
      setError(
        "Password must contain at least one number."
      );
      return;
    }

    // Check special character.
    if (!passwordChecks.special) {
      setError(
        "Password must contain at least one special character."
      );
      return;
    }

    // Check confirmation password.
    if (newPassword !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setLoading(true);

    try {
      const result = await resetPassword({
        token,
        newPassword,
      });

      setMessage(
        result.message ||
          "Password reset successfully. You can now log in."
      );

      setNewPassword("");
      setConfirmPassword("");

      setTimeout(() => {
        router.push("/");
      }, 2000);

    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to reset password."
      );
    } finally {
      setLoading(false);
    }
  }

  function handleGeneratePassword() {
    const generatedPassword = generateStrongPassword();

    setNewPassword(generatedPassword);
    setConfirmPassword(generatedPassword);

    setError("");
    setMessage("");
  }

  return (
    <main className="min-h-screen bg-slate-100 flex items-center justify-center px-4">
      <div className="w-full max-w-md">

        {/* Heading */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-blue-600 text-white text-2xl font-bold shadow-lg">
            TR
          </div>

          <h1 className="mt-5 text-3xl font-bold text-slate-900">
            Reset Password
          </h1>

          <p className="mt-2 text-slate-500">
            Enter your new password below.
          </p>
        </div>

        {/* Reset Password Card */}
        <div className="bg-white rounded-2xl shadow-xl p-8">

          <form
            onSubmit={handleSubmit}
            className="space-y-5"
          >

            {/* New Password */}
            <div>
              <label
                htmlFor="newPassword"
                className="block text-sm font-medium text-slate-700 mb-2"
              >
                New Password
              </label>

              <div className="relative">
                <input
                  id="newPassword"
                  type={
                    showNewPassword
                      ? "text"
                      : "password"
                  }
                  value={newPassword}
                  onChange={(event) =>
                    setNewPassword(event.target.value)
                  }
                  placeholder="Enter new password"
                  required
                  disabled={loading}
                  className="w-full rounded-lg border border-slate-300 px-4 py-3 pr-16 text-slate-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-200 disabled:bg-slate-100"
                />

                <button
                  type="button"
                  onClick={() =>
                    setShowNewPassword(
                      !showNewPassword
                    )
                  }
                  disabled={loading}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-sm font-medium text-blue-600 hover:text-blue-800"
                >
                  {showNewPassword
                    ? "Hide"
                    : "Show"}
                </button>
              </div>

              {/* Password Strength */}
              {newPassword && (
                <div className="mt-3">

                  <div className="flex items-center justify-between mb-2">
                    <span className="text-sm font-medium text-slate-700">
                      Password strength
                    </span>

                    <span
                      className={
                        passwordStrength === "Strong"
                          ? "text-sm font-semibold text-green-600"
                          : passwordStrength ===
                              "Medium"
                            ? "text-sm font-semibold text-yellow-600"
                            : "text-sm font-semibold text-red-600"
                      }
                    >
                      {passwordStrength}
                    </span>
                  </div>

                  {/* Strength bar */}
                  <div className="w-full h-2 rounded-full bg-slate-200 overflow-hidden">
                    <div
                      className={
                        passwordStrength === "Strong"
                          ? "h-full w-full bg-green-500 transition-all"
                          : passwordStrength ===
                              "Medium"
                            ? "h-full w-2/3 bg-yellow-500 transition-all"
                            : "h-full w-1/3 bg-red-500 transition-all"
                      }
                    />
                  </div>

                  {/* Requirements */}
                  <div className="mt-4 rounded-lg bg-slate-50 border border-slate-200 p-4">
                    <p className="text-sm font-semibold text-slate-700 mb-2">
                      Password requirements
                    </p>

                    <p
                      className={
                        passwordChecks.length
                          ? "text-green-600 text-sm"
                          : "text-red-500 text-sm"
                      }
                    >
                      {passwordChecks.length
                        ? "✓"
                        : "✗"}{" "}
                      12-64 characters
                    </p>

                    <p
                      className={
                        passwordChecks.uppercase
                          ? "text-green-600 text-sm"
                          : "text-red-500 text-sm"
                      }
                    >
                      {passwordChecks.uppercase
                        ? "✓"
                        : "✗"}{" "}
                      One uppercase letter
                    </p>

                    <p
                      className={
                        passwordChecks.lowercase
                          ? "text-green-600 text-sm"
                          : "text-red-500 text-sm"
                      }
                    >
                      {passwordChecks.lowercase
                        ? "✓"
                        : "✗"}{" "}
                      One lowercase letter
                    </p>

                    <p
                      className={
                        passwordChecks.number
                          ? "text-green-600 text-sm"
                          : "text-red-500 text-sm"
                      }
                    >
                      {passwordChecks.number
                        ? "✓"
                        : "✗"}{" "}
                      One number
                    </p>

                    <p
                      className={
                        passwordChecks.special
                          ? "text-green-600 text-sm"
                          : "text-red-500 text-sm"
                      }
                    >
                      {passwordChecks.special
                        ? "✓"
                        : "✗"}{" "}
                      One special character
                    </p>
                  </div>

                  {/* Generate Strong Password */}
                  <button
                    type="button"
                    onClick={handleGeneratePassword}
                    disabled={loading}
                    className="mt-3 text-sm font-semibold text-blue-600 hover:text-blue-800 hover:underline disabled:opacity-50"
                  >
                    Generate strong password
                  </button>
                </div>
              )}
            </div>

            {/* Confirm Password */}
            <div>
              <label
                htmlFor="confirmPassword"
                className="block text-sm font-medium text-slate-700 mb-2"
              >
                Confirm Password
              </label>

              <div className="relative">
                <input
                  id="confirmPassword"
                  type={
                    showConfirmPassword
                      ? "text"
                      : "password"
                  }
                  value={confirmPassword}
                  onChange={(event) =>
                    setConfirmPassword(
                      event.target.value
                    )
                  }
                  placeholder="Confirm new password"
                  required
                  disabled={loading}
                  className="w-full rounded-lg border border-slate-300 px-4 py-3 pr-16 text-slate-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-200 disabled:bg-slate-100"
                />

                <button
                  type="button"
                  onClick={() =>
                    setShowConfirmPassword(
                      !showConfirmPassword
                    )
                  }
                  disabled={loading}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-sm font-medium text-blue-600 hover:text-blue-800"
                >
                  {showConfirmPassword
                    ? "Hide"
                    : "Show"}
                </button>
              </div>
            </div>

            {/* Error */}
            {error && (
              <div className="rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
                {error}
              </div>
            )}

            {/* Success */}
            {message && (
              <div className="rounded-lg bg-green-50 border border-green-200 px-4 py-3 text-sm text-green-700">
                {message}
              </div>
            )}

            {/* Submit */}
            <button
              type="submit"
              disabled={
                loading || !token
              }
              className="w-full rounded-lg bg-blue-600 px-4 py-3 font-semibold text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {loading
                ? "Updating Password..."
                : "Reset Password"}
            </button>

          </form>

          {/* Back to Login */}
          <div className="text-center mt-6">
            <button
              type="button"
              onClick={() => router.push("/")}
              className="text-sm text-blue-600 hover:text-blue-800 hover:underline"
            >
              ← Back to Login
            </button>
          </div>

        </div>
      </div>
    </main>
  );
}