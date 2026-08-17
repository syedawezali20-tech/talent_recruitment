"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { login as apiLogin } from "@/lib/api";

export default function Home() {
  const router = useRouter();
  const [usernameOrEmail, setUsernameOrEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleLogin(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    setLoading(true);
    setMessage("");

    try {
      const result = await apiLogin({
        usernameOrEmail,
        password,
      });

      if (!result.success) {
        setMessage(result.message || "Login failed");
        return;
      }

      // Save JWT token
      localStorage.setItem("token", result.data.token);
      localStorage.setItem("username", result.data.username);
      localStorage.setItem("role", result.data.role);

      // Redirect to dashboard
      router.push("/dashboard");
    } catch (error) {
      console.error(error);
      const errorMessage =
        error instanceof Error ? error.message : "An error occurred";
      setMessage(
        errorMessage.includes("Failed to fetch") ||
          errorMessage.includes("Cannot connect")
          ? "Cannot connect to the backend. Make sure Spring Boot is running on port 8080."
          : errorMessage
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="min-h-screen bg-slate-100 flex items-center justify-center px-4">
      <div className="w-full max-w-md">
        {/* Logo / Heading */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-blue-600 text-white text-2xl font-bold shadow-lg">
            TR
          </div>

          <h1 className="mt-5 text-3xl font-bold text-slate-900">
            Talent Recruitment
          </h1>

          <p className="mt-2 text-slate-500">
            Sign in to manage your recruitment process
          </p>
        </div>

        {/* Login Card */}
        <div className="bg-white rounded-2xl shadow-xl p-8">
          <h2 className="text-2xl font-semibold text-slate-900">
            Welcome back 👋
          </h2>

          <p className="mt-1 text-sm text-slate-500">
            Please enter your credentials to continue.
          </p>

          <form onSubmit={handleLogin} className="mt-6 space-y-5">
            {/* Username */}
            <div>
              <label
                htmlFor="username"
                className="block text-sm font-medium text-slate-700 mb-2"
              >
                Username or Email
              </label>

              <input
                id="username"
                type="text"
                value={usernameOrEmail}
                onChange={(event) => setUsernameOrEmail(event.target.value)}
                placeholder="Enter username or email"
                required
                disabled={loading}
                className="w-full rounded-lg border border-slate-300 px-4 py-3 text-slate-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-200 disabled:bg-slate-100 disabled:cursor-not-allowed"
              />
            </div>

            {/* Password */}
            <div>
              <label
                htmlFor="password"
                className="block text-sm font-medium text-slate-700 mb-2"
              >
                Password
              </label>

              <input
                id="password"
                type="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                placeholder="Enter password"
                required
                disabled={loading}
                className="w-full rounded-lg border border-slate-300 px-4 py-3 text-slate-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-200 disabled:bg-slate-100 disabled:cursor-not-allowed"
              />
            </div>

            {/* Error Message */}
            {message && (
              <div className="rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
                {message}
              </div>
            )}

            {/* Login Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full rounded-lg bg-blue-600 px-4 py-3 font-semibold text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {loading ? "Signing in..." : "Sign In"}
            </button>
          </form>
        </div>

        <p className="mt-6 text-center text-xs text-slate-400">
          Talent Recruitment Management System
        </p>
      </div>
    </main>
  );
}