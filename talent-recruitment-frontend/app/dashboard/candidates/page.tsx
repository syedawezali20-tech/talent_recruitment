"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import {
  getCandidates,
  createCandidate,
  updateCandidate,
  deleteCandidate,
} from "@/lib/api";

interface Candidate {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  skills: string;
  experience: number;
  resumeUrl: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}

interface PaginationInfo {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export default function CandidatesPage() {
  const router = useRouter();
  const [candidates, setCandidates] = useState<Candidate[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // Pagination and filtering
  const [pagination, setPagination] = useState<PaginationInfo>({
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
  });

  const [filters, setFilters] = useState({
    skill: "",
    status: "",
  });

  const [sortBy, setSortBy] = useState("id");
  const [direction, setDirection] = useState("asc");

  // Modal states
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedCandidate, setSelectedCandidate] = useState<Candidate | null>(
    null
  );

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    skills: "",
    experience: 0,
    resumeUrl: "",
    status: "ACTIVE",
  });

  // Fetch candidates
  const fetchCandidates = async (page = 0) => {
    setLoading(true);
    setError(null);

    try {
      const response = await getCandidates({
        page,
        size: pagination.size,
        skill: filters.skill || undefined,
        status: filters.status || undefined,
        sortBy,
        direction,
      });

      setCandidates(response.data);
      setPagination({
        page: response.page || 0,
        size: response.size || 10,
        totalElements: response.totalElements || 0,
        totalPages: response.totalPages || 0,
      });
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : "Failed to fetch candidates";

      if (errorMsg === "UNAUTHORIZED") {
        localStorage.removeItem("token");
        localStorage.removeItem("username");
        localStorage.removeItem("role");
        router.push("/");
        return;
      }

      setError(
        errorMsg.includes("Cannot connect")
          ? "Cannot connect to the backend. Make sure Spring Boot is running on port 8080."
          : errorMsg
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCandidates(0);
  }, [filters, sortBy, direction]);

  // Handle create
  const handleCreate = async () => {
    if (!formData.firstName || !formData.lastName || !formData.email) {
      setError("First name, last name, and email are required");
      return;
    }

    try {
      await createCandidate(formData);
      setSuccess("Candidate created successfully!");
      setShowCreateModal(false);
      resetForm();
      fetchCandidates(0);
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : "Failed to create candidate";
      setError(errorMsg);
    }
  };

  // Handle update
  const handleUpdate = async () => {
    if (!selectedCandidate) return;

    try {
      await updateCandidate(selectedCandidate.id, formData);
      setSuccess("Candidate updated successfully!");
      setShowEditModal(false);
      resetForm();
      fetchCandidates(pagination.page);
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : "Failed to update candidate";

      if (errorMsg === "FORBIDDEN") {
        setError(
          "You do not have permission to perform this action."
        );
      } else {
        setError(errorMsg);
      }
    }
  };

  // Handle delete
  const handleDelete = async () => {
    if (!selectedCandidate) return;

    try {
      await deleteCandidate(selectedCandidate.id);
      setSuccess("Candidate deleted successfully!");
      setShowDeleteModal(false);
      fetchCandidates(pagination.page);
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : "Failed to delete candidate";

      if (errorMsg === "FORBIDDEN") {
        setError(
          "You do not have permission to perform this action."
        );
      } else {
        setError(errorMsg);
      }
    }
  };

  const openCreateModal = () => {
    resetForm();
    setShowCreateModal(true);
  };

  const openEditModal = (candidate: Candidate) => {
    setSelectedCandidate(candidate);
    setFormData({
      firstName: candidate.firstName,
      lastName: candidate.lastName,
      email: candidate.email,
      phone: candidate.phone,
      skills: candidate.skills,
      experience: candidate.experience,
      resumeUrl: candidate.resumeUrl,
      status: candidate.status,
    });
    setShowEditModal(true);
  };

  const openDeleteModal = (candidate: Candidate) => {
    setSelectedCandidate(candidate);
    setShowDeleteModal(true);
  };

  const resetForm = () => {
    setFormData({
      firstName: "",
      lastName: "",
      email: "",
      phone: "",
      skills: "",
      experience: 0,
      resumeUrl: "",
      status: "ACTIVE",
    });
  };

  const handleFilterChange = () => {
    setPagination({ ...pagination, page: 0 });
  };

  const clearFilters = () => {
    setFilters({ skill: "", status: "" });
    setSortBy("id");
    setDirection("asc");
  };

  return (
    <div>
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-4xl font-bold text-slate-900">Candidates</h1>
          <p className="mt-2 text-slate-600">
            Total: {pagination.totalElements} candidates
          </p>
        </div>
        <button
          onClick={openCreateModal}
          className="px-6 py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition"
        >
          + Add Candidate
        </button>
      </div>

      {/* Error Message */}
      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg text-red-700">
          <button
            onClick={() => setError(null)}
            className="float-right text-red-700 hover:text-red-900"
          >
            ✕
          </button>
          {error}
        </div>
      )}

      {/* Success Message */}
      {success && (
        <div className="mb-6 p-4 bg-green-50 border border-green-200 rounded-lg text-green-700">
          <button
            onClick={() => setSuccess(null)}
            className="float-right text-green-700 hover:text-green-900"
          >
            ✕
          </button>
          {success}
        </div>
      )}

      {/* Filters */}
      <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-6 mb-6">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-2">
              Search Skill
            </label>
            <input
              type="text"
              placeholder="e.g., Java, Python"
              value={filters.skill}
              onChange={(e) =>
                setFilters({ ...filters, skill: e.target.value })
              }
              className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-2">
              Status
            </label>
            <select
              value={filters.status}
              onChange={(e) =>
                setFilters({ ...filters, status: e.target.value })
              }
              className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">All Status</option>
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
              <option value="HIRED">Hired</option>
              <option value="REJECTED">Rejected</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-2">
              Sort By
            </label>
            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="id">ID</option>
              <option value="firstName">First Name</option>
              <option value="createdAt">Created Date</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-2">
              Direction
            </label>
            <select
              value={direction}
              onChange={(e) => setDirection(e.target.value)}
              className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="asc">Ascending</option>
              <option value="desc">Descending</option>
            </select>
          </div>
        </div>

        <div className="flex gap-3 mt-4">
          <button
            onClick={handleFilterChange}
            className="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition"
          >
            Apply Filters
          </button>
          <button
            onClick={clearFilters}
            className="px-4 py-2 bg-slate-200 text-slate-700 text-sm font-medium rounded-lg hover:bg-slate-300 transition"
          >
            Clear
          </button>
        </div>
      </div>

      {/* Candidates Table */}
      <div className="bg-white rounded-lg shadow-sm border border-slate-200 overflow-hidden">
        {loading ? (
          <div className="flex items-center justify-center p-12">
            <div className="text-center">
              <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
              <p className="mt-4 text-slate-600">Loading candidates...</p>
            </div>
          </div>
        ) : candidates.length === 0 ? (
          <div className="flex items-center justify-center p-12">
            <div className="text-center">
              <p className="text-slate-600 text-lg">No candidates found</p>
              <button
                onClick={openCreateModal}
                className="mt-4 px-4 py-2 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition"
              >
                Add First Candidate
              </button>
            </div>
          </div>
        ) : (
          <>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-slate-50 border-b border-slate-200">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-slate-900">
                      ID
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-slate-900">
                      Name
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-slate-900">
                      Email
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-slate-900">
                      Phone
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-slate-900">
                      Skills
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-slate-900">
                      Experience
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-slate-900">
                      Status
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-slate-900">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-200">
                  {candidates.map((candidate) => (
                    <tr
                      key={candidate.id}
                      className="hover:bg-slate-50 transition"
                    >
                      <td className="px-6 py-4 text-sm text-slate-900">
                        #{candidate.id}
                      </td>
                      <td className="px-6 py-4 text-sm font-medium text-slate-900">
                        {candidate.firstName} {candidate.lastName}
                      </td>
                      <td className="px-6 py-4 text-sm text-slate-600">
                        {candidate.email}
                      </td>
                      <td className="px-6 py-4 text-sm text-slate-600">
                        {candidate.phone}
                      </td>
                      <td className="px-6 py-4 text-sm text-slate-600">
                        <div className="flex flex-wrap gap-1">
                          {candidate.skills.split(",").map((skill, idx) => (
                            <span
                              key={idx}
                              className="px-2 py-1 bg-blue-100 text-blue-700 text-xs rounded"
                            >
                              {skill.trim()}
                            </span>
                          ))}
                        </div>
                      </td>
                      <td className="px-6 py-4 text-sm text-slate-600">
                        {candidate.experience} yrs
                      </td>
                      <td className="px-6 py-4 text-sm">
                        <span
                          className={`px-2 py-1 rounded-full text-xs font-medium ${
                            candidate.status === "ACTIVE"
                              ? "bg-green-100 text-green-700"
                              : candidate.status === "HIRED"
                                ? "bg-blue-100 text-blue-700"
                                : candidate.status === "REJECTED"
                                  ? "bg-red-100 text-red-700"
                                  : "bg-slate-100 text-slate-700"
                          }`}
                        >
                          {candidate.status}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-sm space-x-2">
                        <button
                          onClick={() => openEditModal(candidate)}
                          className="text-blue-600 hover:text-blue-700 font-medium"
                        >
                          Edit
                        </button>
                        <button
                          onClick={() => openDeleteModal(candidate)}
                          className="text-red-600 hover:text-red-700 font-medium"
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            <div className="bg-slate-50 border-t border-slate-200 px-6 py-4 flex items-center justify-between">
              <p className="text-sm text-slate-600">
                Page {pagination.page + 1} of {pagination.totalPages}
                {" "}
                ({pagination.totalElements} total)
              </p>
              <div className="flex gap-2">
                <button
                  onClick={() => fetchCandidates(pagination.page - 1)}
                  disabled={pagination.page === 0}
                  className="px-3 py-2 border border-slate-300 rounded-lg text-sm font-medium text-slate-700 hover:bg-slate-100 disabled:opacity-50 disabled:cursor-not-allowed transition"
                >
                  ← Previous
                </button>
                <button
                  onClick={() => fetchCandidates(pagination.page + 1)}
                  disabled={pagination.page >= pagination.totalPages - 1}
                  className="px-3 py-2 border border-slate-300 rounded-lg text-sm font-medium text-slate-700 hover:bg-slate-100 disabled:opacity-50 disabled:cursor-not-allowed transition"
                >
                  Next →
                </button>
              </div>
            </div>
          </>
        )}
      </div>

      {/* Create Modal */}
      {showCreateModal && (
        <Modal
          title="Add New Candidate"
          onClose={() => setShowCreateModal(false)}
          onSubmit={handleCreate}
          formData={formData}
          setFormData={setFormData}
        />
      )}

      {/* Edit Modal */}
      {showEditModal && (
        <Modal
          title="Edit Candidate"
          onClose={() => setShowEditModal(false)}
          onSubmit={handleUpdate}
          formData={formData}
          setFormData={setFormData}
        />
      )}

      {/* Delete Confirmation Modal */}
      {showDeleteModal && selectedCandidate && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-lg max-w-sm w-full p-6">
            <h2 className="text-xl font-bold text-slate-900">Delete Candidate?</h2>
            <p className="mt-2 text-slate-600">
              Are you sure you want to delete{" "}
              <strong>
                {selectedCandidate.firstName} {selectedCandidate.lastName}
              </strong>
              ? This action cannot be undone.
            </p>
            <div className="mt-6 flex gap-3">
              <button
                onClick={() => setShowDeleteModal(false)}
                className="flex-1 px-4 py-2 border border-slate-300 rounded-lg font-medium text-slate-700 hover:bg-slate-50 transition"
              >
                Cancel
              </button>
              <button
                onClick={handleDelete}
                className="flex-1 px-4 py-2 bg-red-600 rounded-lg font-medium text-white hover:bg-red-700 transition"
              >
                Delete
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

interface ModalProps {
  title: string;
  onClose: () => void;
  onSubmit: () => Promise<void>;
  formData: any;
  setFormData: (data: any) => void;
}

function Modal({
  title,
  onClose,
  onSubmit,
  formData,
  setFormData,
}: ModalProps) {
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async () => {
    setSubmitting(true);
    try {
      await onSubmit();
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto p-6">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-bold text-slate-900">{title}</h2>
          <button
            onClick={onClose}
            className="text-slate-400 hover:text-slate-600 text-2xl"
          >
            ✕
          </button>
        </div>

        <div className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-2">
                First Name *
              </label>
              <input
                type="text"
                value={formData.firstName}
                onChange={(e) =>
                  setFormData({ ...formData, firstName: e.target.value })
                }
                className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-2">
                Last Name *
              </label>
              <input
                type="text"
                value={formData.lastName}
                onChange={(e) =>
                  setFormData({ ...formData, lastName: e.target.value })
                }
                className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-2">
              Email *
            </label>
            <input
              type="email"
              value={formData.email}
              onChange={(e) =>
                setFormData({ ...formData, email: e.target.value })
              }
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-2">
              Phone
            </label>
            <input
              type="tel"
              value={formData.phone}
              onChange={(e) =>
                setFormData({ ...formData, phone: e.target.value })
              }
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-2">
              Skills (comma-separated)
            </label>
            <input
              type="text"
              value={formData.skills}
              onChange={(e) =>
                setFormData({ ...formData, skills: e.target.value })
              }
              placeholder="Java, Spring Boot, REST API"
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-2">
                Experience (years)
              </label>
              <input
                type="number"
                value={formData.experience}
                onChange={(e) =>
                  setFormData({
                    ...formData,
                    experience: parseInt(e.target.value) || 0,
                  })
                }
                min="0"
                className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-2">
                Status
              </label>
              <select
                value={formData.status}
                onChange={(e) =>
                  setFormData({ ...formData, status: e.target.value })
                }
                className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="ACTIVE">Active</option>
                <option value="INACTIVE">Inactive</option>
                <option value="HIRED">Hired</option>
                <option value="REJECTED">Rejected</option>
              </select>
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-2">
              Resume URL
            </label>
            <input
              type="url"
              value={formData.resumeUrl}
              onChange={(e) =>
                setFormData({ ...formData, resumeUrl: e.target.value })
              }
              placeholder="https://example.com/resume.pdf"
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>

        <div className="mt-6 flex gap-3">
          <button
            onClick={onClose}
            className="flex-1 px-4 py-2 border border-slate-300 rounded-lg font-medium text-slate-700 hover:bg-slate-50 transition"
          >
            Cancel
          </button>
          <button
            onClick={handleSubmit}
            disabled={submitting}
            className="flex-1 px-4 py-2 bg-blue-600 rounded-lg font-medium text-white hover:bg-blue-700 disabled:opacity-60 disabled:cursor-not-allowed transition"
          >
            {submitting ? "Saving..." : "Save"}
          </button>
        </div>
      </div>
    </div>
  );
}
