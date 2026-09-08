"use client";

import { FormEvent, useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { login as apiLogin } from "@/lib/api";
import VirtualKeyboard from "@/components/VirtualKeyboard";

export default function Home() {
  const router = useRouter();

  const [usernameOrEmail, setUsernameOrEmail] = useState("");
  const [password, setPassword] = useState("");

  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  // ============================================================
  // VIRTUAL KEYBOARD
  // ============================================================

  const [showVirtualKeyboard, setShowVirtualKeyboard] =
    useState(false);

  // ============================================================
  // DEVELOPMENT TOKEN DISPLAY
  // ============================================================

  const [accessToken, setAccessToken] = useState("");
  const [refreshToken, setRefreshToken] = useState("");

  const [showAccessToken, setShowAccessToken] = useState(false);
  const [showRefreshToken, setShowRefreshToken] = useState(false);

  // ============================================================
  // GOOGLE OAUTH ERROR
  // ============================================================

  useEffect(() => {
    const params = new URLSearchParams(
      window.location.search
    );

    const oauthError = params.get("oauthError");

    if (oauthError === "google_login_failed") {
      setMessage("Google sign-in failed.");

      // Remove oauthError from URL
      window.history.replaceState(
        {},
        document.title,
        window.location.pathname
      );
    }
  }, []);

  // ============================================================
  // VIRTUAL KEYBOARD FUNCTIONS
  // ============================================================

  const handleVirtualKeyPress = (key: string) => {
    setPassword(
      (currentPassword) =>
        currentPassword + key
    );
  };

  const handleVirtualBackspace = () => {
    setPassword(
      (currentPassword) =>
        currentPassword.slice(0, -1)
    );
  };

  const handleVirtualClear = () => {
    setPassword("");
  };

  const handleVirtualEnter = () => {
    setShowVirtualKeyboard(false);
  };

  // ============================================================
  // NORMAL LOGIN
  // ============================================================

  async function handleLogin(
    event: FormEvent<HTMLFormElement>
  ) {
    event.preventDefault();

    setLoading(true);
    setMessage("");

    try {
      const result = await apiLogin({
        usernameOrEmail,
        password,
      });

      // --------------------------------------------------------
      // Check backend response
      // --------------------------------------------------------

      if (!result.success) {
        setMessage(
          result.message ||
            "Login failed"
        );

        return;
      }

      // --------------------------------------------------------
      // Get tokens
      // --------------------------------------------------------

      const newAccessToken =
        result.data.token;

      const newRefreshToken =
        result.data.refreshToken;

      // --------------------------------------------------------
      // Save access token
      // --------------------------------------------------------

      localStorage.setItem(
        "token",
        newAccessToken
      );

      localStorage.setItem(
        "username",
        result.data.username
      );

      localStorage.setItem(
        "role",
        result.data.role
      );

      // --------------------------------------------------------
      // DEMO ONLY:
      // Store tokens for development/demo display.
      //
      // Refresh token is also stored in sessionStorage so that
      // the dashboard idle-timeout can use it.
      //
      // IMPORTANT:
      // Do not use browser storage for refresh tokens in
      // production. Use secure HttpOnly cookies/server-side
      // session handling instead.
      // --------------------------------------------------------

      setAccessToken(newAccessToken);
      setRefreshToken(newRefreshToken);

      sessionStorage.setItem(
        "demoRefreshToken",
        newRefreshToken
      );

      // --------------------------------------------------------
      // Show tokens in browser console
      // --------------------------------------------------------

      console.log(
        "========================================"
      );

      console.log(
        "         AUTHENTICATION TOKENS"
      );

      console.log(
        "========================================"
      );

      console.log(
        "Access Token:",
        newAccessToken
      );

      console.log(
        "Refresh Token:",
        newRefreshToken
      );

      console.log(
        "Username:",
        result.data.username
      );

      console.log(
        "Role:",
        result.data.role
      );

      console.log(
        "========================================"
      );

      // --------------------------------------------------------
      // Redirect to dashboard
      // --------------------------------------------------------

      router.push("/dashboard");

    } catch (error) {
      console.error(error);

      const errorMessage =
        error instanceof Error
          ? error.message
          : "An error occurred";

      setMessage(
        errorMessage.includes(
          "Failed to fetch"
        ) ||
        errorMessage.includes(
          "Cannot connect"
        )
          ? "Cannot connect to the backend. Make sure Spring Boot is running on port 8080."
          : errorMessage
      );

    } finally {
      setLoading(false);
    }
  }

  // ============================================================
  // GOOGLE OAUTH2 LOGIN
  // ============================================================

  function handleGoogleLogin() {
    setMessage("");

    // Start Google OAuth in the SAME browser window.
    //
    // Flow:
    //
    // Next.js
    //    ↓
    // Spring Boot /oauth2/authorization/google
    //    ↓
    // Google
    //    ↓
    // Spring Boot callback
    //    ↓
    // /oauth2/success
    //    ↓
    // Dashboard

    window.location.href =
      "http://localhost:8080/oauth2/authorization/google";
  }

  return (
    <main className="min-h-screen bg-slate-100 flex items-center justify-center px-4 py-10">

      <div className="w-full max-w-md">

        {/* ======================================================
            LOGO / HEADING
        ====================================================== */}

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

        {/* ======================================================
            LOGIN CARD
        ====================================================== */}

        <div className="bg-white rounded-2xl shadow-xl p-8">

          <h2 className="text-2xl font-semibold text-slate-900">
            Welcome back 👋
          </h2>

          <p className="mt-1 text-sm text-slate-500">
            Please enter your credentials to continue.
          </p>

          <form
            onSubmit={handleLogin}
            className="mt-6 space-y-5"
          >

            {/* ==================================================
                USERNAME
            ================================================== */}

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
                onChange={(event) =>
                  setUsernameOrEmail(
                    event.target.value
                  )
                }
                placeholder="Enter username or email"
                required
                disabled={loading}
                className="w-full rounded-lg border border-slate-300 px-4 py-3 text-slate-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-200 disabled:bg-slate-100 disabled:cursor-not-allowed"
              />

            </div>

            {/* ==================================================
                PASSWORD
            ================================================== */}

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
                onChange={(event) =>
                  setPassword(
                    event.target.value
                  )
                }
                onFocus={() =>
                  setShowVirtualKeyboard(true)
                }
                placeholder="Enter password"
                required
                disabled={loading}
                className="w-full rounded-lg border border-slate-300 px-4 py-3 text-slate-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-200 disabled:bg-slate-100 disabled:cursor-not-allowed"
              />

              {/* ==================================================
                  VIRTUAL KEYBOARD
              ================================================== */}

              {showVirtualKeyboard && (
                <VirtualKeyboard
                  onKeyPress={
                    handleVirtualKeyPress
                  }
                  onBackspace={
                    handleVirtualBackspace
                  }
                  onClear={
                    handleVirtualClear
                  }
                  onEnter={
                    handleVirtualEnter
                  }
                />
              )}

            </div>

            {/* ==================================================
                FORGOT PASSWORD
            ================================================== */}

            <div className="flex justify-end -mt-2">

              <Link
                href="/forgot-password"
                className="text-sm font-medium text-blue-600 hover:text-blue-800 hover:underline"
              >
                Forgot password?
              </Link>

            </div>

            {/* ==================================================
                MESSAGE
            ================================================== */}

            {message && (
              <div className="rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
                {message}
              </div>
            )}

            {/* ==================================================
                SIGN IN BUTTON
            ================================================== */}

            <button
              type="submit"
              disabled={loading}
              className="w-full rounded-lg bg-blue-600 px-4 py-3 font-semibold text-white transition hover:bg-blue-700 cursor-pointer disabled:cursor-not-allowed disabled:opacity-60"
            >
              {loading
                ? "Signing in..."
                : "Sign In"}
            </button>

          </form>

          {/* ======================================================
              DEVELOPMENT TOKEN DISPLAY
          ====================================================== */}

          {accessToken &&
            refreshToken && (

            <div className="mt-6 rounded-xl border border-yellow-300 bg-yellow-50 p-4">

              <h3 className="text-sm font-bold text-yellow-800">
                Development Token Details
              </h3>

              <p className="mt-1 text-xs text-yellow-700">
                Demo only — remove this before production.
              </p>

              {/* ==================================================
                  ACCESS TOKEN
              ================================================== */}

              <div className="mt-4">

                <div className="flex items-center justify-between">

                  <p className="text-xs font-semibold text-slate-700">
                    Access Token
                  </p>

                  <button
                    type="button"
                    onClick={() =>
                      setShowAccessToken(
                        !showAccessToken
                      )
                    }
                    className="text-xs font-semibold text-blue-600 hover:underline cursor-pointer"
                  >
                    {showAccessToken
                      ? "Hide"
                      : "Show"}
                  </button>

                </div>

                <div className="mt-2 rounded-lg bg-white border border-slate-200 p-3">

                  <p className="text-xs break-all text-slate-600 font-mono">

                    {showAccessToken
                      ? accessToken
                      : `${accessToken.slice(
                          0,
                          25
                        )}...`}

                  </p>

                </div>

              </div>

              {/* ==================================================
                  REFRESH TOKEN
              ================================================== */}

              <div className="mt-4">

                <div className="flex items-center justify-between">

                  <p className="text-xs font-semibold text-slate-700">
                    Refresh Token
                  </p>

                  <button
                    type="button"
                    onClick={() =>
                      setShowRefreshToken(
                        !showRefreshToken
                      )
                    }
                    className="text-xs font-semibold text-blue-600 hover:underline cursor-pointer"
                  >
                    {showRefreshToken
                      ? "Hide"
                      : "Show"}
                  </button>

                </div>

                <div className="mt-2 rounded-lg bg-white border border-slate-200 p-3">

                  <p className="text-xs break-all text-slate-600 font-mono">

                    {showRefreshToken
                      ? refreshToken
                      : `${refreshToken.slice(
                          0,
                          25
                        )}...`}

                  </p>

                </div>

              </div>

            </div>

          )}

          {/* ======================================================
              DIVIDER
          ====================================================== */}

          <div className="flex items-center my-6">

            <div className="flex-1 border-t border-slate-200"></div>

            <span className="px-4 text-sm text-slate-400">
              OR
            </span>

            <div className="flex-1 border-t border-slate-200"></div>

          </div>

          {/* ======================================================
              GOOGLE LOGIN
          ====================================================== */}

          <button
            type="button"
            onClick={handleGoogleLogin}
            className="w-full rounded-lg border border-slate-300 bg-white px-4 py-3 font-semibold text-slate-700 transition hover:bg-slate-50 cursor-pointer flex items-center justify-center gap-3"
          >

            <span className="text-lg font-bold">
              G
            </span>

            Continue with Google

          </button>

          {/* ======================================================
              SIGN UP LINK
          ====================================================== */}

          <div className="mt-6 text-center">

            <span className="text-sm text-slate-500">
              Don't have an account?{" "}
            </span>

            <Link
              href="/register"
              className="text-sm font-semibold text-blue-600 hover:text-blue-800 hover:underline"
            >
              Sign Up
            </Link>

          </div>

        </div>

        {/* ======================================================
            FOOTER
        ====================================================== */}

        <p className="mt-6 text-center text-xs text-slate-400">
          Talent Recruitment Management System
        </p>

      </div>

    </main>
  );
}