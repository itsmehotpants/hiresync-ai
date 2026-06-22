import { useState, useRef, useEffect } from 'react'
import { useAuthStore } from '../hooks/useAuth'
import ReactMarkdown from 'react-markdown'

interface Message {
  role: 'user' | 'assistant'
  content: string
  timestamp: Date
}

export default function AiChatPage() {
  const [messages, setMessages] = useState<Message[]>([
    {
      role: 'assistant',
      content: '👋 Hi! I\'m your AI career coach. I have access to your real application data. Try asking me:\n\n• "Which applications haven\'t had a response in 2 weeks?"\n• "Draft a follow-up email for Google"\n• "What\'s my response rate?"\n• "What upcoming interviews do I have?"',
      timestamp: new Date(),
    },
  ])
  const [input, setInput] = useState('')
  const [streaming, setStreaming] = useState(false)
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const { accessToken } = useAuthStore()

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages])

  const sendMessage = async () => {
    if (!input.trim() || streaming) return

    const userMsg: Message = { role: 'user', content: input, timestamp: new Date() }
    setMessages((prev) => [...prev, userMsg])
    const question = input
    setInput('')
    setStreaming(true)

    // Add placeholder assistant message
    setMessages((prev) => [...prev, { role: 'assistant', content: '', timestamp: new Date() }])

    const token = localStorage.getItem('accessToken') || ''
    const url = `/api/ai/chat/stream?message=${encodeURIComponent(question)}&token=${encodeURIComponent(token)}`
    const eventSource = new EventSource(url)

    eventSource.onmessage = (event) => {
      setMessages((prev) => {
        const updated = [...prev]
        updated[updated.length - 1].content += event.data
        return updated
      })
    }

    eventSource.addEventListener('complete', () => {
      eventSource.close()
      setStreaming(false)
    })

    eventSource.onerror = () => {
      eventSource.close()
      setStreaming(false)
    }
  }

  return (
    <div className="flex flex-col h-screen">
      {/* Header */}
      <div className="p-6 border-b border-white/10 glass">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-gradient-to-br from-primary-500 to-purple-600 rounded-xl flex items-center justify-center text-xl">
            🤖
          </div>
          <div>
            <h1 className="text-xl font-bold text-white">AI Career Coach</h1>
            <p className="text-sm text-slate-400">Powered by Gemini • Sees your real data</p>
          </div>
          {streaming && (
            <div className="ml-auto flex items-center gap-2 text-primary-400 text-sm">
              <div className="w-2 h-2 bg-primary-400 rounded-full animate-pulse" />
              Thinking...
            </div>
          )}
        </div>
      </div>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto p-6 space-y-4">
        {messages.map((msg, i) => (
          <div key={i} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'} animate-fade-in`}>
            {msg.role === 'assistant' && (
              <div className="w-8 h-8 bg-gradient-to-br from-primary-500 to-purple-600 rounded-full flex items-center justify-center text-sm mr-3 flex-shrink-0 mt-1">
                🤖
              </div>
            )}
            <div className={`max-w-2xl rounded-2xl px-4 py-3 text-sm ${
              msg.role === 'user'
                ? 'bg-primary-600/30 text-white border border-primary-500/30 rounded-br-sm'
                : 'glass text-slate-100 rounded-bl-sm'
            }`}>
              {msg.content ? (
                <ReactMarkdown className="prose prose-invert prose-sm max-w-none">{msg.content}</ReactMarkdown>
              ) : (
                <div className="flex gap-1">
                  <div className="w-2 h-2 bg-primary-400 rounded-full animate-bounce" style={{ animationDelay: '0ms' }} />
                  <div className="w-2 h-2 bg-primary-400 rounded-full animate-bounce" style={{ animationDelay: '150ms' }} />
                  <div className="w-2 h-2 bg-primary-400 rounded-full animate-bounce" style={{ animationDelay: '300ms' }} />
                </div>
              )}
            </div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      {/* Input */}
      <div className="p-6 border-t border-white/10 glass">
        <div className="flex gap-3">
          <input
            id="chat-input"
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && !e.shiftKey && sendMessage()}
            placeholder="Ask your AI coach anything about your job hunt..."
            disabled={streaming}
            className="input-field flex-1 disabled:opacity-50"
          />
          <button
            id="chat-send"
            onClick={sendMessage}
            disabled={streaming || !input.trim()}
            className="btn-primary px-6 disabled:opacity-50"
          >
            {streaming ? '⏳' : '→'}
          </button>
        </div>
      </div>
    </div>
  )
}
