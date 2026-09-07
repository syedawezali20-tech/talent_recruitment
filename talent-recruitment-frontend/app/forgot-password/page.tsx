"use client";

import { FormEvent, useState } from "react";
import Link from "next/link";
import { forgotPassword } from "@/lib/api";

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (
    event: FormEvent<HTMLFormElement>
  ) => {
    event.preventDefault();

    setMessage("");
    setError("");
    setLoading(true);

    try {
      const response = await forgotPassword({
        email,
      });

      // Registered email
      setMessage(
        response.message ||
          "Password reset link has been sent to your email."
      );

      setEmail("");

    } catch (err) {

      // Unregistered email or other backend error
      setError(
        err instanceof Error
          ? err.message
          : "Something went wrong. Please try again."
      );

    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="min-h-screen flex items-center justify-center bg-gray-100 px-4">

      <div className="w-full max-w-md bg-white rounded-lg shadow-md p-8">

        {/* Heading */}
        <h1 className="text-2xl font-bold text-center mb-2">
          Forgot Password
        </h1>

        <p className="text-gray-600 text-center mb-6">
          Enter your email address and we will send you a
          password reset link.
        </p>

        {/* Form */}
        <form
          onSubmit={handleSubmit}
          className="space-y-5"
        >

          {/* Email */}
          <div>
            <label
              htmlFor="email"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              Email Address
            </label>

            <input
              id="email"
              type="email"
              value={email}
              onChange={(event) =>
                setEmail(event.target.value)
              }
              placeholder="Enter your email"
              required
              disabled={loading}
              className="w-full border border-gray-300 rounded-md px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
            />
          </div>

          {/* Submit */}
          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 disabled:bg-gray-400"
          >
            {loading
              ? "Sending..."
              : "Send Reset Link"}
          </button>

        </form>


        {/* Success Message */}
        {message && (
          <div className="mt-5 p-3 bg-green-100 text-green-700 rounded-md">
            {message}
          </div>
        )}


        {/* Error Message */}
        {error && (
          <div className="mt-5 p-3 bg-red-100 text-red-700 rounded-md">
            {error}
          </div>
        )}


        {/* Back to Login */}
        <div className="mt-6 text-center">
          <Link
            href="/"
            className="text-sm text-blue-600 hover:text-blue-800 hover:underline"
          >
            ← Back to Login
          </Link>
        </div>

      </div>

    </main>
  );
}