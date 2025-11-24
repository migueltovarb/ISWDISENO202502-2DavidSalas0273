import { useCallback, useEffect, useState } from 'react'
import './App.css'

const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080'

const speciesOptions = [
  { value: 'DOG', label: 'Perro' },
  { value: 'CAT', label: 'Gato' },
  { value: 'BIRD', label: 'Ave' },
  { value: 'REPTILE', label: 'Reptil' },
  { value: 'SMALL_MAMMAL', label: 'Pequeño mamífero' },
  { value: 'OTHER', label: 'Otro' }
]

const appointmentTypes = [
  { value: 'CONSULTATION', label: 'Consulta' },
  { value: 'VACCINATION', label: 'Vacunación' }
]

const roleOptions = [
  { value: 'OWNER', label: 'Dueño' },
  { value: 'VETERINARIAN', label: 'Veterinario' }
]

const StatusMessage = ({ status }) => {
  if (!status?.text) return null
  return <p className={`status ${status.type}`}>{status.text}</p>
}

async function api(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers ?? {})
    },
    ...options
  })
  const text = await response.text()
  if (!response.ok) {
    let message = `Error ${response.status}`
    if (text) {
      try {
        const payload = JSON.parse(text)
        message = payload.message ?? JSON.stringify(payload)
      } catch (_) {
        message = text
      }
    }
    throw new Error(message)
  }
  return text ? JSON.parse(text) : null
}

function LoginForm({ onLogin, goRegister }) {
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
    <div className="auth-card">
      <h1>Vet Care Pro</h1>
      <p>Inicia sesión o regístrate para acceder al panel.</p>
      <form onSubmit={handleSubmit}>
        <label>
          Email
          <input name="email" type="email" value={form.email} onChange={handleChange} required />
        </label>
        <label>
          Contraseña
          <input name="password" type="password" value={form.password} onChange={handleChange} required />
        </label>
        <button type="submit">Ingresar</button>
      </form>
      <StatusMessage status={status} />
      <p className="switch">
        ¿No tienes cuenta?{' '}
        <button type="button" className="link" onClick={goRegister}>
          Regístrate aquí
        </button>
      </p>
    </div>
  )
}

function RegisterForm({ onRegister, goLogin }) {
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
    <div className="auth-card">
      <h1>Crear cuenta</h1>
      <p>Selecciona tu rol para acceder al tablero correspondiente.</p>
      <form onSubmit={handleSubmit}>
        <label>
          Rol
          <select name="role" value={form.role} onChange={handleChange}>
            {roleOptions.map((role) => (
              <option key={role.value} value={role.value}>
                {role.label}
              </option>
            ))}
          </select>
        </label>
        <label>
          Nombre completo
          <input name="fullName" value={form.fullName} onChange={handleChange} required />
        </label>
        <label>
          Email
          <input name="email" type="email" value={form.email} onChange={handleChange} required />
        </label>
        <label>
          Contraseña
          <input name="password" type="password" value={form.password} onChange={handleChange} required />
        </label>
        <label>
          Teléfono
          <input name="phone" value={form.phone} onChange={handleChange} required />
        </label>
        <label>
          Dirección
          <input name="address" value={form.address} onChange={handleChange} required={!isVet} />
        </label>
        {isVet && (
          <>
            <label>
              Número de licencia
              <input name="licenseNumber" value={form.licenseNumber} onChange={handleChange} required />
            </label>
            <label>
              Especialización
              <input name="specialization" value={form.specialization} onChange={handleChange} required />
            </label>
          </>
        )}
        <button type="submit">Registrarme</button>
      </form>
      <StatusMessage status={status} />
      <p className="switch">
        ¿Ya tienes cuenta?{' '}
        <button type="button" className="link" onClick={goLogin}>
          Inicia sesión
        </button>
      </p>
    </div>
  )
}

