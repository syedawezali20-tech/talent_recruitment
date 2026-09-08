const BASE_URL = "http://localhost:8080";

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  page?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}

interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

interface AuthData {
  token: string;
  refreshToken: string;
  username: string;
  role: string;
}

interface CandidateData {
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

interface CandidatesListResponse
  extends ApiResponse<CandidateData[]> {}

interface CreateCandidateRequest {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  skills: string;
  experience: number;
  resumeUrl?: string;
  status?: string;
}

const getHeaders = () => {
  const headers: HeadersInit = {
    "Content-Type": "application/json",
  };

  if (typeof window !== "undefined") {
    const token = localStorage.getItem("token");

    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }
  }

  return headers;
};

// ============================================================
// LOGIN
// ============================================================

export async function login(
  request: LoginRequest
): Promise<ApiResponse<AuthData>> {
  const response = await fetch(
    `${BASE_URL}/api/auth/login`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    }
  );

  if (!response.ok) {
    const error = await response.json();

    throw new Error(
      error.message || "Login failed"
    );
  }

  return response.json();
}

// ============================================================
// LOGOUT
// ============================================================

export async function logout(
  refreshToken: string
): Promise<ApiResponse<string>> {
  const response = await fetch(
    `${BASE_URL}/api/auth/logout`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        refreshToken,
      }),
    }
  );

  if (!response.ok) {
    const error =
      await response.json().catch(() => ({}));

    throw new Error(
      error.message || "Logout failed"
    );
  }

  return response.json();
}

// ============================================================
// CANDIDATE APIs
// ============================================================

export async function getCandidates(params?: {
  page?: number;
  size?: number;
  skill?: string;
  status?: string;
  sortBy?: string;
  direction?: string;
}): Promise<CandidatesListResponse> {
  const queryParams = new URLSearchParams();

  if (params?.page !== undefined)
    queryParams.append(
      "page",
      String(params.page)
    );

  if (params?.size !== undefined)
    queryParams.append(
      "size",
      String(params.size)
    );

  if (params?.skill)
    queryParams.append(
      "skill",
      params.skill
    );

  if (params?.status)
    queryParams.append(
      "status",
      params.status
    );

  if (params?.sortBy)
    queryParams.append(
      "sortBy",
      params.sortBy
    );

  if (params?.direction)
    queryParams.append(
      "direction",
      params.direction
    );

  const url =
    queryParams.toString().length > 0
      ? `${BASE_URL}/api/candidates?${queryParams.toString()}`
      : `${BASE_URL}/api/candidates`;

  const response = await fetch(url, {
    method: "GET",
    headers: getHeaders(),
  });

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("UNAUTHORIZED");
    }

    const error = await response.json();

    throw new Error(
      error.message ||
        "Failed to fetch candidates"
    );
  }

  return response.json();
}

export async function getCandidateById(
  id: number
): Promise<ApiResponse<CandidateData>> {
  const response = await fetch(
    `${BASE_URL}/api/candidates/${id}`,
    {
      method: "GET",
      headers: getHeaders(),
    }
  );

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("UNAUTHORIZED");
    }

    const error = await response.json();

    throw new Error(
      error.message ||
        "Failed to fetch candidate"
    );
  }

  return response.json();
}

export async function createCandidate(
  request: CreateCandidateRequest
): Promise<ApiResponse<CandidateData>> {
  const response = await fetch(
    `${BASE_URL}/api/candidates`,
    {
      method: "POST",
      headers: getHeaders(),
      body: JSON.stringify(request),
    }
  );

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("UNAUTHORIZED");
    }

    const error = await response.json();

    throw new Error(
      error.message ||
        "Failed to create candidate"
    );
  }

  return response.json();
}

export async function updateCandidate(
  id: number,
  request: CreateCandidateRequest
): Promise<ApiResponse<CandidateData>> {
  const response = await fetch(
    `${BASE_URL}/api/candidates/${id}`,
    {
      method: "PUT",
      headers: getHeaders(),
      body: JSON.stringify(request),
    }
  );

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("UNAUTHORIZED");
    }

    if (response.status === 403) {
      throw new Error("FORBIDDEN");
    }

    const error = await response.json();

    throw new Error(
      error.message ||
        "Failed to update candidate"
    );
  }

  return response.json();
}

export async function deleteCandidate(
  id: number
): Promise<void> {
  const response = await fetch(
    `${BASE_URL}/api/candidates/${id}`,
    {
      method: "DELETE",
      headers: getHeaders(),
    }
  );

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("UNAUTHORIZED");
    }

    if (response.status === 403) {
      throw new Error("FORBIDDEN");
    }

    const error = await response.json();

    throw new Error(
      error.message ||
        "Failed to delete candidate"
    );
  }
}

// ============================================================
// JOB TYPES
// ============================================================

export interface JobData {
  id: number;
  title: string;
  department: string;
  description: string;
  location: string;
  experienceRequired: number;
  employmentType: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}

export interface JobsListResponse
  extends ApiResponse<JobData[]> {}

export interface CreateJobRequest {
  title: string;
  department: string;
  description: string;
  location: string;
  experienceRequired: number;
  employmentType: string;
  status?: string;
}

// ============================================================
// JOB APIs
// ============================================================

