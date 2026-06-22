import apiClient from './client'

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  user: { id: string; email: string; name: string; pictureUrl?: string }
}

export const authApi = {
  register: (data: { email: string; name: string; password: string }) =>
    apiClient.post<{ data: AuthResponse }>('/auth/register', data),

  login: (data: { email: string; password: string }) =>
    apiClient.post<{ data: AuthResponse }>('/auth/login', data),

  logout: () => apiClient.post('/auth/logout'),

  me: () => apiClient.get<{ data: { id: string; email: string; name: string } }>('/auth/me'),
}