function OwnerDashboard({ user, onLogout }) {
  const ownerId = user.referenceId
  const [pets, setPets] = useState([])
  const [appointments, setAppointments] = useState([])
  const [plans, setPlans] = useState({})
  const [certificates, setCertificates] = useState({})
  const [status, setStatus] = useState(null)
  const [formStatus, setFormStatus] = useState(null)
  const [form, setForm] = useState({
    name: '',
    species: 'DOG',
    breed: '',
    birthDate: '',
    microchipId: '',
    neutered: 'false'
  })

  const loadOwnerData = useCallback(async () => {
    if (!ownerId) return
    try {
      setStatus({ type: 'info', text: 'Cargando información...' })
      const [petsData, apptData] = await Promise.all([
        api(`/api/pets/owner/${ownerId}`),
        api(`/api/appointments/owner/${ownerId}`)
      ])
      setPets(petsData)
      setAppointments(apptData)
      const planMap = {}
      const certMap = {}
      await Promise.all(
        petsData.map(async (pet) => {
          const [planList, certList] = await Promise.all([
            api(`/api/vaccination-plans/pet/${pet.id}`),
            api(`/api/vaccination-certificates/pet/${pet.id}`)
          ])
          planMap[pet.id] = planList
          certMap[pet.id] = certList
        })
      )
      setPlans(planMap)
      setCertificates(certMap)
      setStatus({ type: 'ok', text: 'Datos actualizados' })
    } catch (err) {
      setStatus({ type: 'error', text: err.message })
    }
  }, [ownerId])

  useEffect(() => {
    loadOwnerData()
  }, [loadOwnerData])

  const handleChange = (event) => {
    const { name, value } = event.target
    setForm((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    if (!ownerId) {
      setFormStatus({ type: 'error', text: 'No se encontró tu ID de dueño.' })
      return
    }
    setFormStatus({ type: 'info', text: 'Guardando mascota...' })
    try {
      await api('/api/pets', {
        method: 'POST',
        body: JSON.stringify({
          ownerId,
          name: form.name,
          species: form.species,
          breed: form.breed,
          birthDate: form.birthDate || null,
          microchipId: form.microchipId,
          neutered: form.neutered === 'true'
        })
      })
      setFormStatus({ type: 'ok', text: 'Mascota registrada correctamente' })
      setForm({ name: '', species: 'DOG', breed: '', birthDate: '', microchipId: '', neutered: 'false' })
      await loadOwnerData()
    } catch (err) {
      setFormStatus({ type: 'error', text: err.message })
    }
  }

  const upcomingAppointments = appointments
    .slice()
    .sort((a, b) => a.appointmentDate.localeCompare(b.appointmentDate))

  return (
    <div className="dashboard owner-theme">
      <header>
        <div>
          <p className="eyebrow">Dueño</p>
          <h2>Hola, {user.fullName}</h2>
          <p className="muted">
            Tu ID de dueño es: <strong>{ownerId ?? 'No disponible'}</strong>
          </p>
        </div>
        <div className="header-actions">
          <button className="ghost" onClick={loadOwnerData}>
            Refrescar
          </button>
          <button className="ghost" onClick={onLogout}>
            Cerrar sesión
          </button>
        </div>
      </header>

      <section className="card">
        <h3>Registrar nueva mascota</h3>
        <form className="grid-two" onSubmit={handleSubmit}>
          <label>
            Nombre
            <input name="name" value={form.name} onChange={handleChange} required />
          </label>
          <label>
            Especie
            <select name="species" value={form.species} onChange={handleChange}>
              {speciesOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </label>
          <label>
            Raza
            <input name="breed" value={form.breed} onChange={handleChange} />
          </label>
          <label>
            Fecha de nacimiento
            <input name="birthDate" type="date" value={form.birthDate} onChange={handleChange} />
          </label>
          <label>
            Nº microchip
            <input name="microchipId" value={form.microchipId} onChange={handleChange} />
          </label>
          <label>
            ¿Castrado?
            <select name="neutered" value={form.neutered} onChange={handleChange}>
              <option value="false">No</option>
              <option value="true">Sí</option>
            </select>
          </label>
          <button type="submit">Guardar mascota</button>
        </form>
        <StatusMessage status={formStatus} />
      </section>

      <section className="card">
        <h3>Tus mascotas ({pets.length})</h3>
        {pets.length === 0 ? (
          <p className="muted">Aún no has registrado mascotas.</p>
        ) : (
          <ul className="items-list">
            {pets.map((pet) => (
              <li key={pet.id}>
                <strong>{pet.name}</strong>
                <span>ID: {pet.id}</span>
                <span>Especie: {pet.species}</span>
                {pet.breed && <span>Raza: {pet.breed}</span>}
              </li>
            ))}
          </ul>
        )}
      </section>

      <section className="card">
        <h3>Próximas citas ({upcomingAppointments.length})</h3>
        {upcomingAppointments.length === 0 ? (
          <p className="muted">No hay citas agendadas.</p>
        ) : (
          <ul className="items-list">
            {upcomingAppointments.map((appt) => (
              <li key={appt.id}>
                <strong>{new Date(appt.appointmentDate).toLocaleString()}</strong>
                <span>Mascota: {appt.petId}</span>
                <span>Veterinario: {appt.veterinarianId}</span>
                <span>Tipo: {appt.type}</span>
                {appt.reason && <span>Motivo: {appt.reason}</span>}
              </li>
            ))}
          </ul>
        )}
      </section>

      <section className="card">
        <h3>Planes de vacunación y vacunas aplicadas</h3>
        {pets.length === 0 ? (
          <p className="muted">Registra una mascota para ver su plan.</p>
        ) : (
          pets.map((pet) => (
            <div key={pet.id} className="plan-row">
              <div>
                <strong>{pet.name}</strong>
                <p className="muted">ID: {pet.id}</p>
              </div>
              <div className="plan-columns">
                <div>
                  <p className="eyebrow">Plan programado</p>
                  {plans[pet.id]?.length ? (
                    <ul className="items-list">
                      {plans[pet.id].map((plan) => (
                        <li key={plan.id}>
                          <strong>{plan.vaccineName}</strong>
                          <span>Fecha objetivo: {plan.dueDate}</span>
                          <span>Estado: {plan.completed ? 'Completado' : 'Pendiente'}</span>
                          {plan.notes && <span>Notas: {plan.notes}</span>}
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p className="muted">Sin plan registrado.</p>
                  )}
                </div>
                <div>
                  <p className="eyebrow">Vacunas aplicadas</p>
                  {certificates[pet.id]?.length ? (
                    <ul className="items-list">
                      {certificates[pet.id].map((cert) => (
                        <li key={cert.id}>
                          <strong>{cert.certificateNumber ?? cert.id}</strong>
                          <span>Emisión: {cert.issueDate}</span>
                          <span>Expira: {cert.expirationDate}</span>
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p className="muted">Aún no hay certificados.</p>
                  )}
                </div>
              </div>
            </div>
          ))
        )}
      </section>
      <StatusMessage status={status} />
    </div>
  )
}

function VetDashboard({ user, onLogout }) {
  const vetId = user.referenceId
  const [patients, setPatients] = useState([])
  const [petId, setPetId] = useState('')
  const [patientStatus, setPatientStatus] = useState(null)
  const [appointments, setAppointments] = useState([])
  const [appointmentForm, setAppointmentForm] = useState({
    ownerId: '',
    petId: '',
    type: 'CONSULTATION',
    appointmentDate: '',
    reason: '',
    vaccineId: ''
  })
  const [appointmentStatus, setAppointmentStatus] = useState(null)
  const [plans, setPlans] = useState([])
  const [planForm, setPlanForm] = useState({
    petId: '',
    vaccineName: '',
    dueDate: '',
    notes: ''
  })
  const [planStatus, setPlanStatus] = useState(null)

  const loadAppointments = useCallback(async () => {
    if (!vetId) return
    try {
      const data = await api(`/api/appointments/veterinarian/${vetId}`)
      setAppointments(data)
    } catch (err) {
      setAppointmentStatus({ type: 'error', text: err.message })
    }
  }, [vetId])

  const loadPlans = useCallback(async () => {
    if (!vetId) return
    try {
      const data = await api(`/api/vaccination-plans/veterinarian/${vetId}`)
      setPlans(data)
    } catch (err) {
      setPlanStatus({ type: 'error', text: err.message })
    }
  }, [vetId])

  useEffect(() => {
    if (vetId) {
      loadAppointments()
      loadPlans()
    }
  }, [vetId, loadAppointments, loadPlans])

  const handleAppointmentChange = (event) => {
    const { name, value } = event.target
    setAppointmentForm((prev) => ({ ...prev, [name]: value }))
  }

  const handlePlanChange = (event) => {
    const { name, value } = event.target
    setPlanForm((prev) => ({ ...prev, [name]: value }))
  }

  const addPatient = async (event) => {
    event.preventDefault()
    if (!petId.trim()) {
      setPatientStatus({ type: 'error', text: 'Ingresa un ID de mascota' })
      return
    }
    setPatientStatus({ type: 'info', text: 'Buscando mascota...' })
    try {
      const pet = await api(`/api/pets/${encodeURIComponent(petId.trim())}`)
      setPatients((prev) => {
        if (prev.some((item) => item.id === pet.id)) return prev
        return [pet, ...prev]
      })
      setPetId('')
      setPatientStatus({ type: 'ok', text: `Mascota ${pet.name} añadida a tu lista` })
    } catch (err) {
      setPatientStatus({ type: 'error', text: err.message })
    }
  }

  const createAppointment = async (event) => {
    event.preventDefault()
    if (!vetId) {
      setAppointmentStatus({ type: 'error', text: 'No se encontró tu ID de veterinario.' })
      return
    }
    setAppointmentStatus({ type: 'info', text: 'Creando cita...' })
    try {
      await api('/api/appointments', {
        method: 'POST',
        body: JSON.stringify({
          ownerId: appointmentForm.ownerId,
          petId: appointmentForm.petId,
          veterinarianId: vetId,
          type: appointmentForm.type,
          appointmentDate: appointmentForm.appointmentDate,
          reason: appointmentForm.reason,
          vaccineId: appointmentForm.vaccineId || null
        })
      })
      setAppointmentStatus({ type: 'ok', text: 'Cita agendada' })
      setAppointmentForm({ ownerId: '', petId: '', type: 'CONSULTATION', appointmentDate: '', reason: '', vaccineId: '' })
      await loadAppointments()
    } catch (err) {
      setAppointmentStatus({ type: 'error', text: err.message })
    }
  }

  const removeAppointment = async (id) => {
    try {
      await api(`/api/appointments/${id}`, { method: 'DELETE' })
      setAppointmentStatus({ type: 'ok', text: 'Cita cancelada' })
      await loadAppointments()
    } catch (err) {
      setAppointmentStatus({ type: 'error', text: err.message })
    }
  }

  const createPlan = async (event) => {
    event.preventDefault()
    if (!vetId) {
      setPlanStatus({ type: 'error', text: 'No se encontró tu ID de veterinario.' })
      return
    }
    setPlanStatus({ type: 'info', text: 'Guardando plan...' })
    try {
      await api('/api/vaccination-plans', {
        method: 'POST',
        body: JSON.stringify({
          petId: planForm.petId,
          veterinarianId: vetId,
          vaccineName: planForm.vaccineName,
          dueDate: planForm.dueDate,
          notes: planForm.notes
        })
      })
      setPlanStatus({ type: 'ok', text: 'Plan registrado' })
      setPlanForm({ petId: '', vaccineName: '', dueDate: '', notes: '' })
      await loadPlans()
    } catch (err) {
      setPlanStatus({ type: 'error', text: err.message })
    }
  }

  const completePlan = async (id) => {
    try {
      await api(`/api/vaccination-plans/${id}/complete`, { method: 'POST' })
      setPlanStatus({ type: 'ok', text: 'Plan marcado como completado' })
      await loadPlans()
    } catch (err) {
      setPlanStatus({ type: 'error', text: err.message })
    }
  }

  const deletePlan = async (id) => {
    try {
      await api(`/api/vaccination-plans/${id}`, { method: 'DELETE' })
      setPlanStatus({ type: 'ok', text: 'Plan eliminado' })
      await loadPlans()
    } catch (err) {
      setPlanStatus({ type: 'error', text: err.message })
    }
  }

  return (
    <div className="dashboard vet-theme">
      <header>
        <div>
          <p className="eyebrow">Veterinario</p>
          <h2>Bienvenido, {user.fullName}</h2>
          <p className="muted">
            Tu ID de veterinario es: <strong>{vetId ?? 'No disponible'}</strong>
          </p>
        </div>
        <div className="header-actions">
          <button className="ghost" onClick={() => { loadAppointments(); loadPlans() }}>
            Refrescar
          </button>
          <button className="ghost" onClick={onLogout}>
            Cerrar sesión
          </button>
        </div>
      </header>

      <section className="card">
        <h3>Agregar animal por ID</h3>
        <form className="inline" onSubmit={addPatient}>
          <input
            placeholder="Ej: 6743f1..."
            value={petId}
            onChange={(event) => setPetId(event.target.value)}
          />
          <button type="submit">Añadir</button>
        </form>
        <StatusMessage status={patientStatus} />
        {patients.length > 0 && (
          <ul className="items-list">
            {patients.map((pet) => (
              <li key={pet.id}>
                <strong>{pet.name}</strong>
                <span>ID: {pet.id}</span>
                <span>Dueño: {pet.ownerId}</span>
              </li>
            ))}
          </ul>
        )}
      </section>

      <section className="card">
        <h3>Agendar cita</h3>
        <form className="grid-two" onSubmit={createAppointment}>
          <label>
            ID Dueño
            <input name="ownerId" value={appointmentForm.ownerId} onChange={handleAppointmentChange} required />
          </label>
          <label>
            ID Mascota
            <input name="petId" value={appointmentForm.petId} onChange={handleAppointmentChange} required />
          </label>
          <label>
            Tipo
            <select name="type" value={appointmentForm.type} onChange={handleAppointmentChange}>
              {appointmentTypes.map((type) => (
                <option key={type.value} value={type.value}>
                  {type.label}
                </option>
              ))}
            </select>
          </label>
          <label>
            Fecha y hora
            <input
              name="appointmentDate"
              type="datetime-local"
              value={appointmentForm.appointmentDate}
              onChange={handleAppointmentChange}
              required
            />
          </label>
          <label>
            Motivo
            <textarea name="reason" value={appointmentForm.reason} onChange={handleAppointmentChange} />
          </label>
          <label>
            ID Vacuna (si aplica)
            <input name="vaccineId" value={appointmentForm.vaccineId} onChange={handleAppointmentChange} />
          </label>
          <button type="submit">Crear cita</button>
        </form>
        <StatusMessage status={appointmentStatus} />
        <h4>Citas programadas ({appointments.length})</h4>
        {appointments.length === 0 ? (
          <p className="muted">Sin citas programadas.</p>
        ) : (
          <ul className="items-list">
            {appointments
              .slice()
              .sort((a, b) => a.appointmentDate.localeCompare(b.appointmentDate))
              .map((appt) => (
                <li key={appt.id}>
                  <strong>{new Date(appt.appointmentDate).toLocaleString()}</strong>
                  <span>Mascota: {appt.petId}</span>
                  <span>Dueño: {appt.ownerId}</span>
                  <span>Tipo: {appt.type}</span>
                  <button className="ghost" onClick={() => removeAppointment(appt.id)}>
                    Cancelar
                  </button>
                </li>
              ))}
          </ul>
        )}
      </section>

      <section className="card">
        <h3>Plan de vacunación</h3>
        <form className="grid-two" onSubmit={createPlan}>
          <label>
            ID Mascota
            <input name="petId" value={planForm.petId} onChange={handlePlanChange} required />
          </label>
          <label>
            Vacuna
            <input name="vaccineName" value={planForm.vaccineName} onChange={handlePlanChange} required />
          </label>
          <label>
            Fecha objetivo
            <input name="dueDate" type="date" value={planForm.dueDate} onChange={handlePlanChange} required />
          </label>
          <label>
            Notas
            <textarea name="notes" value={planForm.notes} onChange={handlePlanChange} />
          </label>
          <button type="submit">Guardar plan</button>
        </form>
        <StatusMessage status={planStatus} />
        {plans.length === 0 ? (
          <p className="muted">No hay planes registrados.</p>
        ) : (
          <ul className="items-list">
            {plans
              .slice()
              .sort((a, b) => a.dueDate.localeCompare(b.dueDate))
              .map((plan) => (
                <li key={plan.id}>
                  <strong>{plan.vaccineName}</strong>
                  <span>Mascota: {plan.petId}</span>
                  <span>Fecha objetivo: {plan.dueDate}</span>
                  <span>Estado: {plan.completed ? 'Completado' : 'Pendiente'}</span>
                  <div className="inline-actions">
                    {!plan.completed && (
                      <button className="ghost" onClick={() => completePlan(plan.id)}>
                        Marcar completado
                      </button>
                    )}
                    <button className="ghost" onClick={() => deletePlan(plan.id)}>
                      Eliminar
                    </button>
                  </div>
                </li>
              ))}
          </ul>
        )}
      </section>
    </div>
  )
}

function App() {
  const [user, setUser] = useState(null)
  const [view, setView] = useState('login')

  const handleLogin = async (credentials) => {
    const data = await api('/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials)
    })
    setUser(data)
  }

  const handleRegister = async (form) => {
    if (form.role === 'OWNER') {
      const payload = {
        fullName: form.fullName,
        email: form.email,
        password: form.password,
        phone: form.phone,
        address: form.address
      }
      const resp = await api('/auth/register-owner', {
        method: 'POST',
        body: JSON.stringify(payload)
      })
      return `Dueño creado con ID ${resp.id}. Inicia sesión para continuar.`
    }

    const payload = {
      fullName: form.fullName,
      email: form.email,
      password: form.password,
      phone: form.phone,
      licenseNumber: form.licenseNumber,
      specialization: form.specialization
    }
    const resp = await api('/auth/register-veterinarian', {
      method: 'POST',
      body: JSON.stringify(payload)
    })
    return `Veterinario creado con ID ${resp.id}. Inicia sesión para continuar.`
  }

  const handleLogout = () => {
    setUser(null)
    setView('login')
  }

  return (
    <div className="app-shell">
      <div className="backdrop" aria-hidden="true">
        <span className="blob blob-one" />
        <span className="blob blob-two" />
        <span className="grid-pattern" />
      </div>
      <div className="app-content">
        {!user ? (
          view === 'login' ? (
            <LoginForm onLogin={handleLogin} goRegister={() => setView('register')} />
          ) : (
            <RegisterForm onRegister={handleRegister} goLogin={() => setView('login')} />
          )
        ) : user.role === 'OWNER' ? (
          <OwnerDashboard user={user} onLogout={handleLogout} />
        ) : user.role === 'VETERINARIAN' ? (
          <VetDashboard user={user} onLogout={handleLogout} />
        ) : (
          <div className="dashboard">
            <header>
              <div>
                <p className="eyebrow">Usuario</p>
                <h2>{user.fullName}</h2>
                <p className="muted">No hay tablero para el rol {user.role}.</p>
              </div>
              <button className="ghost" onClick={handleLogout}>
                Cerrar sesi?n
              </button>
            </header>
          </div>
        )}
      </div>
    </div>
  )
}

export default App
