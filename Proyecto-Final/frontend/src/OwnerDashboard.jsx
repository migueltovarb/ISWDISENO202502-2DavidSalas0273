import { useCallback, useEffect, useState } from 'react'
import './OwnerDashboard.css'

const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080'

const speciesOptions = [
  { value: 'DOG', label: 'Perro' },
  { value: 'CAT', label: 'Gato' },
  { value: 'BIRD', label: 'Ave' },
  { value: 'REPTILE', label: 'Reptil' },
  { value: 'SMALL_MAMMAL', label: 'Pequeño mamífero' },
  { value: 'OTHER', label: 'Otro' }
]

const speciesLabel = {
  DOG: 'Perro', CAT: 'Gato', BIRD: 'Ave', REPTILE: 'Reptil',
  SMALL_MAMMAL: 'Mamifero', OTHER: 'Otro'
}

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

const StatusMessage = ({ status }) => {
  if (!status?.text) return null
  return <p className={`o-status o-status--${status.type}`}>{status.text}</p>
}

export default function OwnerDashboard({ user, onLogout }) {
  const ownerId = user.referenceId
  const [activeTab, setActiveTab] = useState('home')
  const [pets, setPets] = useState([])
  const [appointments, setAppointments] = useState([])
  const [plans, setPlans] = useState({})
  const [certificates, setCertificates] = useState({})
  const [loading, setLoading] = useState(true)
  const [status, setStatus] = useState(null)
  const [formStatus, setFormStatus] = useState(null)
  const [form, setForm] = useState({
    name: '', species: 'DOG', breed: '', birthDate: '', microchipId: '', neutered: 'false'
  })

  const loadData = useCallback(async () => {
    if (!ownerId) return
    setLoading(true)
    try {
      const [petsData, apptData] = await Promise.all([
        api(`/api/pets/owner/${ownerId}`),
        api(`/api/appointments/owner/${ownerId}`)
      ])
      setPets(petsData)
      setAppointments(apptData)
      const planMap = {}, certMap = {}
      await Promise.all(petsData.map(async (pet) => {
        const [planList, certList] = await Promise.all([
          api(`/api/vaccination-plans/pet/${pet.id}`),
          api(`/api/vaccination-certificates/pet/${pet.id}`)
        ])
        planMap[pet.id] = planList
        certMap[pet.id] = certList
      }))
      setPlans(planMap)
      setCertificates(certMap)
      setStatus(null)
    } catch (err) {
      setStatus({ type: 'error', text: err.message })
    } finally {
      setLoading(false)
    }
  }, [ownerId])

  useEffect(() => { loadData() }, [loadData])

  const handleChange = (e) => {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!ownerId) { setFormStatus({ type: 'error', text: 'No se encontró tu ID.' }); return }
    setFormStatus({ type: 'info', text: 'Guardando...' })
    try {
      await api('/api/pets', {
        method: 'POST',
        body: JSON.stringify({
          ownerId, name: form.name, species: form.species,
          breed: form.breed, birthDate: form.birthDate || null,
          microchipId: form.microchipId, neutered: form.neutered === 'true'
        })
      })
      setFormStatus({ type: 'ok', text: 'Mascota registrada correctamente' })
      setForm({ name: '', species: 'DOG', breed: '', birthDate: '', microchipId: '', neutered: 'false' })
      await loadData()
      setActiveTab('pets')
    } catch (err) {
      setFormStatus({ type: 'error', text: err.message })
    }
  }

  const upcoming = appointments
    .filter(a => new Date(a.appointmentDate) >= new Date())
    .sort((a, b) => a.appointmentDate.localeCompare(b.appointmentDate))

  const pendingVaccines = Object.values(plans).flat().filter(p => !p.completed).length

  const tabs = [
    { id: 'home',     label: 'Inicio' },
    { id: 'pets',     label: 'Mis Mascotas' },
    { id: 'appts',    label: 'Citas' },
    { id: 'vaccines', label: 'Vacunas' },
    { id: 'addpet',   label: 'Nueva Mascota' },
  ]

  return (
    <div className="od-shell">
      {/* SIDEBAR */}
      <aside className="od-sidebar">
        <div className="od-sidebar__brand">
          <span className="od-sidebar__logo">VetCare<em>Pro</em></span>
        </div>
        <div className="od-sidebar__profile">
          <div className="od-avatar">{user.fullName?.charAt(0).toUpperCase()}</div>
          <div>
            <p className="od-sidebar__name">{user.fullName}</p>
            <p className="od-sidebar__role">Dueño de mascota</p>
          </div>
        </div>
        <nav className="od-sidebar__nav">
          {tabs.map(tab => (
            <button
              key={tab.id}
              className={`od-nav-item ${activeTab === tab.id ? 'od-nav-item--active' : ''}`}
              onClick={() => setActiveTab(tab.id)}
            >
              <span>{tab.label}</span>
              {tab.id === 'appts' && upcoming.length > 0 && (
                <span className="od-badge">{upcoming.length}</span>
              )}
              {tab.id === 'vaccines' && pendingVaccines > 0 && (
                <span className="od-badge od-badge--warn">{pendingVaccines}</span>
              )}
            </button>
          ))}
        </nav>
        <button className="od-logout" onClick={onLogout}>Cerrar sesión</button>
      </aside>

      {/* MAIN */}
      <main className="od-main">
        {loading && (
          <div className="od-loading">
            <div className="od-spinner"></div>
            <p>Cargando tu información...</p>
          </div>
        )}

        {!loading && activeTab === 'home' && (
          <div className="od-tab-content">
            <div className="od-page-header">
              <h1>Hola, {user.fullName?.split(' ')[0]}</h1>
              <p className="od-page-subtitle">Bienvenido a tu panel de VetCarePro</p>
            </div>

            <div className="od-stats-row">
              <div className="od-stat-card od-stat-card--blue">
                <div className="od-stat-card__value">{pets.length}</div>
                <div className="od-stat-card__label">Mascotas</div>
              </div>
              <div className="od-stat-card od-stat-card--green">
                <div className="od-stat-card__value">{upcoming.length}</div>
                <div className="od-stat-card__label">Citas próximas</div>
              </div>
              <div className="od-stat-card od-stat-card--orange">
                <div className="od-stat-card__value">{pendingVaccines}</div>
                <div className="od-stat-card__label">Vacunas pendientes</div>
              </div>
              <div className="od-stat-card od-stat-card--purple">
                <div className="od-stat-card__value">{Object.values(certificates).flat().length}</div>
                <div className="od-stat-card__label">Certificados</div>
              </div>
            </div>

            {upcoming.length > 0 && (
              <div className="od-section-card">
                <h2 className="od-section-title">Próximas citas</h2>
                <div className="od-appt-list">
                  {upcoming.slice(0, 3).map(appt => (
                    <div key={appt.id} className="od-appt-item">
                      <div className="od-appt-date">
                        <span className="od-appt-day">{new Date(appt.appointmentDate).getDate()}</span>
                        <span className="od-appt-month">{new Date(appt.appointmentDate).toLocaleString('es', { month: 'short' }).toUpperCase()}</span>
                      </div>
                      <div className="od-appt-info">
                        <p className="od-appt-type">{appt.type === 'VACCINATION' ? 'Vacunación' : 'Consulta'}</p>
                        <p className="od-appt-detail">{new Date(appt.appointmentDate).toLocaleTimeString('es', { hour: '2-digit', minute: '2-digit' })}</p>
                      </div>
                      <span className={`od-pill od-pill--${appt.status?.toLowerCase() ?? 'pending'}`}>
                        {appt.status ?? 'Pendiente'}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {pets.length > 0 && (
              <div className="od-section-card">
                <h2 className="od-section-title">Tus mascotas</h2>
                <div className="od-pets-grid">
                  {pets.slice(0, 4).map(pet => (
                    <div key={pet.id} className="od-pet-card" onClick={() => setActiveTab('pets')}>
                      <div className="od-pet-card__icon">{speciesLabel[pet.species] ?? pet.species}</div>
                      <p className="od-pet-card__name">{pet.name}</p>
                      <p className="od-pet-card__breed">{pet.breed || pet.species}</p>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {pets.length === 0 && (
              <div className="od-empty-hero">
                <div className="od-empty-hero__icon">+</div>
                <h3>¡Registra tu primera mascota!</h3>
                <p>Agrega a tu compañero para llevar su historial médico.</p>
                <button className="od-btn od-btn--primary" onClick={() => setActiveTab('addpet')}>
                  Agregar mascota
                </button>
              </div>
            )}
          </div>
        )}

        {!loading && activeTab === 'pets' && (
          <div className="od-tab-content">
            <div className="od-page-header od-page-header--row">
              <div>
                <h1>Mis Mascotas</h1>
                <p className="od-page-subtitle">{pets.length} mascota{pets.length !== 1 ? 's' : ''} registrada{pets.length !== 1 ? 's' : ''}</p>
              </div>
              <button className="od-btn od-btn--primary" onClick={() => setActiveTab('addpet')}>
                + Nueva mascota
              </button>
            </div>

            {pets.length === 0 ? (
              <div className="od-empty">
                <p>Aún no has registrado mascotas.</p>
                <button className="od-btn od-btn--primary" onClick={() => setActiveTab('addpet')}>Agregar mascota</button>
              </div>
            ) : (
              <div className="od-pets-full-grid">
                {pets.map(pet => (
                  <div key={pet.id} className="od-pet-full-card">
                    <div className="od-pet-full-card__header">
                      <span className="od-pet-full-card__icon">{speciesLabel[pet.species] ?? pet.species}</span>
                      <div>
                        <h3>{pet.name}</h3>
                        <p>{speciesOptions.find(s => s.value === pet.species)?.label ?? pet.species}</p>
                      </div>
                    </div>
                    <div className="od-pet-full-card__body">
                      {pet.breed && <div className="od-detail-row"><span>Raza</span><strong>{pet.breed}</strong></div>}
                      {pet.birthDate && <div className="od-detail-row"><span>Nacimiento</span><strong>{pet.birthDate}</strong></div>}
                      {pet.microchipId && <div className="od-detail-row"><span>Microchip</span><strong>{pet.microchipId}</strong></div>}
                      <div className="od-detail-row">
                        <span>Castrado</span>
                        <strong>{pet.neutered ? 'Sí' : 'No'}</strong>
                      </div>
                    </div>
                    <div className="od-pet-full-card__footer">
                      <span className="od-chip">{plans[pet.id]?.filter(p => !p.completed).length ?? 0} vacunas pendientes</span>
                      <span className="od-chip od-chip--green">{certificates[pet.id]?.length ?? 0} certificados</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {!loading && activeTab === 'appts' && (
          <div className="od-tab-content">
            <div className="od-page-header">
              <h1>Mis Citas</h1>
              <p className="od-page-subtitle">{appointments.length} cita{appointments.length !== 1 ? 's' : ''} en total</p>
            </div>

            {appointments.length === 0 ? (
              <div className="od-empty"><p>No hay citas agendadas.</p></div>
            ) : (
              <div className="od-appt-full-list">
                {appointments
                  .slice()
                  .sort((a, b) => a.appointmentDate.localeCompare(b.appointmentDate))
                  .map(appt => {
                    const d = new Date(appt.appointmentDate)
                    const isPast = d < new Date()
                    return (
                      <div key={appt.id} className={`od-appt-full-card ${isPast ? 'od-appt-full-card--past' : ''}`}>
                        <div className="od-appt-full-card__cal">
                          <span className="od-appt-full-card__day">{d.getDate()}</span>
                          <span className="od-appt-full-card__month">{d.toLocaleString('es', { month: 'short' }).toUpperCase()}</span>
                          <span className="od-appt-full-card__year">{d.getFullYear()}</span>
                        </div>
                        <div className="od-appt-full-card__info">
                          <h3>{appt.type === 'VACCINATION' ? 'Vacunación' : 'Consulta'}</h3>
                          <p>{d.toLocaleTimeString('es', { hour: '2-digit', minute: '2-digit' })} hrs</p>
                          {appt.reason && <p className="od-appt-reason">"{appt.reason}"</p>}
                        </div>
                        <span className={`od-pill od-pill--${appt.status?.toLowerCase() ?? 'pending'}`}>
                          {appt.status ?? 'Pendiente'}
                        </span>
                      </div>
                    )
                  })}
              </div>
            )}
          </div>
        )}

        {!loading && activeTab === 'vaccines' && (
          <div className="od-tab-content">
            <div className="od-page-header">
              <h1>Vacunas</h1>
              <p className="od-page-subtitle">Planes y certificados de tus mascotas</p>
            </div>

            {pets.length === 0 ? (
              <div className="od-empty"><p>Registra una mascota para ver su plan de vacunación.</p></div>
            ) : (
              pets.map(pet => (
                <div key={pet.id} className="od-vax-section">
                  <div className="od-vax-section__header">
                    <span className="od-vax-species-tag">{speciesLabel[pet.species] ?? pet.species}</span>
                    <h2>{pet.name}</h2>
                  </div>
                  <div className="od-vax-cols">
                    <div className="od-section-card">
                      <h3 className="od-section-title">Plan programado</h3>
                      {plans[pet.id]?.length ? (
                        <ul className="od-vax-list">
                          {plans[pet.id].map(plan => (
                            <li key={plan.id} className={`od-vax-item ${plan.completed ? 'od-vax-item--done' : ''}`}>
                              <div className="od-vax-item__dot"></div>
                              <div>
                                <strong>{plan.vaccineName}</strong>
                                <p>Fecha: {plan.dueDate}</p>
                                {plan.notes && <p className="od-muted">{plan.notes}</p>}
                              </div>
                              <span className={`od-pill ${plan.completed ? 'od-pill--success' : 'od-pill--warn'}`}>
                                {plan.completed ? 'Completado' : 'Pendiente'}
                              </span>
                            </li>
                          ))}
                        </ul>
                      ) : <p className="od-muted">Sin plan registrado.</p>}
                    </div>
                    <div className="od-section-card">
                      <h3 className="od-section-title">Certificados emitidos</h3>
                      {certificates[pet.id]?.length ? (
                        <ul className="od-cert-list">
                          {certificates[pet.id].map(cert => (
                            <li key={cert.id} className="od-cert-item">
                              <div className="od-cert-item__icon">Cert.</div>
                              <div>
                                <strong>{cert.certificateNumber ?? cert.id}</strong>
                                <p>Emisión: {cert.issueDate}</p>
                                <p>Expira: {cert.expirationDate}</p>
                              </div>
                            </li>
                          ))}
                        </ul>
                      ) : <p className="od-muted">Aún no hay certificados.</p>}
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        )}

        {!loading && activeTab === 'addpet' && (
          <div className="od-tab-content">
            <div className="od-page-header">
              <h1>Registrar nueva mascota</h1>
              <p className="od-page-subtitle">Completa el formulario con la información de tu mascota</p>
            </div>

            <div className="od-form-card">
              <form onSubmit={handleSubmit}>
                <div className="od-form-grid">
                  <div className="od-form-group">
                    <label>Nombre de la mascota <span className="od-required">*</span></label>
                    <input name="name" value={form.name} onChange={handleChange} placeholder="Ej: Max, Luna..." required />
                  </div>
                  <div className="od-form-group">
                    <label>Especie <span className="od-required">*</span></label>
                    <select name="species" value={form.species} onChange={handleChange}>
                      {speciesOptions.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
                    </select>
                  </div>
                  <div className="od-form-group">
                    <label>Raza</label>
                    <input name="breed" value={form.breed} onChange={handleChange} placeholder="Ej: Labrador, Siamés..." />
                  </div>
                  <div className="od-form-group">
                    <label>Fecha de nacimiento</label>
                    <input name="birthDate" type="date" value={form.birthDate} onChange={handleChange} />
                  </div>
                  <div className="od-form-group">
                    <label>Número de microchip</label>
                    <input name="microchipId" value={form.microchipId} onChange={handleChange} placeholder="Opcional" />
                  </div>
                  <div className="od-form-group">
                    <label>¿Está castrado/a?</label>
                    <select name="neutered" value={form.neutered} onChange={handleChange}>
                      <option value="false">No</option>
                      <option value="true">Sí</option>
                    </select>
                  </div>
                </div>
                <StatusMessage status={formStatus} />
                <div className="od-form-actions">
                  <button type="button" className="od-btn od-btn--ghost" onClick={() => setActiveTab('pets')}>
                    Cancelar
                  </button>
                  <button type="submit" className="od-btn od-btn--primary">
                    Guardar mascota
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        <StatusMessage status={status} />
      </main>
    </div>
  )
}
