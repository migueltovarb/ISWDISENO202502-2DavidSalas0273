import { useState } from 'react'
import './LoginRegister.css'

const StatusMessage = ({ status }) => {
  if (!status?.text) return null
  return <p className={`status ${status.type}`}>{status.text}</p>
}

export function LoginForm({ onLogin, goRegister, goLanding }) {
  const [form, setForm] = useState({ email: 'admin@vetcarepro.local', password: 'admin123' })
  const [status, setStatus] = useState(null)

  const handleChange = (event) => {
    const { name, value } = event.target
    setForm((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setStatus({ type: 'info', text: 'Validando credenciales...' })
    try {
      await onLogin(form)
      setStatus({ type: 'ok', text: 'Ingreso exitoso' })
    } catch (err) {
      setStatus({ type: 'error', text: err.message })
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-bg"></div>
      <div className="auth-content">
        <button className="back-btn" onClick={goLanding} title="Volver">←</button>
        
        <div className="auth-card">
          <div className="auth-header">
            <div className="auth-logo">
              <span className="logo-icon"></span>
              <div>
                <h1>VetCarePro</h1>
                <p className="auth-subtitle">Inicia sesión en tu cuenta</p>
              </div>
            </div>
          </div>

          <form onSubmit={handleSubmit} className="auth-form">
            <div className="form-group">
              <label htmlFor="email">Correo electrónico</label>
              <input
                id="email"
                name="email"
                type="email"
                value={form.email}
                onChange={handleChange}
                placeholder="tu@email.com"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">Contraseña</label>
              <input
                id="password"
                name="password"
                type="password"
                value={form.password}
                onChange={handleChange}
                placeholder="••••••••"
                required
              />
            </div>

            <StatusMessage status={status} />

            <button type="submit" className="btn btn-primary btn-large">
              Iniciar Sesión
            </button>
          </form>

          <div className="auth-footer">
            <p>¿No tienes cuenta?</p>
            <button
              type="button"
              className="link-btn"
              onClick={goRegister}
            >
              Crea una nueva cuenta →
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export function RegisterForm({ onRegister, goLogin, goLanding }) {
  const [form, setForm] = useState({
    role: 'OWNER',
    fullName: '',
    email: '',
    password: '',
    phone: '',
    address: '',
    licenseNumber: '',
    specialization: ''
  })
  const [status, setStatus] = useState(null)

  const handleChange = (event) => {
    const { name, value } = event.target
    setForm((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setStatus({ type: 'info', text: 'Creando cuenta...' })
    try {
      const message = await onRegister(form)
      setStatus({ type: 'ok', text: message })
    } catch (err) {
      setStatus({ type: 'error', text: err.message })
    }
  }

  const isVet = form.role === 'VETERINARIAN'

  return (
    <div className="auth-container">
      <div className="auth-bg"></div>
      <div className="auth-content">
        <button className="back-btn" onClick={goLanding} title="Volver">←</button>
        
        <div className="auth-card">
          <div className="auth-header">
            <div className="auth-logo">
              <span className="logo-icon"></span>
              <div>
                <h1>VetCarePro</h1>
                <p className="auth-subtitle">Crea tu cuenta</p>
              </div>
            </div>
          </div>

          <div className="role-selector">
            <label className={`role-option ${!isVet ? 'active' : ''}`}>
              <input
                type="radio"
                name="role"
                value="OWNER"
                checked={!isVet}
                onChange={handleChange}
              />
              <span>Dueño de Mascota</span>
            </label>
            <label className={`role-option ${isVet ? 'active' : ''}`}>
              <input
                type="radio"
                name="role"
                value="VETERINARIAN"
                checked={isVet}
                onChange={handleChange}
              />
              <span>Veterinario</span>
            </label>
          </div>

          <form onSubmit={handleSubmit} className="auth-form">
            <div className="form-group">
              <label htmlFor="fullName">Nombre completo</label>
              <input
                id="fullName"
                name="fullName"
                type="text"
                value={form.fullName}
                onChange={handleChange}
                placeholder="Tu nombre"
                required
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="email">Correo electrónico</label>
                <input
                  id="email"
                  name="email"
                  type="email"
                  value={form.email}
                  onChange={handleChange}
                  placeholder="tu@email.com"
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="password">Contraseña</label>
                <input
                  id="password"
                  name="password"
                  type="password"
                  value={form.password}
                  onChange={handleChange}
                  placeholder="••••••••"
                  required
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="phone">Teléfono</label>
                <input
                  id="phone"
                  name="phone"
                  type="tel"
                  value={form.phone}
                  onChange={handleChange}
                  placeholder="+1 (555) 000-0000"
                  required
                />
              </div>
              {isVet && (
                <div className="form-group">
                  <label htmlFor="licenseNumber">Número de Licencia</label>
                  <input
                    id="licenseNumber"
                    name="licenseNumber"
                    type="text"
                    value={form.licenseNumber}
                    onChange={handleChange}
                    placeholder="Lic. #123456"
                    required
                  />
                </div>
              )}
            </div>

            {!isVet && (
              <div className="form-group">
                <label htmlFor="address">Dirección</label>
                <input
                  id="address"
                  name="address"
                  type="text"
                  value={form.address}
                  onChange={handleChange}
                  placeholder="Tu dirección"
                  required
                />
              </div>
            )}

            {isVet && (
              <div className="form-group">
                <label htmlFor="specialization">Especialización</label>
                <input
                  id="specialization"
                  name="specialization"
                  type="text"
                  value={form.specialization}
                  onChange={handleChange}
                  placeholder="Ej: Cirugía, Dermatología"
                  required
                />
              </div>
            )}

            <StatusMessage status={status} />

            <button type="submit" className="btn btn-primary btn-large">
              Crear Cuenta
            </button>
          </form>

          <div className="auth-footer">
            <p>¿Ya tienes cuenta?</p>
            <button
              type="button"
              className="link-btn"
              onClick={goLogin}
            >
              Inicia sesión aquí →
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}
