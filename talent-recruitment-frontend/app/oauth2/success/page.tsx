"use client";

import { useEffect, useRef } from "react";
import { useRouter } from "next/navigation";

export default function OAuth2SuccessPage() {
    const router = useRouter();

    // Prevent the callback from being processed more than once.
    const hasProcessedCallback = useRef(false);

    useEffect(() => {
        // ============================================================
        // 1. PREVENT DUPLICATE PROCESSING
        // ============================================================

        if (hasProcessedCallback.current) {
            return;
        }

        hasProcessedCallback.current = true;

        // ============================================================
        // 2. READ THE CURRENT URL
        // ============================================================

        const currentUrl = window.location.href;

        console.log(
            "========================================"
        );

        console.log(
            "GOOGLE OAUTH2 CALLBACK"
        );

        console.log(
            "Current URL:",
            currentUrl
        );

        console.log(
            "========================================"
        );

        // ============================================================
        // 3. READ QUERY PARAMETERS
        //
        // Expected:
        //
        // /oauth2/success
        // ?token=...
        // &refreshToken=...
        // &username=...
        // &role=...
        // ============================================================

        const params =
            new URLSearchParams(
                window.location.search
            );

        // ============================================================
        // 4. GET ACCESS TOKEN
        // ============================================================

        const token =
            params.get("token");

        // ============================================================
        // 5. GET REFRESH TOKEN
        // ============================================================

        const refreshToken =
            params.get("refreshToken");

        // ============================================================
        // 6. GET USERNAME
        // ============================================================

        const username =
            params.get("username");

        // ============================================================
        // 7. GET ROLE
        // ============================================================

        const role =
            params.get("role");

        // ============================================================
        // 8. DEBUG
        // ============================================================

        console.log(
            "Access Token Found:",
            !!token
        );

        console.log(
            "Refresh Token Found:",
            !!refreshToken
        );

        console.log(
            "Username:",
            username
        );

        console.log(
            "Role:",
            role
        );

        // ============================================================
        // 9. VALIDATE ACCESS TOKEN
        // ============================================================

        if (!token) {
            console.error(
                "JWT access token not found"
            );

            // Don't continue without authentication.
            router.replace("/");
            return;
        }

        // ============================================================
        // 10. VALIDATE REFRESH TOKEN
        // ============================================================

        if (!refreshToken) {
            console.error(
                "Refresh token not found"
            );

            // Don't continue without a valid application session.
            router.replace("/");
            return;
        }

        // ============================================================
        // 11. SAVE ACCESS TOKEN
        // ============================================================

        localStorage.setItem(
            "token",
            token
        );

        // ============================================================
        // 12. SAVE USERNAME
        // ============================================================

        if (username) {
            localStorage.setItem(
                "username",
                username
            );
        }

        // ============================================================
        // 13. SAVE ROLE
        // ============================================================

        if (role) {
            localStorage.setItem(
                "role",
                role
            );
        }

        // ============================================================
        // 14. SAVE REFRESH TOKEN
        //
        // DEMO ONLY
        //
        // Dashboard uses this for:
        // 30-second idle logout
        //        ↓
        // POST /api/auth/logout
        // ============================================================

        sessionStorage.setItem(
            "demoRefreshToken",
            refreshToken
        );

        // ============================================================
        // 15. SUCCESS LOG
        // ============================================================

        console.log(
            "========================================"
        );

        console.log(
            "GOOGLE OAUTH2 LOGIN SUCCESS"
        );

        console.log(
            "Access Token saved: true"
        );

        console.log(
            "Refresh Token saved: true"
        );

        console.log(
            "Username:",
            username
        );

        console.log(
            "Role:",
            role
        );

        console.log(
            "========================================"
        );

        // ============================================================
        // 16. REMOVE TOKENS FROM THE URL
        //
        // Example:
        //
        // BEFORE:
        // /oauth2/success?token=ABC&refreshToken=XYZ...
        //
        // AFTER:
        // /oauth2/success
        // ============================================================

        window.history.replaceState(
            {},
            document.title,
            window.location.pathname
        );

        // ============================================================
        // 17. GO TO DASHBOARD
        // ============================================================

        router.replace(
            "/dashboard"
        );

    }, [router]);

    // ================================================================
    // PAGE UI
    // ================================================================

    return (
        <div className="min-h-screen flex items-center justify-center bg-slate-100">

            <div className="rounded-xl bg-white px-8 py-6 shadow-lg text-center">

                <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-blue-100 text-blue-600">
                    🔐
                </div>

                <h2 className="text-xl font-semibold text-slate-900">
                    Completing Google Sign-In...
                </h2>

                <p className="mt-2 text-sm text-slate-500">
                    Please wait while authentication is completed.
                </p>

            </div>

        </div>
    );
}