import { useState, useEffect } from 'react'
import './AdminDashboard.css'

const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080'

export default function AdminDashboard({ user, onLogout }) {
  const [owners, setOwners] = useState([])
  const [vets, setVets] = useState([])
  const [appointments, setAppointments] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [activeTab, setActiveTab] = useState('overview')

  useEffect(() => {
    fetchAllData()
  }, [])

  const fetchAllData = async () => {
    try {
      setLoading(true)
      setError(null)
      
      const [ownersRes, vetsRes, appointmentsRes] = await Promise.all([
        fetch(`${API_BASE}/api/owners`),
        fetch(`${API_BASE}/api/veterinarians`),
        fetch(`${API_BASE}/api/appointments`)
      ])

      if (!ownersRes.ok) throw new Error(`Error cargando dueños: ${ownersRes.status}`)
      if (!vetsRes.ok) throw new Error(`Error cargando veterinarios: ${vetsRes.status}`)
      if (!appointmentsRes.ok) throw new Error(`Error cargando citas: ${appointmentsRes.status}`)

      const ownersData = await ownersRes.json()
      const vetsData = await vetsRes.json()
      const appointmentsData = await appointmentsRes.json()

      setOwners(Array.isArray(ownersData) ? ownersData : [])
      setVets(Array.isArray(vetsData) ? vetsData : [])
      setAppointments(Array.isArray(appointmentsData) ? appointmentsData : [])
    } catch (err) {
      setError(err.message)
      console.error('Error fetching data:', err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="admin-dashboard">
      <header className="admin-header">
        <div className="header-content">
          <h1>Panel de Administración</h1>
          <p className="subtitle">Sistema de Gestión VetCare Pro</p>
        </div>
        <div className="header-actions">
          <button className="refresh-btn" onClick={fetchAllData} disabled={loading}>
            {loading ? 'Cargando...' : 'Actualizar'}
          </button>
          <span className="admin-badge">ADMIN</span>
          <button className="logout-btn" onClick={onLogout}>
            Cerrar sesión
          </button>
        </div>
      </header>

      <nav className="admin-nav">
        <button
          className={`nav-btn ${activeTab === 'overview' ? 'active' : ''}`}
          onClick={() => setActiveTab('overview')}
        >
          Resumen
        </button>
        <button
          className={`nav-btn ${activeTab === 'owners' ? 'active' : ''}`}
          onClick={() => setActiveTab('owners')}
        >
          Dueños ({owners.length})
        </button>
        <button
          className={`nav-btn ${activeTab === 'vets' ? 'active' : ''}`}
          onClick={() => setActiveTab('vets')}
        >
          Veterinarios ({vets.length})
        </button>
        <button
          className={`nav-btn ${activeTab === 'appointments' ? 'active' : ''}`}
          onClick={() => setActiveTab('appointments')}
        >
          Citas ({appointments.length})
        </button>
      </nav>

      <div className="admin-content">
        {loading && <div className="loading">Cargando datos...</div>}
        {error && <div className="error">Error: {error}</div>}

        {!loading && activeTab === 'overview' && (
          <div className="overview-section">
            <h2>Resumen del Sistema</h2>
            <div className="stats-grid">
              <div className="stat-card stat-card--blue">
                <div className="stat-icon-badge stat-icon-badge--blue">D</div>
                <div className="stat-info">
                  <p className="stat-label">Dueños Registrados</p>
                  <p className="stat-value">{owners.length}</p>
                </div>
              </div>
              <div className="stat-card stat-card--teal">
                <div className="stat-icon-badge stat-icon-badge--teal">V</div>
                <div className="stat-info">
                  <p className="stat-label">Veterinarios</p>
                  <p className="stat-value">{vets.length}</p>
                </div>
              </div>
              <div className="stat-card stat-card--green">
                <div className="stat-icon-badge stat-icon-badge--green">C</div>
                <div className="stat-info">
                  <p className="stat-label">Citas Totales</p>
                  <p className="stat-value">{appointments.length}</p>
                </div>
              </div>
              <div className="stat-card stat-card--purple">
                <div className="stat-icon-badge stat-icon-badge--purple">U</div>
                <div className="stat-info">
                  <p className="stat-label">Usuarios Activos</p>
                  <p className="stat-value">{owners.length + vets.length}</p>
                </div>
              </div>
            </div>
          </div>
        )}

        {!loading && activeTab === 'owners' && (
          <div className="list-section">
            <h2>Dueños de Mascotas</h2>
            {owners.length === 0 ? (
              <p className="empty-state">No hay dueños registrados</p>
            ) : (
              <div className="table-responsive">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Nombre</th>
                      <th>Email</th>
                      <th>Teléfono</th>
                      <th>Dirección</th>
                      <th>Estado</th>
                    </tr>
                  </thead>
                  <tbody>
                    {owners.map((owner) => (
                      <tr key={owner.id}>
                        <td className="bold">{owner.fullName}</td>
                        <td>{owner.email}</td>
                        <td>{owner.phone}</td>
                        <td>{owner.address}</td>
                        <td><span className="badge active">Activo</span></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}

        {!loading && activeTab === 'vets' && (
          <div className="list-section">
            <h2>Veterinarios</h2>
            {vets.length === 0 ? (
              <p className="empty-state">No hay veterinarios registrados</p>
            ) : (
              <div className="table-responsive">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Nombre</th>
                      <th>Email</th>
                      <th>Licencia</th>
                      <th>Especialización</th>
                      <th>Teléfono</th>
                      <th>Estado</th>
                    </tr>
                  </thead>
                  <tbody>
                    {vets.map((vet) => (
                      <tr key={vet.id}>
                        <td className="bold">{vet.fullName}</td>
                        <td>{vet.email}</td>
                        <td>{vet.licenseNumber}</td>
                        <td>{vet.specialization}</td>
                        <td>{vet.phone}</td>
                        <td><span className="badge active">Activo</span></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}

        {!loading && activeTab === 'appointments' && (
          <div className="list-section">
            <h2>Citas del Sistema</h2>
            {appointments.length === 0 ? (
              <p className="empty-state">No hay citas registradas</p>
            ) : (
              <div className="table-responsive">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Mascota</th>
                      <th>Dueño</th>
                      <th>Veterinario</th>
                      <th>Fecha</th>
                      <th>Tipo</th>
                      <th>Estado</th>
                    </tr>
                  </thead>
                  <tbody>
                    {appointments.map((apt) => (
                      <tr key={apt.id}>
                        <td className="bold">{apt.petName}</td>
                        <td>{apt.ownerName}</td>
                        <td>{apt.veterinarianName}</td>
                        <td>{new Date(apt.appointmentDate).toLocaleDateString('es-ES')}</td>
                        <td>{apt.appointmentType}</td>
                        <td>
                          <span className={`badge ${apt.status?.toLowerCase()}`}>
                            {apt.status}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  )
}
