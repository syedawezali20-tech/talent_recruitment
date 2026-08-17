"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";

export default function Dashboard() {
  const router = useRouter();
  const [username, setUsername] = useState<string | null>(null);
  const [role, setRole] = useState<string | null>(null);

  useEffect(() => {
    const storedUsername = localStorage.getItem("username");
    const storedRole = localStorage.getItem("role");
    setUsername(storedUsername);
    setRole(storedRole);
  }, []);

  return (
    <div>
      {/* Welcome Section */}
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-slate-900">
          Welcome, {username} 👋
        </h1>
        <p className="mt-2 text-slate-600">
          Role: <span className="font-semibold">{role}</span>
        </p>
      </div>

      {/* Dashboard Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Candidates Card */}
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-8 hover:shadow-md transition">
          <div className="inline-flex items-center justify-center w-12 h-12 rounded-lg bg-blue-100 text-blue-600 text-xl">
            👥
          </div>
          <h2 className="mt-4 text-2xl font-bold text-slate-900">Candidates</h2>
          <p className="mt-2 text-slate-600 text-sm">
            Manage all candidates and track their application status
          </p>
          <Link
            href="/dashboard/candidates"
            className="mt-6 inline-flex items-center px-4 py-2 rounded-lg bg-blue-600 text-white font-medium hover:bg-blue-700 transition"
          >
            View Candidates
            <span className="ml-2">→</span>
          </Link>
        </div>

        {/* Jobs Card */}
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-8 hover:shadow-md transition">
          <div className="inline-flex items-center justify-center w-12 h-12 rounded-lg bg-green-100 text-green-600 text-xl">
            💼
          </div>
          <h2 className="mt-4 text-2xl font-bold text-slate-900">Jobs</h2>
          <p className="mt-2 text-slate-600 text-sm">
            Manage job openings and recruitment requirements
          </p>
          <button
            disabled
            className="mt-6 inline-flex items-center px-4 py-2 rounded-lg bg-slate-100 text-slate-500 font-medium cursor-not-allowed"
          >
            View Jobs
            <span className="ml-2">→</span>
          </button>
          <p className="mt-2 text-xs text-slate-500">(Coming soon)</p>
        </div>

        {/* Recruitment Card */}
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-8 hover:shadow-md transition">
          <div className="inline-flex items-center justify-center w-12 h-12 rounded-lg bg-purple-100 text-purple-600 text-xl">
            🎯
          </div>
          <h2 className="mt-4 text-2xl font-bold text-slate-900">
            Recruitment
          </h2>
          <p className="mt-2 text-slate-600 text-sm">
            Monitor recruitment pipeline and hiring progress
          </p>
          <button
            disabled
            className="mt-6 inline-flex items-center px-4 py-2 rounded-lg bg-slate-100 text-slate-500 font-medium cursor-not-allowed"
          >
            View Pipeline
            <span className="ml-2">→</span>
          </button>
          <p className="mt-2 text-xs text-slate-500">(Coming soon)</p>
        </div>
      </div>

      {/* Quick Stats */}
      <div className="mt-12 bg-white rounded-xl shadow-sm border border-slate-200 p-8">
        <h2 className="text-xl font-bold text-slate-900 mb-6">Quick Stats</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="text-center">
            <p className="text-4xl font-bold text-blue-600">—</p>
            <p className="mt-2 text-slate-600">Total Candidates</p>
          </div>
          <div className="text-center">
            <p className="text-4xl font-bold text-green-600">—</p>
            <p className="mt-2 text-slate-600">Open Positions</p>
          </div>
          <div className="text-center">
            <p className="text-4xl font-bold text-purple-600">—</p>
            <p className="mt-2 text-slate-600">Pending Applications</p>
          </div>
        </div>
        <p className="mt-6 text-sm text-slate-500 text-center">
          Real-time data will be loaded from the backend
        </p>
      </div>
    </div>
  );
}
