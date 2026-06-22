import { useQuery } from '@tanstack/react-query'
import { jobsApi, JobStatus } from '../api/jobs'
import { useAuthStore } from '../hooks/useAuth'

const STATUS_LABELS: Record<JobStatus, string> = {
  WISHLIST: 'Wishlist', APPLIED: 'Applied', RECRUITER_CALL: 'Recruiter Call',
  TECHNICAL_SCREEN: 'Tech Screen', TAKE_HOME: 'Take Home', FINAL_ROUND: 'Final Round',
  OFFER: 'Offer', NEGOTIATING: 'Negotiating', ACCEPTED: 'Accepted',
  REJECTED: 'Rejected', WITHDRAWN: 'Withdrawn', GHOSTED: 'Ghosted',
}

export default function DashboardPage() {
  const { user } = useAuthStore()
  const { data, isLoading } = useQuery({
    queryKey: ['jobs-all'],
    queryFn: () => jobsApi.getAll().then(r => r.data.data),
  })

  const jobs = data || []
  const activeJobs = jobs.filter(j => !['REJECTED', 'WITHDRAWN', 'GHOSTED', 'ACCEPTED'].includes(j.status))
  const offers = jobs.filter(j => j.status === 'OFFER' || j.status === 'ACCEPTED')
  const applied = jobs.filter(j => j.status === 'APPLIED')
  const avgScore = jobs.filter(j => j.aiMatchScore).reduce((a, b, _, arr) =>
    a + (b.aiMatchScore || 0) / arr.length, 0)

  return (
    <div className="p-8 space-y-8">
      {/* Header */}
      <div className="animate-fade-in">
        <h1 className="text-3xl font-bold text-white">Good morning, {user?.name?.split(' ')[0]} 👋</h1>
        <p className="text-slate-400 mt-1">Here's your job hunt snapshot</p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 animate-slide-up">
        {[
          { label: 'Active Applications', value: activeJobs.length, icon: '💼', color: 'from-primary-500 to-primary-700' },
          { label: 'Awaiting Response', value: applied.length, icon: '⏳', color: 'from-amber-500 to-orange-600' },
          { label: 'Offers Received', value: offers.length, icon: '🎉', color: 'from-emerald-500 to-teal-600' },
          { label: 'Avg AI Match Score', value: avgScore ? `${Math.round(avgScore)}%` : '—', icon: '🤖', color: 'from-purple-500 to-pink-600' },
        ].map((stat) => (
          <div key={stat.label} className="card">
            <div className={`inline-flex items-center justify-center w-10 h-10 bg-gradient-to-br ${stat.color} rounded-xl text-xl mb-3 shadow-lg`}>
              {stat.icon}
            </div>
            <div className="text-3xl font-bold text-white mb-1">{isLoading ? '...' : stat.value}</div>
            <div className="text-sm text-slate-400">{stat.label}</div>
          </div>
        ))}
      </div>

      {/* Recent Applications */}
      <div className="card">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-semibold text-white">Recent Applications</h2>
          <a href="/board" className="text-sm text-primary-400 hover:text-primary-300">View Kanban →</a>
        </div>
        {isLoading ? (
          <div className="space-y-3">
            {[...Array(3)].map((_, i) => (
              <div key={i} className="h-16 bg-white/5 rounded-xl animate-pulse" />
            ))}
          </div>
        ) : jobs.length === 0 ? (
          <div className="text-center py-12">
            <div className="text-5xl mb-3">🚀</div>
            <p className="text-slate-400">No applications yet. Add your first one!</p>
            <a href="/board" className="btn-primary inline-block mt-4">Add Application</a>
          </div>
        ) : (
          <div className="space-y-2">
            {jobs.slice(0, 8).map((job) => (
              <div key={job.id} className="flex items-center justify-between p-3 glass-hover rounded-xl">
                <div className="flex items-center gap-3">
                  <img
                    src={`https://logo.clearbit.com/${encodeURIComponent(job.companyName.toLowerCase().replace(/ /g, ''))}.com`}
                    onError={(e) => {
                      const el = e.currentTarget as HTMLImageElement
                      el.style.display = 'none'
                      el.nextElementSibling!.classList.remove('hidden')
                    }}
                    className="w-8 h-8 rounded-lg object-contain bg-white/10"
                    alt={job.companyName}
                  />
                  <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-primary-500 to-purple-600 hidden items-center justify-center text-xs font-bold">
                    {job.companyName.charAt(0)}
                  </div>
                  <div>
                    <p className="font-medium text-white text-sm">{job.companyName}</p>
                    <p className="text-xs text-slate-400">{job.roleTitle}</p>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  {job.aiMatchScore && (
                    <span className="badge-primary">{job.aiMatchScore}% match</span>
                  )}
                  <span className="badge badge-info">{STATUS_LABELS[job.status]}</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
