import { useRef } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import apiClient from '../api/client'

export default function ResumesPage() {
  const fileRef = useRef<HTMLInputElement>(null)
  const queryClient = useQueryClient()

  const { data, isLoading } = useQuery({
    queryKey: ['resumes'],
    queryFn: () => apiClient.get<{ data: any[] }>('/resumes').then(r => r.data.data),
  })

  const uploadMutation = useMutation({
    mutationFn: (file: File) => {
      const form = new FormData()
      form.append('file', file)
      form.append('label', file.name)
      return apiClient.post('/resumes/upload', form, { headers: { 'Content-Type': 'multipart/form-data' } })
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['resumes'] }),
  })

  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">Resume Vault</h1>
          <p className="text-slate-400 text-sm">Upload and manage your resumes. AI will analyze them automatically.</p>
        </div>
        <button id="upload-resume-btn" onClick={() => fileRef.current?.click()} className="btn-primary">
          + Upload Resume
        </button>
        <input ref={fileRef} type="file" accept=".pdf,.docx" className="hidden"
          onChange={e => e.target.files?.[0] && uploadMutation.mutate(e.target.files[0])} />
      </div>

      {uploadMutation.isPending && (
        <div className="card border border-primary-500/30">
          <div className="flex items-center gap-3">
            <div className="w-5 h-5 border-2 border-primary-500 border-t-transparent rounded-full animate-spin" />
            <span className="text-slate-300">Uploading and extracting text...</span>
          </div>
        </div>
      )}

      <div className="space-y-3">
        {isLoading ? (
          [...Array(2)].map((_, i) => <div key={i} className="h-20 bg-white/5 rounded-2xl animate-pulse" />)
        ) : data?.length === 0 ? (
          <div className="card text-center py-16">
            <div className="text-5xl mb-3">📄</div>
            <p className="text-slate-400">No resumes yet. Upload a PDF or DOCX.</p>
          </div>
        ) : data?.map((r: any) => (
          <div key={r.id} className="card flex items-center gap-4">
            <div className="w-10 h-10 bg-red-500/20 rounded-xl flex items-center justify-center text-lg">📄</div>
            <div className="flex-1">
              <p className="font-medium text-white">{r.label}</p>
              <p className="text-xs text-slate-400">{r.fileName} • {r.fileSize ? `${(r.fileSize / 1024).toFixed(0)} KB` : ''}</p>
            </div>
            {r.isPrimary && <span className="badge-success">Primary</span>}
          </div>
        ))}
      </div>
    </div>
  )
}
