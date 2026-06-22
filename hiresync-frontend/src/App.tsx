import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuthStore } from './hooks/useAuth'
import Layout from './components/Layout'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import DashboardPage from './pages/DashboardPage'
import BoardPage from './pages/BoardPage'
import JobsPage from './pages/JobsPage'
import ResumesPage from './pages/ResumesPage'
import AnalyticsPage from './pages/AnalyticsPage'
import AiChatPage from './pages/AiChatPage'

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuthStore()
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/" element={
        <ProtectedRoute><Layout /></ProtectedRoute>
      }>
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<DashboardPage />} />
        <Route path="board" element={<BoardPage />} />
        <Route path="jobs" element={<JobsPage />} />
        <Route path="resumes" element={<ResumesPage />} />
        <Route path="analytics" element={<AnalyticsPage />} />
        <Route path="ai-chat" element={<AiChatPage />} />
      </Route>
    </Routes>
  )
}
