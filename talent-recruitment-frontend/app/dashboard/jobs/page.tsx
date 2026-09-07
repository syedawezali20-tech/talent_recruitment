"use client";

import { useEffect, useState } from "react";
import {
  getJobs,
  deleteJob,
  JobData,
} from "@/lib/api";

export default function JobsPage() {
  // ============================================================
  // STATE
  // ============================================================

  const [jobs, setJobs] = useState<JobData[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Filters
  const [department, setDepartment] = useState("");
  const [location, setLocation] = useState("");
  const [status, setStatus] = useState("");

  // Sorting
  const [sortBy, setSortBy] = useState("createdAt");
  const [direction, setDirection] = useState("desc");

  // Pagination
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // ============================================================
  // LOAD JOBS
  // ============================================================

  const loadJobs = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await getJobs({
        page,
        size,
        department: department || undefined,
        location: location || undefined,
        status: status || undefined,
        sortBy,
        direction,
      });

      setJobs(response.data || []);
      setTotalPages(response.totalPages || 0);
      setTotalElements(response.totalElements || 0);
    } catch (err) {
      console.error("Failed to load jobs:", err);

      if (err instanceof Error) {
        if (err.message === "UNAUTHORIZED") {
          setError("You are not authorized. Please login again.");
        } else if (err.message === "FORBIDDEN") {
          setError("You don't have permission to view jobs.");
        } else {
          setError(err.message);
        }
      } else {
        setError("Failed to load jobs");
      }
    } finally {
      setLoading(false);
    }
  };

  // ============================================================
  // LOAD WHEN FILTERS / PAGINATION CHANGE
  // ============================================================

  useEffect(() => {
    loadJobs();
  }, [page, size, department, location, status, sortBy, direction]);

  // ============================================================
  // DELETE JOB
  // ============================================================

  const handleDelete = async (id: number) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this job?"
    );

    if (!confirmed) {
      return;
    }

    try {
      await deleteJob(id);

      // Reload jobs after deletion
      await loadJobs();
    } catch (err) {
      console.error("Failed to delete job:", err);

      if (err instanceof Error) {
        if (err.message === "FORBIDDEN") {
          alert("You don't have permission to delete jobs.");
        } else if (err.message === "UNAUTHORIZED") {
          alert("Your session has expired. Please login again.");
        } else {
          alert(err.message);
        }
      } else {
        alert("Failed to delete job");
      }
    }
  };

  // ============================================================
  // RESET FILTERS
  // ============================================================

  const handleReset = () => {
    setDepartment("");
    setLocation("");
    setStatus("");
    setSortBy("createdAt");
    setDirection("desc");
    setPage(0);
  };

  // ============================================================
  // LOADING
  // ============================================================

  if (loading) {
    return (
      <div className="p-8">
        <h1 className="text-2xl font-bold mb-4">Jobs</h1>
        <p>Loading jobs...</p>
      </div>
    );
  }

  // ============================================================
  // PAGE
  // ============================================================

  return (
    <div className="p-8">

      {/* ======================================================
          HEADER
      ====================================================== */}

      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-3xl font-bold">
            Jobs
          </h1>

          <p className="text-gray-500 mt-1">
            Manage job openings and positions
          </p>
        </div>

        <button
          onClick={() => {
            // We will connect this to the Create Job form next.
            alert("Create Job form will be added next.");
          }}
          className="px-5 py-2 rounded-lg bg-blue-600 text-white font-medium hover:bg-blue-700"
        >
          + Create Job
        </button>
      </div>

      {/* ======================================================
          ERROR
      ====================================================== */}

      {error && (
        <div className="mb-6 rounded-lg border border-red-300 bg-red-50 p-4 text-red-700">
          {error}
        </div>
      )}

      {/* ======================================================
          FILTERS
      ====================================================== */}

      <div className="bg-white border rounded-xl p-5 mb-6">

        <h2 className="text-lg font-semibold mb-4">
          Filters
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">

          {/* Department */}

          <div>
            <label className="block text-sm font-medium mb-1">
              Department
            </label>

            <input
              type="text"
              value={department}
              onChange={(e) => {
                setDepartment(e.target.value);
                setPage(0);
              }}
              placeholder="e.g. IT"
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>

          {/* Location */}

          <div>
            <label className="block text-sm font-medium mb-1">
              Location
            </label>

            <input
              type="text"
              value={location}
              onChange={(e) => {
                setLocation(e.target.value);
                setPage(0);
              }}
              placeholder="e.g. Hyderabad"
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>

          {/* Status */}

          <div>
            <label className="block text-sm font-medium mb-1">
              Status
            </label>

            <select
              value={status}
              onChange={(e) => {
                setStatus(e.target.value);
                setPage(0);
              }}
              className="w-full border rounded-lg px-3 py-2"
            >
              <option value="">All Statuses</option>
              <option value="OPEN">OPEN</option>
              <option value="CLOSED">CLOSED</option>
              <option value="DRAFT">DRAFT</option>
            </select>
          </div>

        </div>

        {/* ==================================================
            SORTING
        ================================================== */}

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-4">

          <div>
            <label className="block text-sm font-medium mb-1">
              Sort By
            </label>

            <select
              value={sortBy}
              onChange={(e) => {
                setSortBy(e.target.value);
                setPage(0);
              }}
              className="w-full border rounded-lg px-3 py-2"
            >
              <option value="createdAt">
                Created At
              </option>

              <option value="title">
                Title
              </option>

              <option value="department">
                Department
              </option>

              <option value="location">
                Location
              </option>

              <option value="status">
                Status
              </option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">
              Direction
            </label>

            <select
              value={direction}
              onChange={(e) => {
                setDirection(e.target.value);
                setPage(0);
              }}
              className="w-full border rounded-lg px-3 py-2"
            >
              <option value="asc">
                Ascending
              </option>

              <option value="desc">
                Descending
              </option>
            </select>
          </div>

          <div className="flex items-end">
            <button
              onClick={handleReset}
              className="w-full border rounded-lg px-3 py-2 hover:bg-gray-100"
            >
              Reset Filters
            </button>
          </div>

        </div>

      </div>

      {/* ======================================================
          TOTAL
      ====================================================== */}

      <div className="mb-4 text-sm text-gray-500">
        Total Jobs: {totalElements}
      </div>

      {/* ======================================================
          JOB LIST
      ====================================================== */}

      {jobs.length === 0 ? (

        <div className="bg-white border rounded-xl p-10 text-center">
          <h2 className="text-xl font-semibold">
            No jobs found
          </h2>

          <p className="text-gray-500 mt-2">
            Try changing your filters or create a new job.
          </p>
        </div>

      ) : (

        <div className="space-y-4">

          {jobs.map((job) => (

            <div
              key={job.id}
              className="bg-white border rounded-xl p-5 hover:shadow-md transition"
            >

              {/* Job Header */}

              <div className="flex items-start justify-between">

                <div>
                  <h2 className="text-xl font-semibold">
                    {job.title}
                  </h2>

                  <p className="text-gray-500 mt-1">
                    {job.department} • {job.location}
                  </p>
                </div>

                <span
                  className={`px-3 py-1 rounded-full text-sm font-medium ${
                    job.status === "OPEN"
                      ? "bg-green-100 text-green-700"
                      : job.status === "CLOSED"
                      ? "bg-red-100 text-red-700"
                      : "bg-gray-100 text-gray-700"
                  }`}
                >
                  {job.status}
                </span>

              </div>

              {/* Description */}

              <p className="text-gray-600 mt-4">
                {job.description}
              </p>

              {/* Job Details */}

              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-5">

                <div>
                  <p className="text-xs text-gray-500">
                    Experience
                  </p>

                  <p className="font-medium">
                    {job.experienceRequired} years
                  </p>
                </div>

                <div>
                  <p className="text-xs text-gray-500">
                    Employment Type
                  </p>

                  <p className="font-medium">
                    {job.employmentType}
                  </p>
                </div>

                <div>
                  <p className="text-xs text-gray-500">
                    Created
                  </p>

                  <p className="font-medium">
                    {new Date(job.createdAt).toLocaleDateString()}
                  </p>
                </div>

                <div>
                  <p className="text-xs text-gray-500">
                    Job ID
                  </p>

                  <p className="font-medium">
                    #{job.id}
                  </p>
                </div>

              </div>

              {/* Actions */}

              <div className="flex justify-end gap-3 mt-5 pt-4 border-t">

                <button
                  onClick={() => {
                    alert(
                      `Edit Job ${job.id} will be implemented next.`
                    );
                  }}
                  className="px-4 py-2 border rounded-lg hover:bg-gray-100"
                >
                  Edit
                </button>

                <button
                  onClick={() => handleDelete(job.id)}
                  className="px-4 py-2 rounded-lg bg-red-600 text-white hover:bg-red-700"
                >
                  Delete
                </button>

              </div>

            </div>

          ))}

        </div>

      )}

      {/* ======================================================
          PAGINATION
      ====================================================== */}

      {totalPages > 0 && (
        <div className="flex items-center justify-between mt-8">

          <button
            disabled={page === 0}
            onClick={() => setPage((previous) => previous - 1)}
            className="px-4 py-2 border rounded-lg disabled:opacity-50 disabled:cursor-not-allowed"
          >
            ← Previous
          </button>

          <span className="text-sm text-gray-600">
            Page {page + 1} of {totalPages}
          </span>

          <button
            disabled={page >= totalPages - 1}
            onClick={() => setPage((previous) => previous + 1)}
            className="px-4 py-2 border rounded-lg disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Next →
          </button>

        </div>
      )}

    </div>
  );
}