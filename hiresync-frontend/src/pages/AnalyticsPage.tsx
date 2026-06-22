import { useQuery } from '@tanstack/react-query'
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, Legend,
} from 'recharts'
import { useAuthStore } from '../hooks/useAuth'
import { jobsApi } from '../api/jobs'

const COLORS = ['#6272f2', '#8b5cf6', '#f59e0b', '#10b981', '#ef4444', '#6b7280']

export default function AnalyticsPage() {
  const { user } = useAuthStore()
  const { data, isLoading } = useQuery({
    queryKey: ['jobs-all'],
    queryFn: () => jobsApi.getAll().then(r => r.data.data),
  })

  const statusCounts = (data || []).reduce((acc, job) => {
    acc[job.status] = (acc[job.status] || 0) + 1
    return acc
  }, {} as Record<string, number>)

  const funnelData = Object.entries(statusCounts).map(([status, count]) => ({ status, count }))

  const sourceCounts = (data || []).filter(j => j.source).reduce((acc, job) => {
    const src = job.source!
    acc[src] = (acc[src] || 0) + 1
    return acc
  }, {} as Record<string, number>)

  const sourceData = Object.entries(sourceCounts).map(([name, value]) => ({ name, value }))

  // Suppress unused variable warning from linter — user is available for future personalization
  void user

  return (
    <div className="p-8 space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Analytics</h1>
        <p className="text-slate-400 text-sm">Your job hunt performance at a glance</p>
      </div>

      <div className="grid grid-cols-2 gap-6">
        <div className="card">
          <h2 className="text-lg font-semibold text-white mb-4">Application Funnel</h2>
          {isLoading ? (
            <div className="h-48 bg-white/5 rounded-xl animate-pulse" />
          ) : (
            <ResponsiveContainer width="100%" height={240}>
              <BarChart data={funnelData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#ffffff10" />
                <XAxis dataKey="status" tick={{ fill: '#94a3b8', fontSize: 10 }} />
                <YAxis tick={{ fill: '#94a3b8', fontSize: 12 }} />
                <Tooltip contentStyle={{ background: '#1a1a2e', border: '1px solid #ffffff20', borderRadius: 8 }} />
                <Bar dataKey="count" fill="#6272f2" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          )}
        </div>

        {sourceData.length > 0 && (
          <div className="card">
            <h2 className="text-lg font-semibold text-white mb-4">By Source</h2>
            <ResponsiveContainer width="100%" height={240}>
              <PieChart>
                <Pie
                  data={sourceData}
                  cx="50%"
                  cy="50%"
                  outerRadius={80}
                  dataKey="value"
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                >
                  {sourceData.map((_, i) => (
                    <Cell key={i} fill={COLORS[i % COLORS.length]} />
                  ))}
                </Pie>
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </div>
        )}
      </div>
    </div>
  )
}
