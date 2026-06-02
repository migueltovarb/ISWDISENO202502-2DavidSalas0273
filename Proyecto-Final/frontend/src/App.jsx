import { useState } from 'react'
import './App.css'
import LandingPage from './LandingPage'
import { LoginForm, RegisterForm } from './LoginRegister'
import AdminDashboard from './AdminDashboard'
import OwnerDashboard from './OwnerDashboard'
import VetDashboard from './VetDashboard'

const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080'

async function api(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json', ...(options.headers ?? {}) },
    ...options
  })
  const text = await response.text()
  if (!response.ok) {
    let message = `Error ${response.status}`
    try { message = JSON.parse(text)?.message ?? text } catch (_) { message = text }
    throw new Error(message)
  }
  return text ? JSON.parse(text) : null
}

function App() {
  const [user, setUser] = useState(null)
  const [view, setView] = useState('landing')

  const handleLogin = async (credentials) => {
    const data = await api('/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials)
    })
    setUser(data)
  }

  const handleRegister = async (form) => {
    if (form.role === 'OWNER') {
      const resp = await api('/auth/register-owner', {
        method: 'POST',
        body: JSON.stringify({
          fullName: form.fullName, email: form.email,
          password: form.password, phone: form.phone, address: form.address
        })
      })
      return `Dueño creado con ID ${resp.id}. Inicia sesión para continuar.`
    }
    const resp = await api('/auth/register-veterinarian', {
      method: 'POST',
      body: JSON.stringify({
        fullName: form.fullName, email: form.email,
        password: form.password, phone: form.phone,
        licenseNumber: form.licenseNumber, specialization: form.specialization
      })
    })
    return `Veterinario creado con ID ${resp.id}. Inicia sesión para continuar.`
  }

  const handleLogout = () => {
    setUser(null)
    setView('landing')
  }

  const isDashboard = !!user
  const isLanding = !user && view === 'landing'

  return (
    <div className={`app-shell ${isLanding ? 'landing-view' : ''} ${isDashboard ? 'dashboard-view' : ''}`}>
      <div className={`backdrop ${isLanding || isDashboard ? 'hidden' : ''}`} aria-hidden="true">
        <span className="blob blob-one" />
        <span className="blob blob-two" />
        <span className="grid-pattern" />
      </div>
      <div className={`app-content ${isLanding ? 'landing-view' : ''} ${isDashboard ? 'dashboard-view' : ''}`}>
        {!user ? (
          view === 'landing' ? (
            <LandingPage
              onStartLogin={() => setView('login')}
              onStartRegister={() => setView('register')}
            />
          ) : view === 'login' ? (
            <LoginForm
              onLogin={handleLogin}
              goRegister={() => setView('register')}
              goLanding={() => setView('landing')}
            />
          ) : (
            <RegisterForm
              onRegister={handleRegister}
              goLogin={() => setView('login')}
              goLanding={() => setView('landing')}
            />
          )
        ) : user.role === 'OWNER' ? (
          <OwnerDashboard user={user} onLogout={handleLogout} />
        ) : user.role === 'VETERINARIAN' ? (
          <VetDashboard user={user} onLogout={handleLogout} />
        ) : user.role === 'ADMIN' ? (
          <AdminDashboard user={user} onLogout={handleLogout} />
        ) : (
          <div className="dashboard">
            <header>
              <div>
                <p className="eyebrow">Usuario</p>
                <h2>{user.fullName}</h2>
                <p className="muted">No hay tablero para el rol {user.role}.</p>
              </div>
              <button className="ghost" onClick={handleLogout}>Cerrar sesión</button>
            </header>
          </div>
        )}
      </div>
    </div>
  )
}

export default App
