import { useCallback, useEffect, useState } from 'react'
import './VetDashboard.css'

const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080'

const appointmentTypes = [
  { value: 'CONSULTATION', label: 'Consulta' },
  { value: 'VACCINATION', label: 'Vacunación' }
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

const StatusMsg = ({ status }) => {
  if (!status?.text) return null
  return <p className={`vd-status vd-status--${status.type}`}>{status.text}</p>
}

export default function VetDashboard({ user, onLogout }) {
  const vetId = user.referenceId
  const [activeTab, setActiveTab] = useState('home')

  // All pets from API
  const [allPets, setAllPets] = useState([])
  const [petsLoading, setPetsLoading] = useState(false)
  const [petsError, setPetsError] = useState(null)
  const [searchQuery, setSearchQuery] = useState('')

  // Selected pet for quick action
  const [selectedPet, setSelectedPet] = useState(null)
  const [quickAction, setQuickAction] = useState(null) // 'appt' | 'plan'
  const [quickApptForm, setQuickApptForm] = useState({
    type: 'CONSULTATION', appointmentDate: '', reason: '', vaccineId: ''
  })
  const [quickPlanForm, setQuickPlanForm] = useState({
    vaccineName: '', dueDate: '', notes: ''
  })
  const [quickStatus, setQuickStatus] = useState(null)

  // Appointments & plans
  const [appointments, setAppointments] = useState([])
  const [apptStatus, setApptStatus] = useState(null)
  const [plans, setPlans] = useState([])
  const [planStatus, setPlanStatus] = useState(null)
  const [loading, setLoading] = useState(true)

  // Full forms
  const [apptForm, setApptForm] = useState({
    ownerId: '', petId: '', type: 'CONSULTATION',
    appointmentDate: '', reason: '', vaccineId: ''
  })
  const [planForm, setPlanForm] = useState({
    petId: '', vaccineName: '', dueDate: '', notes: ''
  })

  const loadAppointments = useCallback(async () => {
    if (!vetId) return
    try {
      const data = await api(`/api/appointments/veterinarian/${vetId}`)
      setAppointments(Array.isArray(data) ? data : [])
    } catch (err) { setApptStatus({ type: 'error', text: err.message }) }
  }, [vetId])

  const loadPlans = useCallback(async () => {
    if (!vetId) return
    try {
      const data = await api(`/api/vaccination-plans/veterinarian/${vetId}`)
      setPlans(Array.isArray(data) ? data : [])
    } catch (err) { setPlanStatus({ type: 'error', text: err.message }) }
  }, [vetId])

  const loadAllPets = useCallback(async () => {
    setPetsLoading(true)
    setPetsError(null)
    try {
      const data = await api('/api/pets')
      setAllPets(Array.isArray(data) ? data : [])
    } catch (err) {
      setPetsError(err.message)
    } finally {
      setPetsLoading(false)
    }
  }, [])

  useEffect(() => {
    if (vetId) {
      setLoading(true)
      Promise.all([loadAppointments(), loadPlans(), loadAllPets()])
        .finally(() => setLoading(false))
    }
  }, [vetId, loadAppointments, loadPlans, loadAllPets])

  // Filtered pets by search
  const filteredPets = allPets.filter(pet => {
    if (!searchQuery.trim()) return true
    const q = searchQuery.toLowerCase()
    return (
      pet.name?.toLowerCase().includes(q) ||
      pet.species?.toLowerCase().includes(q) ||
      pet.breed?.toLowerCase().includes(q) ||
      speciesLabel[pet.species]?.toLowerCase().includes(q)
    )
  })

  const openQuickAction = (pet, action) => {
    setSelectedPet(pet)
    setQuickAction(action)
    setQuickStatus(null)
    if (action === 'appt') {
      setQuickApptForm({ type: 'CONSULTATION', appointmentDate: '', reason: '', vaccineId: '' })
    } else {
      setQuickPlanForm({ vaccineName: '', dueDate: '', notes: '' })
    }
  }

  const closeQuickAction = () => {
    setSelectedPet(null)
    setQuickAction(null)
    setQuickStatus(null)
  }

  const submitQuickAppt = async (e) => {
    e.preventDefault()
    setQuickStatus({ type: 'info', text: 'Creando cita...' })
    try {
      await api('/api/appointments', {
        method: 'POST',
        body: JSON.stringify({
          ownerId: selectedPet.ownerId,
          petId: selectedPet.id,
          veterinarianId: vetId,
          type: quickApptForm.type,
          appointmentDate: quickApptForm.appointmentDate,
          reason: quickApptForm.reason,
          vaccineId: quickApptForm.vaccineId || null
        })
      })
      setQuickStatus({ type: 'ok', text: `Cita creada para ${selectedPet.name}` })
      await loadAppointments()
      setTimeout(closeQuickAction, 1500)
    } catch (err) { setQuickStatus({ type: 'error', text: err.message }) }
  }

  const submitQuickPlan = async (e) => {
    e.preventDefault()
    setQuickStatus({ type: 'info', text: 'Guardando plan...' })
    try {
      await api('/api/vaccination-plans', {
        method: 'POST',
        body: JSON.stringify({
          petId: selectedPet.id,
          veterinarianId: vetId,
          vaccineName: quickPlanForm.vaccineName,
          dueDate: quickPlanForm.dueDate,
          notes: quickPlanForm.notes
        })
      })
      setQuickStatus({ type: 'ok', text: `Plan registrado para ${selectedPet.name}` })
      await loadPlans()
      setTimeout(closeQuickAction, 1500)
    } catch (err) { setQuickStatus({ type: 'error', text: err.message }) }
  }

  const createAppointment = async (e) => {
    e.preventDefault()
    setApptStatus({ type: 'info', text: 'Creando cita...' })
    try {
      await api('/api/appointments', {
        method: 'POST',
        body: JSON.stringify({
          ownerId: apptForm.ownerId, petId: apptForm.petId,
          veterinarianId: vetId, type: apptForm.type,
          appointmentDate: apptForm.appointmentDate,
          reason: apptForm.reason, vaccineId: apptForm.vaccineId || null
        })
      })
      setApptStatus({ type: 'ok', text: 'Cita agendada correctamente' })
      setApptForm({ ownerId: '', petId: '', type: 'CONSULTATION', appointmentDate: '', reason: '', vaccineId: '' })
      await loadAppointments()
      setActiveTab('appointments')
    } catch (err) { setApptStatus({ type: 'error', text: err.message }) }
  }

  const removeAppointment = async (id) => {
    try {
      await api(`/api/appointments/${id}`, { method: 'DELETE' })
      setApptStatus({ type: 'ok', text: 'Cita cancelada' })
      await loadAppointments()
    } catch (err) { setApptStatus({ type: 'error', text: err.message }) }
  }

  const createPlan = async (e) => {
    e.preventDefault()
    setPlanStatus({ type: 'info', text: 'Guardando plan...' })
    try {
      await api('/api/vaccination-plans', {
        method: 'POST',
        body: JSON.stringify({
          petId: planForm.petId, veterinarianId: vetId,
          vaccineName: planForm.vaccineName, dueDate: planForm.dueDate, notes: planForm.notes
        })
      })
      setPlanStatus({ type: 'ok', text: 'Plan registrado' })
      setPlanForm({ petId: '', vaccineName: '', dueDate: '', notes: '' })
      await loadPlans()
      setActiveTab('vaccines')
    } catch (err) { setPlanStatus({ type: 'error', text: err.message }) }
  }

  const completePlan = async (id) => {
    try {
      await api(`/api/vaccination-plans/${id}/complete`, { method: 'POST' })
      setPlanStatus({ type: 'ok', text: 'Plan completado' })
      await loadPlans()
    } catch (err) { setPlanStatus({ type: 'error', text: err.message }) }
  }

  const deletePlan = async (id) => {
    try {
      await api(`/api/vaccination-plans/${id}`, { method: 'DELETE' })
      setPlanStatus({ type: 'ok', text: 'Plan eliminado' })
      await loadPlans()
    } catch (err) { setPlanStatus({ type: 'error', text: err.message }) }
  }

  const upcomingAppts = appointments
    .filter(a => new Date(a.appointmentDate) >= new Date())
    .sort((a, b) => a.appointmentDate.localeCompare(b.appointmentDate))
  const pendingPlans = plans.filter(p => !p.completed)

  const tabs = [
    { id: 'home',         label: 'Inicio' },
    { id: 'patients',     label: 'Pacientes' },
    { id: 'appointments', label: 'Citas' },
    { id: 'vaccines',     label: 'Vacunacion' },
    { id: 'new-appt',     label: 'Nueva Cita' },
    { id: 'new-plan',     label: 'Nuevo Plan' },
  ]

  return (
    <div className="vd-shell">
      <aside className="vd-sidebar">
        <div className="vd-sidebar__brand">
          <span className="vd-sidebar__logo">VetCare<em>Pro</em></span>
        </div>
        <div className="vd-sidebar__profile">
          <div className="vd-avatar">{user.fullName?.charAt(0).toUpperCase()}</div>
          <div>
            <p className="vd-sidebar__name">{user.fullName}</p>
            <p className="vd-sidebar__role">Veterinario</p>
          </div>
        </div>
        <nav className="vd-sidebar__nav">
          {tabs.map(tab => (
            <button key={tab.id}
              className={`vd-nav-item ${activeTab === tab.id ? 'vd-nav-item--active' : ''}`}
              onClick={() => setActiveTab(tab.id)}
            >
              <span>{tab.label}</span>
              {tab.id === 'appointments' && upcomingAppts.length > 0 && (
                <span className="vd-badge">{upcomingAppts.length}</span>
              )}
              {tab.id === 'vaccines' && pendingPlans.length > 0 && (
                <span className="vd-badge vd-badge--warn">{pendingPlans.length}</span>
              )}
              {tab.id === 'patients' && allPets.length > 0 && (
                <span className="vd-badge vd-badge--teal">{allPets.length}</span>
              )}
            </button>
          ))}
        </nav>
        <button className="vd-logout" onClick={onLogout}>Cerrar sesion</button>
      </aside>

      <main className="vd-main">
        {loading && (
          <div className="vd-loading">
            <div className="vd-spinner"></div>
            <p>Cargando informacion...</p>
          </div>
        )}

        {!loading && activeTab === 'home' && (
          <div className="vd-tab-content">
            <div className="vd-page-header">
              <h1>Bienvenido, Dr. {user.fullName?.split(' ')[0]}</h1>
              <p className="vd-page-subtitle">Panel de gestion veterinaria</p>
            </div>
            <div className="vd-stats-row">
              <div className="vd-stat-card vd-stat-card--teal">
                <div className="vd-stat-card__value">{allPets.length}</div>
                <div className="vd-stat-card__label">Pacientes registrados</div>
              </div>
              <div className="vd-stat-card vd-stat-card--blue">
                <div className="vd-stat-card__value">{upcomingAppts.length}</div>
                <div className="vd-stat-card__label">Proximas citas</div>
              </div>
              <div className="vd-stat-card vd-stat-card--orange">
                <div className="vd-stat-card__value">{pendingPlans.length}</div>
                <div className="vd-stat-card__label">Vacunas pendientes</div>
              </div>
              <div className="vd-stat-card vd-stat-card--green">
                <div className="vd-stat-card__value">{appointments.length}</div>
                <div className="vd-stat-card__label">Citas totales</div>
              </div>
            </div>

            {upcomingAppts.length > 0 && (
              <div className="vd-section-card">
                <h2 className="vd-section-title">Proximas citas</h2>
                <div className="vd-appt-list">
                  {upcomingAppts.slice(0, 4).map(appt => {
                    const d = new Date(appt.appointmentDate)
                    return (
                      <div key={appt.id} className="vd-appt-item">
                        <div className="vd-appt-cal">
                          <span className="vd-appt-day">{d.getDate()}</span>
                          <span className="vd-appt-month">{d.toLocaleString('es', { month: 'short' }).toUpperCase()}</span>
                        </div>
                        <div className="vd-appt-info">
                          <p className="vd-appt-type">{appt.type === 'VACCINATION' ? 'Vacunacion' : 'Consulta'}</p>
                          <p className="vd-appt-detail">{d.toLocaleTimeString('es', { hour: '2-digit', minute: '2-digit' })}</p>
                          {appt.reason && <p className="vd-appt-reason">"{appt.reason}"</p>}
                        </div>
                        <button className="vd-btn vd-btn--danger-ghost" onClick={() => removeAppointment(appt.id)}>
                          Cancelar
                        </button>
                      </div>
                    )
                  })}
                </div>
              </div>
            )}

            <div className="vd-quick-actions">
              <button className="vd-quick-btn" onClick={() => setActiveTab('patients')}>
                <span className="vd-quick-btn__label">Buscar paciente</span>
              </button>
              <button className="vd-quick-btn" onClick={() => setActiveTab('new-appt')}>
                <span className="vd-quick-btn__label">Nueva cita</span>
              </button>
              <button className="vd-quick-btn" onClick={() => setActiveTab('new-plan')}>
                <span className="vd-quick-btn__label">Plan vacunacion</span>
              </button>
            </div>
          </div>
        )}

        {/* ── PACIENTES ── */}
        {!loading && activeTab === 'patients' && (
          <div className="vd-tab-content">
            <div className="vd-page-header">
              <h1>Pacientes</h1>
              <p className="vd-page-subtitle">{allPets.length} mascotas registradas en el sistema</p>
            </div>

            {/* Barra de busqueda */}
            <div className="vd-search-bar">
              <input
                className="vd-search-input"
                placeholder="Buscar por nombre, especie o raza..."
                value={searchQuery}
                onChange={e => setSearchQuery(e.target.value)}
                autoFocus
              />
              {searchQuery && (
                <button className="vd-search-clear" onClick={() => setSearchQuery('')}>X</button>
              )}
              <button className="vd-btn vd-btn--ghost" onClick={loadAllPets} disabled={petsLoading}>
                {petsLoading ? 'Cargando...' : 'Actualizar'}
              </button>
            </div>

            {petsError && <div className="vd-error-msg">{petsError}</div>}

            {/* Contador de resultados */}
            {searchQuery && (
              <p className="vd-search-count">
                {filteredPets.length} resultado{filteredPets.length !== 1 ? 's' : ''} para "{searchQuery}"
              </p>
            )}

            {/* Listado */}
            {petsLoading ? (
              <div className="vd-loading"><div className="vd-spinner"></div><p>Cargando pacientes...</p></div>
            ) : filteredPets.length === 0 ? (
              <div className="vd-empty">
                <p>{searchQuery ? 'No se encontraron mascotas con ese criterio.' : 'No hay mascotas registradas.'}</p>
              </div>
            ) : (
              <div className="vd-patients-table-wrap">
                <table className="vd-patients-table">
                  <thead>
                    <tr>
                      <th>Nombre</th>
                      <th>Especie</th>
                      <th>Raza</th>
                      <th>Nacimiento</th>
                      <th>Castrado</th>
                      <th>ID Dueño</th>
                      <th>Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredPets.map(pet => (
                      <tr key={pet.id}>
                        <td className="vd-pt-name">{pet.name}</td>
                        <td><span className="vd-species-tag">{speciesLabel[pet.species] ?? pet.species}</span></td>
                        <td>{pet.breed || '—'}</td>
                        <td>{pet.birthDate || '—'}</td>
                        <td>{pet.neutered ? 'Si' : 'No'}</td>
                        <td><span className="vd-id-chip">{pet.ownerId?.slice(0,10)}...</span></td>
                        <td className="vd-pt-actions">
                          <button
                            className="vd-btn vd-btn--primary vd-btn--sm"
                            onClick={() => openQuickAction(pet, 'appt')}
                          >
                            + Cita
                          </button>
                          <button
                            className="vd-btn vd-btn--success vd-btn--sm"
                            onClick={() => openQuickAction(pet, 'plan')}
                          >
                            + Vacuna
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {/* Modal rapido */}
            {selectedPet && quickAction && (
              <div className="vd-modal-overlay" onClick={closeQuickAction}>
                <div className="vd-modal" onClick={e => e.stopPropagation()}>
                  <div className="vd-modal__header">
                    <div>
                      <h2>{quickAction === 'appt' ? 'Nueva cita' : 'Nuevo plan de vacunacion'}</h2>
                      <p className="vd-modal__subtitle">
                        Paciente: <strong>{selectedPet.name}</strong>
                        <span className="vd-species-tag" style={{marginLeft:'0.5rem'}}>{speciesLabel[selectedPet.species] ?? selectedPet.species}</span>
                        {selectedPet.breed && <span style={{color:'var(--vd-text-muted)',marginLeft:'0.5rem'}}>· {selectedPet.breed}</span>}
                      </p>
                    </div>
                    <button className="vd-modal__close" onClick={closeQuickAction}>X</button>
                  </div>

                  {quickAction === 'appt' ? (
                    <form onSubmit={submitQuickAppt} className="vd-modal__form">
                      <div className="vd-form-group">
                        <label>Tipo de cita <span className="vd-required">*</span></label>
                        <select value={quickApptForm.type} onChange={e => setQuickApptForm(p => ({...p, type: e.target.value}))}>
                          {appointmentTypes.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
                        </select>
                      </div>
                      <div className="vd-form-group">
                        <label>Fecha y hora <span className="vd-required">*</span></label>
                        <input type="datetime-local" value={quickApptForm.appointmentDate}
                          onChange={e => setQuickApptForm(p => ({...p, appointmentDate: e.target.value}))} required />
                      </div>
                      <div className="vd-form-group">
                        <label>Motivo</label>
                        <textarea rows={2} placeholder="Motivo de la consulta..."
                          value={quickApptForm.reason}
                          onChange={e => setQuickApptForm(p => ({...p, reason: e.target.value}))} />
                      </div>
                      <div className="vd-form-group">
                        <label>ID Vacuna (opcional)</label>
                        <input placeholder="Solo si es vacunacion" value={quickApptForm.vaccineId}
                          onChange={e => setQuickApptForm(p => ({...p, vaccineId: e.target.value}))} />
                      </div>
                      <StatusMsg status={quickStatus} />
                      <div className="vd-form-actions">
                        <button type="button" className="vd-btn vd-btn--ghost" onClick={closeQuickAction}>Cancelar</button>
                        <button type="submit" className="vd-btn vd-btn--primary">Crear cita</button>
                      </div>
                    </form>
                  ) : (
                    <form onSubmit={submitQuickPlan} className="vd-modal__form">
                      <div className="vd-form-group">
                        <label>Vacuna <span className="vd-required">*</span></label>
                        <input placeholder="Ej: Rabia, Parvovirus..." value={quickPlanForm.vaccineName}
                          onChange={e => setQuickPlanForm(p => ({...p, vaccineName: e.target.value}))} required />
                      </div>
                      <div className="vd-form-group">
                        <label>Fecha objetivo <span className="vd-required">*</span></label>
                        <input type="date" value={quickPlanForm.dueDate}
                          onChange={e => setQuickPlanForm(p => ({...p, dueDate: e.target.value}))} required />
                      </div>
                      <div className="vd-form-group">
                        <label>Notas</label>
                        <textarea rows={2} placeholder="Observaciones, dosis..."
                          value={quickPlanForm.notes}
                          onChange={e => setQuickPlanForm(p => ({...p, notes: e.target.value}))} />
                      </div>
                      <StatusMsg status={quickStatus} />
                      <div className="vd-form-actions">
                        <button type="button" className="vd-btn vd-btn--ghost" onClick={closeQuickAction}>Cancelar</button>
                        <button type="submit" className="vd-btn vd-btn--success">Guardar plan</button>
                      </div>
                    </form>
                  )}
                </div>
              </div>
            )}
          </div>
        )}

        {/* ── CITAS ── */}
        {!loading && activeTab === 'appointments' && (
          <div className="vd-tab-content">
            <div className="vd-page-header vd-page-header--row">
              <div>
                <h1>Citas</h1>
                <p className="vd-page-subtitle">{appointments.length} citas en total</p>
              </div>
              <button className="vd-btn vd-btn--primary" onClick={() => setActiveTab('new-appt')}>+ Nueva cita</button>
            </div>
            <StatusMsg status={apptStatus} />
            {appointments.length === 0 ? (
              <div className="vd-empty"><p>No hay citas programadas.</p></div>
            ) : (
              <div className="vd-appt-full-list">
                {appointments.slice().sort((a,b) => a.appointmentDate.localeCompare(b.appointmentDate)).map(appt => {
                  const d = new Date(appt.appointmentDate)
                  const isPast = d < new Date()
                  return (
                    <div key={appt.id} className={`vd-appt-full-card ${isPast ? 'vd-appt-full-card--past' : ''}`}>
                      <div className="vd-appt-full-card__cal">
                        <span className="vd-appt-full-card__day">{d.getDate()}</span>
                        <span className="vd-appt-full-card__month">{d.toLocaleString('es', { month: 'short' }).toUpperCase()}</span>
                      </div>
                      <div className="vd-appt-full-card__info">
                        <h3>{appt.type === 'VACCINATION' ? 'Vacunacion' : 'Consulta'}</h3>
                        <p>{d.toLocaleTimeString('es', { hour: '2-digit', minute: '2-digit' })} hrs</p>
                        <p>Mascota: {appt.petId?.slice(0,10)}... | Dueno: {appt.ownerId?.slice(0,8)}...</p>
                        {appt.reason && <p className="vd-appt-reason">"{appt.reason}"</p>}
                      </div>
                      {!isPast && (
                        <button className="vd-btn vd-btn--danger-ghost" onClick={() => removeAppointment(appt.id)}>
                          Cancelar
                        </button>
                      )}
                    </div>
                  )
                })}
              </div>
            )}
          </div>
        )}

        {/* ── VACUNACION ── */}
        {!loading && activeTab === 'vaccines' && (
          <div className="vd-tab-content">
            <div className="vd-page-header vd-page-header--row">
              <div>
                <h1>Planes de Vacunacion</h1>
                <p className="vd-page-subtitle">{plans.length} planes — {pendingPlans.length} pendientes</p>
              </div>
              <button className="vd-btn vd-btn--primary" onClick={() => setActiveTab('new-plan')}>+ Nuevo plan</button>
            </div>
            <StatusMsg status={planStatus} />
            {plans.length === 0 ? (
              <div className="vd-empty"><p>No hay planes registrados.</p></div>
            ) : (
              <div className="vd-plans-list">
                {plans.slice().sort((a,b) => a.dueDate.localeCompare(b.dueDate)).map(plan => (
                  <div key={plan.id} className={`vd-plan-card ${plan.completed ? 'vd-plan-card--done' : ''}`}>
                    <div className="vd-plan-card__status-bar"></div>
                    <div className="vd-plan-card__body">
                      <div className="vd-plan-card__main">
                        <h3>{plan.vaccineName}</h3>
                        <p>Mascota: {plan.petId?.slice(0,12)}...</p>
                        <p>Fecha objetivo: <strong>{plan.dueDate}</strong></p>
                        {plan.notes && <p className="vd-muted">"{plan.notes}"</p>}
                      </div>
                      <div className="vd-plan-card__actions">
                        <span className={`vd-pill ${plan.completed ? 'vd-pill--success' : 'vd-pill--warn'}`}>
                          {plan.completed ? 'Completado' : 'Pendiente'}
                        </span>
                        {!plan.completed && (
                          <button className="vd-btn vd-btn--success" onClick={() => completePlan(plan.id)}>
                            Marcar completado
                          </button>
                        )}
                        <button className="vd-btn vd-btn--danger-ghost" onClick={() => deletePlan(plan.id)}>
                          Eliminar
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* ── NUEVA CITA ── */}
        {!loading && activeTab === 'new-appt' && (
          <div className="vd-tab-content">
            <div className="vd-page-header">
              <h1>Agendar nueva cita</h1>
              <p className="vd-page-subtitle">O busca un paciente y usa el boton + Cita</p>
            </div>
            <div className="vd-form-card">
              <form onSubmit={createAppointment}>
                <div className="vd-form-grid">
                  <div className="vd-form-group">
                    <label>ID del Dueno <span className="vd-required">*</span></label>
                    <input value={apptForm.ownerId} onChange={e => setApptForm(p => ({...p, ownerId: e.target.value}))} required placeholder="ID del dueno" />
                  </div>
                  <div className="vd-form-group">
                    <label>ID de la Mascota <span className="vd-required">*</span></label>
                    <input value={apptForm.petId} onChange={e => setApptForm(p => ({...p, petId: e.target.value}))} required placeholder="ID de la mascota" />
                  </div>
                  <div className="vd-form-group">
                    <label>Tipo <span className="vd-required">*</span></label>
                    <select value={apptForm.type} onChange={e => setApptForm(p => ({...p, type: e.target.value}))}>
                      {appointmentTypes.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
                    </select>
                  </div>
                  <div className="vd-form-group">
                    <label>Fecha y hora <span className="vd-required">*</span></label>
                    <input type="datetime-local" value={apptForm.appointmentDate} onChange={e => setApptForm(p => ({...p, appointmentDate: e.target.value}))} required />
                  </div>
                  <div className="vd-form-group vd-form-group--full">
                    <label>Motivo</label>
                    <textarea rows={3} placeholder="Motivo..." value={apptForm.reason} onChange={e => setApptForm(p => ({...p, reason: e.target.value}))} />
                  </div>
                  <div className="vd-form-group">
                    <label>ID Vacuna (opcional)</label>
                    <input placeholder="Opcional" value={apptForm.vaccineId} onChange={e => setApptForm(p => ({...p, vaccineId: e.target.value}))} />
                  </div>
                </div>
                <StatusMsg status={apptStatus} />
                <div className="vd-form-actions">
                  <button type="button" className="vd-btn vd-btn--ghost" onClick={() => setActiveTab('appointments')}>Cancelar</button>
                  <button type="submit" className="vd-btn vd-btn--primary">Crear cita</button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* ── NUEVO PLAN ── */}
        {!loading && activeTab === 'new-plan' && (
          <div className="vd-tab-content">
            <div className="vd-page-header">
              <h1>Nuevo Plan de Vacunacion</h1>
              <p className="vd-page-subtitle">O busca un paciente y usa el boton + Vacuna</p>
            </div>
            <div className="vd-form-card">
              <form onSubmit={createPlan}>
                <div className="vd-form-grid">
                  <div className="vd-form-group">
                    <label>ID de la Mascota <span className="vd-required">*</span></label>
                    <input value={planForm.petId} onChange={e => setPlanForm(p => ({...p, petId: e.target.value}))} required placeholder="ID de la mascota" />
                  </div>
                  <div className="vd-form-group">
                    <label>Vacuna <span className="vd-required">*</span></label>
                    <input value={planForm.vaccineName} onChange={e => setPlanForm(p => ({...p, vaccineName: e.target.value}))} required placeholder="Ej: Rabia, Parvovirus..." />
                  </div>
                  <div className="vd-form-group">
                    <label>Fecha objetivo <span className="vd-required">*</span></label>
                    <input type="date" value={planForm.dueDate} onChange={e => setPlanForm(p => ({...p, dueDate: e.target.value}))} required />
                  </div>
                  <div className="vd-form-group vd-form-group--full">
                    <label>Notas</label>
                    <textarea rows={3} placeholder="Observaciones, dosis..." value={planForm.notes} onChange={e => setPlanForm(p => ({...p, notes: e.target.value}))} />
                  </div>
                </div>
                <StatusMsg status={planStatus} />
                <div className="vd-form-actions">
                  <button type="button" className="vd-btn vd-btn--ghost" onClick={() => setActiveTab('vaccines')}>Cancelar</button>
                  <button type="submit" className="vd-btn vd-btn--primary">Guardar plan</button>
                </div>
              </form>
            </div>
          </div>
        )}
      </main>
    </div>
  )
}