export async function getJobs(params?: {
  page?: number;
  size?: number;
  department?: string;
  location?: string;
  status?: string;
  sortBy?: string;
  direction?: string;
}): Promise<JobsListResponse> {
  const queryParams = new URLSearchParams();

  if (params?.page !== undefined) {
    queryParams.append(
      "page",
      String(params.page)
    );
  }

  if (params?.size !== undefined) {
    queryParams.append(
      "size",
      String(params.size)
    );
  }

  if (params?.department) {
    queryParams.append(
      "department",
      params.department
    );
  }

  if (params?.location) {
    queryParams.append(
      "location",
      params.location
    );
  }

  if (params?.status) {
    queryParams.append(
      "status",
      params.status
    );
  }

  if (params?.sortBy) {
    queryParams.append(
      "sortBy",
      params.sortBy
    );
  }

  if (params?.direction) {
    queryParams.append(
      "direction",
      params.direction
    );
  }

  const queryString =
    queryParams.toString();

  const url = queryString
    ? `${BASE_URL}/api/jobs?${queryString}`
    : `${BASE_URL}/api/jobs`;

  const response = await fetch(url, {
    method: "GET",
    headers: getHeaders(),
  });

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("UNAUTHORIZED");
    }

    if (response.status === 403) {
      throw new Error("FORBIDDEN");
    }

    const error =
      await response.json().catch(
        () => ({})
      );

    throw new Error(
      error.message ||
        "Failed to fetch jobs"
    );
  }

  return response.json();
}

export async function getJobById(
  id: number
): Promise<ApiResponse<JobData>> {
  const response = await fetch(
    `${BASE_URL}/api/jobs/${id}`,
    {
      method: "GET",
      headers: getHeaders(),
    }
  );

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("UNAUTHORIZED");
    }

    if (response.status === 403) {
      throw new Error("FORBIDDEN");
    }

    const error =
      await response.json().catch(
        () => ({})
      );

    throw new Error(
      error.message ||
        "Failed to fetch job"
    );
  }

  return response.json();
}

export async function createJob(
  request: CreateJobRequest
): Promise<ApiResponse<JobData>> {
  const response = await fetch(
    `${BASE_URL}/api/jobs`,
    {
      method: "POST",
      headers: getHeaders(),
      body: JSON.stringify(request),
    }
  );

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("UNAUTHORIZED");
    }

    if (response.status === 403) {
      throw new Error("FORBIDDEN");
    }

    const error =
      await response.json().catch(
        () => ({})
      );

    throw new Error(
      error.message ||
        "Failed to create job"
    );
  }

  return response.json();
}

export async function updateJob(
  id: number,
  request: CreateJobRequest
): Promise<ApiResponse<JobData>> {
  const response = await fetch(
    `${BASE_URL}/api/jobs/${id}`,
    {
      method: "PUT",
      headers: getHeaders(),
      body: JSON.stringify(request),
    }
  );

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("UNAUTHORIZED");
    }

    if (response.status === 403) {
      throw new Error("FORBIDDEN");
    }

    const error =
      await response.json().catch(
        () => ({})
      );

    throw new Error(
      error.message ||
        "Failed to update job"
    );
  }

  return response.json();
}

export async function deleteJob(
  id: number
): Promise<void> {
  const response = await fetch(
    `${BASE_URL}/api/jobs/${id}`,
    {
      method: "DELETE",
      headers: getHeaders(),
    }
  );

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("UNAUTHORIZED");
    }

    if (response.status === 403) {
      throw new Error("FORBIDDEN");
    }

    const error =
      await response.json().catch(
        () => ({})
      );

    throw new Error(
      error.message ||
        "Failed to delete job"
    );
  }
}

// ============================================================
// CANDIDATE REGISTRATION
// ============================================================

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
  skills: string;
  experience: number;
  resumeUrl?: string;
}

export async function registerCandidate(
  request: RegisterRequest
): Promise<ApiResponse<string>> {
  const response = await fetch(
    `${BASE_URL}/api/auth/candidate/register`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    }
  );

  if (!response.ok) {
    const error =
      await response.json().catch(
        () => ({})
      );

    throw new Error(
      error.message ||
        "Candidate registration failed"
    );
  }

  return response.json();
}

// ============================================================
// FORGOT PASSWORD
// ============================================================

export interface ForgotPasswordRequest {
  email: string;
}

export async function forgotPassword(
  request: ForgotPasswordRequest
): Promise<ApiResponse<string>> {
  const response = await fetch(
    `${BASE_URL}/api/auth/forgot-password`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    }
  );

  if (!response.ok) {
    const error =
      await response.json().catch(
        () => ({})
      );

    throw new Error(
      error.message ||
        "Failed to send password reset email"
    );
  }

  return response.json();
}

// ============================================================
// RESET PASSWORD
// ============================================================

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}

export async function resetPassword(
  request: ResetPasswordRequest
): Promise<ApiResponse<string>> {
  const response = await fetch(
    `${BASE_URL}/api/auth/reset-password`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    }
  );

  if (!response.ok) {
    const error =
      await response.json().catch(
        () => ({})
      );

    throw new Error(
      error.message ||
        "Failed to reset password"
    );
  }

  return response.json();
}

// ============================================================
// CHANGE PASSWORD
// ============================================================

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export async function changePassword(
  request: ChangePasswordRequest
): Promise<ApiResponse<string>> {
  const token =
    localStorage.getItem("token");

  const response = await fetch(
    `${BASE_URL}/api/auth/change-password`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        ...(token
          ? {
              Authorization: `Bearer ${token}`,
            }
          : {}),
      },
      body: JSON.stringify(request),
    }
  );

  if (!response.ok) {
    const error =
      await response.json().catch(
        () => ({})
      );

    throw new Error(
      error.message ||
        "Failed to change password"
    );
  }

  return response.json();
}