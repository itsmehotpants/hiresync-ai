import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { DragDropContext, Droppable, Draggable, DropResult } from '@hello-pangea/dnd'
import { jobsApi, JobApplication, JobStatus } from '../api/jobs'
import { useState } from 'react'

const COLUMNS: { key: JobStatus; label: string; color: string }[] = [
  { key: 'WISHLIST', label: '⭐ Wishlist', color: 'border-slate-500' },
  { key: 'APPLIED', label: '📨 Applied', color: 'border-blue-500' },
  { key: 'RECRUITER_CALL', label: '📞 Recruiter', color: 'border-yellow-500' },
  { key: 'TECHNICAL_SCREEN', label: '💻 Tech Screen', color: 'border-orange-500' },
  { key: 'FINAL_ROUND', label: '🏆 Final Round', color: 'border-purple-500' },
  { key: 'OFFER', label: '🎉 Offer', color: 'border-emerald-500' },
  { key: 'REJECTED', label: '❌ Rejected', color: 'border-red-500' },
  { key: 'GHOSTED', label: '👻 Ghosted', color: 'border-gray-500' },
]

function JobCard({ job, index }: { job: JobApplication; index: number }) {
  const daysSince = job.appliedAt
    ? Math.floor((Date.now() - new Date(job.appliedAt).getTime()) / 86400000)
    : null

  return (
    <Draggable draggableId={job.id} index={index}>
      {(provided) => (
        <div
          ref={provided.innerRef}
          {...provided.draggableProps}
          {...provided.dragHandleProps}
          className="card mb-2 cursor-grab active:cursor-grabbing group"
        >
          <div className="flex items-start gap-2">
            <img
              src={`https://logo.clearbit.com/${job.companyName.toLowerCase().replace(/ /g, '')}.com`}
              onError={(e) => { (e.currentTarget as HTMLImageElement).style.display = 'none' }}
              className="w-7 h-7 rounded-md object-contain bg-white/10 flex-shrink-0"
              alt=""
            />
            <div className="flex-1 min-w-0">
              <p className="font-semibold text-white text-sm truncate">{job.companyName}</p>
              <p className="text-xs text-slate-400 truncate">{job.roleTitle}</p>
            </div>
          </div>

          <div className="flex items-center justify-between mt-3">
            <div className="flex flex-wrap gap-1">
              {daysSince !== null && (
                <span className="text-xs text-slate-500">{daysSince}d ago</span>
              )}
            </div>
            {job.aiMatchScore !== null && job.aiMatchScore !== undefined && (
              <span className={`badge text-xs font-bold ${
                job.aiMatchScore >= 70 ? 'badge-success' :
                job.aiMatchScore >= 50 ? 'badge-warning' : 'badge-danger'
              }`}>{job.aiMatchScore}%</span>
            )}
          </div>
        </div>
      )}
    </Draggable>
  )
}

export default function BoardPage() {
  const queryClient = useQueryClient()
  const [showAddModal, setShowAddModal] = useState(false)
  const [newJob, setNewJob] = useState({ companyName: '', roleTitle: '' })

  const { data, isLoading } = useQuery({
    queryKey: ['kanban'],
    queryFn: () => jobsApi.getKanban().then(r => r.data.data),
  })

  const updateMutation = useMutation({
    mutationFn: ({ id, status }: { id: string; status: JobStatus }) =>
      jobsApi.update(id, { status }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['kanban'] }),
  })

  const createMutation = useMutation({
    mutationFn: (data: typeof newJob) => jobsApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['kanban'] })
      setShowAddModal(false)
      setNewJob({ companyName: '', roleTitle: '' })
    },
  })

  const onDragEnd = (result: DropResult) => {
    const { destination, draggableId } = result
    if (!destination) return
    const newStatus = destination.droppableId as JobStatus
    updateMutation.mutate({ id: draggableId, status: newStatus })
  }

  return (
    <div className="p-6 h-full flex flex-col">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-white">Application Board</h1>
          <p className="text-slate-400 text-sm">Drag cards to update status</p>
        </div>
        <button id="add-job-btn" onClick={() => setShowAddModal(true)} className="btn-primary">
          + Add Application
        </button>
      </div>

      {isLoading ? (
        <div className="flex gap-4 overflow-x-auto pb-4">
          {COLUMNS.map(col => (
            <div key={col.key} className="flex-shrink-0 w-64">
              <div className="h-8 bg-white/5 rounded-xl mb-3 animate-pulse" />
              {[1, 2].map(i => <div key={i} className="h-24 bg-white/5 rounded-xl mb-2 animate-pulse" />)}
            </div>
          ))}
        </div>
      ) : (
        <DragDropContext onDragEnd={onDragEnd}>
          <div className="flex gap-4 overflow-x-auto pb-4 flex-1">
            {COLUMNS.map((col) => {
              const columnJobs = data?.[col.key] || []
              return (
                <div key={col.key} className="flex-shrink-0 w-64 flex flex-col">
                  <div className={`flex items-center justify-between px-3 py-2 mb-2 rounded-xl border-l-2 ${col.color} bg-white/5`}>
                    <span className="text-sm font-semibold text-slate-300">{col.label}</span>
                    <span className="text-xs text-slate-500 bg-white/10 px-2 py-0.5 rounded-full">{columnJobs.length}</span>
                  </div>
                  <Droppable droppableId={col.key}>
                    {(provided, snapshot) => (
                      <div
                        ref={provided.innerRef}
                        {...provided.droppableProps}
                        className={`flex-1 min-h-32 rounded-xl p-2 transition-colors ${
                          snapshot.isDraggingOver ? 'bg-primary-500/10 border border-primary-500/30' : 'bg-white/[0.02]'
                        }`}
                      >
                        {columnJobs.map((job, i) => (
                          <JobCard key={job.id} job={job} index={i} />
                        ))}
                        {provided.placeholder}
                      </div>
                    )}
                  </Droppable>
                </div>
              )
            })}
          </div>
        </DragDropContext>
      )}

      {/* Add Job Modal */}
      {showAddModal && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="card w-full max-w-md animate-slide-up">
            <h2 className="text-xl font-bold text-white mb-4">Add Application</h2>
            <div className="space-y-3">
              <div>
                <label className="block text-sm text-slate-300 mb-1">Company Name *</label>
                <input id="new-company" className="input-field" placeholder="Google, Meta, Startup..."
                  value={newJob.companyName} onChange={e => setNewJob(p => ({ ...p, companyName: e.target.value }))} />
              </div>
              <div>
                <label className="block text-sm text-slate-300 mb-1">Role Title *</label>
                <input id="new-role" className="input-field" placeholder="Software Engineer, PM..."
                  value={newJob.roleTitle} onChange={e => setNewJob(p => ({ ...p, roleTitle: e.target.value }))} />
              </div>
            </div>
            <div className="flex gap-3 mt-6">
              <button onClick={() => setShowAddModal(false)} className="btn-secondary flex-1">Cancel</button>
              <button id="submit-job-btn" onClick={() => createMutation.mutate(newJob)}
                disabled={!newJob.companyName || !newJob.roleTitle || createMutation.isPending}
                className="btn-primary flex-1 disabled:opacity-50">
                {createMutation.isPending ? 'Adding...' : 'Add Application'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
