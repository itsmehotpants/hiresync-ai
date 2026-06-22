import apiClient from './client'

export type JobStatus =
  | 'WISHLIST' | 'APPLIED' | 'RECRUITER_CALL' | 'TECHNICAL_SCREEN'
  | 'TAKE_HOME' | 'FINAL_ROUND' | 'OFFER' | 'NEGOTIATING'
  | 'ACCEPTED' | 'REJECTED' | 'WITHDRAWN' | 'GHOSTED'

export interface JobApplication {
  id: string
  companyName: string
  roleTitle: string
  jobUrl?: string
  jobDescription?: string
  status: JobStatus
  stageOrder: number
  source?: string
  location?: string
  isRemote: boolean
  salaryMin?: number
  salaryMax?: number
  salaryCurrency: string
  notes?: string
  appliedAt?: string
  deadlineAt?: string
  aiMatchScore?: number
  aiFeedback?: string
  atsPrediction?: string
  createdAt: string
  updatedAt: string
}

export const jobsApi = {
  create: (data: Partial<JobApplication>) =>
    apiClient.post<{ data: JobApplication }>('/jobs', data),

  getKanban: () =>
    apiClient.get<{ data: Record<string, JobApplication[]> }>('/jobs/kanban'),

  getAll: () =>
    apiClient.get<{ data: JobApplication[] }>('/jobs'),

  getOne: (id: string) =>
    apiClient.get<{ data: JobApplication }>(`/jobs/${id}`),

  update: (id: string, data: Partial<JobApplication>) =>
    apiClient.put<{ data: JobApplication }>(`/jobs/${id}`, data),

  delete: (id: string) =>
    apiClient.delete(`/jobs/${id}`),

  reorder: (reorders: Array<{ jobId: string; newOrder: number }>) =>
    apiClient.put('/jobs/reorder', reorders),
}
