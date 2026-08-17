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

interface CandidatesListResponse extends ApiResponse<CandidateData[]> {}

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

export async function login(
  request: LoginRequest
): Promise<ApiResponse<AuthData>> {
  const response = await fetch(`${BASE_URL}/api/auth/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || "Login failed");
  }

  return response.json();
}

export async function getCandidates(params?: {
  page?: number;
  size?: number;
  skill?: string;
  status?: string;
  sortBy?: string;
  direction?: string;
}): Promise<CandidatesListResponse> {
  const queryParams = new URLSearchParams();

  if (params?.page !== undefined) queryParams.append("page", String(params.page));
  if (params?.size !== undefined) queryParams.append("size", String(params.size));
  if (params?.skill) queryParams.append("skill", params.skill);
  if (params?.status) queryParams.append("status", params.status);
  if (params?.sortBy) queryParams.append("sortBy", params.sortBy);
  if (params?.direction) queryParams.append("direction", params.direction);

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
    throw new Error(error.message || "Failed to fetch candidates");
  }

  return response.json();
}

export async function getCandidateById(
  id: number
): Promise<ApiResponse<CandidateData>> {
  const response = await fetch(`${BASE_URL}/api/candidates/${id}`, {
    method: "GET",
    headers: getHeaders(),
  });

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("UNAUTHORIZED");
    }
    const error = await response.json();
    throw new Error(error.message || "Failed to fetch candidate");
  }

  return response.json();
}

export async function createCandidate(
  request: CreateCandidateRequest
): Promise<ApiResponse<CandidateData>> {
  const response = await fetch(`${BASE_URL}/api/candidates`, {
    method: "POST",
    headers: getHeaders(),
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("UNAUTHORIZED");
    }
    const error = await response.json();
    throw new Error(error.message || "Failed to create candidate");
  }

  return response.json();
}

export async function updateCandidate(
  id: number,
  request: CreateCandidateRequest
): Promise<ApiResponse<CandidateData>> {
  const response = await fetch(`${BASE_URL}/api/candidates/${id}`, {
    method: "PUT",
    headers: getHeaders(),
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("UNAUTHORIZED");
    }
    if (response.status === 403) {
      throw new Error("FORBIDDEN");
    }
    const error = await response.json();
    throw new Error(error.message || "Failed to update candidate");
  }

  return response.json();
}

export async function deleteCandidate(id: number): Promise<void> {
  const response = await fetch(`${BASE_URL}/api/candidates/${id}`, {
    method: "DELETE",
    headers: getHeaders(),
  });

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("UNAUTHORIZED");
    }
    if (response.status === 403) {
      throw new Error("FORBIDDEN");
    }
    const error = await response.json();
    throw new Error(error.message || "Failed to delete candidate");
  }
}
