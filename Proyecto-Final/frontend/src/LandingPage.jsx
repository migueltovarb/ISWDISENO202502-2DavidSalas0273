import { useState, useEffect } from 'react'
import './LandingPage.css'

export default function LandingPage({ onStartLogin, onStartRegister }) {
  const [isNavScrolled, setIsNavScrolled] = useState(false)
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const [currentSlide, setCurrentSlide] = useState(0)
  const [visibleElements, setVisibleElements] = useState(new Set())

  useEffect(() => {
    const handleScroll = () => {
      setIsNavScrolled(window.scrollY > 20)
    }
    window.addEventListener('scroll', handleScroll)
    return () => window.removeEventListener('scroll', handleScroll)
  }, [])

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentSlide(prev => (prev + 1) % 5)
    }, 4500)
    return () => clearInterval(interval)
  }, [])

  useEffect(() => {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          setVisibleElements(prev => new Set([...prev, entry.target]))
          entry.target.classList.add('visible')
        }
      })
    }, { threshold: 0.12 })

    document.querySelectorAll('.reveal').forEach(el => observer.observe(el))
    return () => observer.disconnect()
  }, [])

  const scrollToSection = (sectionId) => {
    const element = document.getElementById(sectionId)
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' })
      setIsMenuOpen(false)
    }
  }

  return (
    <div className="landing">
      {/* NAVBAR */}
      <nav className={`nav ${isNavScrolled ? 'scrolled' : ''}`} role="navigation">
        <a href="#" className="nav-logo">
          <span className="nav-logo-icon">🐾</span>
          VetCare<span>Pro</span>
        </a>
        <ul className={`nav-links ${isMenuOpen ? 'open' : ''}`} id="navLinks">
          <li><a href="#features" onClick={() => scrollToSection('features')}>Características</a></li>
          <li><a href="#services" onClick={() => scrollToSection('services')}>Servicios</a></li>
          <li><a href="#about" onClick={() => scrollToSection('about')}>Acerca de</a></li>
          <li><a href="#contact" onClick={() => scrollToSection('contact')}>Contacto</a></li>
          <li><button onClick={onStartLogin} className="nav-cta">Iniciar Sesión</button></li>
        </ul>
        <button 
          className="burger" 
          id="burger" 
          onClick={() => setIsMenuOpen(!isMenuOpen)}
          aria-label="Abrir menú"
        >
          <span></span><span></span><span></span>
        </button>
      </nav>

      {/* HERO */}
      <section id="hero" className="hero" aria-label="Bienvenida a VetCarePro">
        <div className="hero-bg"></div>
        <div className="hero-leaves" aria-hidden="true">🌿</div>

        <div className="hero-content">
          <div className="hero-badge"><span className="dot"></span> Cuidado veterinario de calidad</div>
          <h1 className="hero-title">Tu clínica veterinaria <br/><em>en línea</em></h1>
          <p className="hero-subtitle">VetCarePro conecta dueños de mascotas con veterinarios profesionales. Citas, consultas y seguimiento médico en un solo lugar.</p>
          <div className="hero-actions">
            <button onClick={onStartRegister} className="btn btn-primary">📋 Crear Cuenta</button>
            <button onClick={onStartLogin} className="btn btn-outline">🔓 Iniciar Sesión</button>
          </div>
          <div className="hero-stats">
            <div className="hero-stat"><div className="num">1000+</div><div className="label">MASCOTAS</div></div>
            <div className="hero-stat"><div className="num">50+</div><div className="label">VETERINARIOS</div></div>
            <div className="hero-stat"><div className="num">4.9★</div><div className="label">CALIFICACIÓN</div></div>
          </div>
        </div>

        <div className="hero-image" aria-hidden="true">
          <div className="hero-animal-card">
            <div className="animal-emoji">🐱</div>
            <h3>Mascotas Cuidadas</h3>
            <p>Con atención profesional</p>
          </div>
        </div>
      </section>

      {/* FEATURES */}
      <section id="features" className="features reveal" aria-label="Características principales">
        <div className="section-header">
          <span className="section-label">✨ CARACTERÍSTICAS</span>
          <h2 className="section-title">Servicios <em>profesionales</em></h2>
          <p className="section-desc">Todo lo que necesitas para mantener a tu mascota saludable.</p>
        </div>
        <div className="features-grid reveal">
          <div className="feature-card">
            <div className="feature-icon">🏥</div>
            <h3>Consultas Veterinarias</h3>
            <p>Conecta con veterinarios calificados desde la comodidad de tu hogar.</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">💉</div>
            <h3>Historial Médico</h3>
            <p>Mantén registro completo de vacunas y tratamientos de tu mascota.</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">📅</div>
            <h3>Citas Agendadas</h3>
            <p>Reserva citas fácilmente con disponibilidad en tiempo real.</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">🔔</div>
            <h3>Recordatorios</h3>
            <p>Notificaciones automáticas para vacunas y chequeos pendientes.</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">📝</div>
            <h3>Recetas Digitales</h3>
            <p>Recibe recetas y prescripciones digitales al instante.</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">👥</div>
            <h3>Comunidad</h3>
            <p>Conecta con otros dueños de mascotas y comparte experiencias.</p>
          </div>
        </div>
      </section>

      {/* SLIDER */}
      <section id="services" className="slider reveal" aria-label="Servicios destacados">
        <div className="section-header">
          <span className="section-label">⭐ DESTACADOS</span>
          <h2 className="section-title">Servicios que <em>transforman</em></h2>
          <p className="section-desc">Conoce lo que otros dueños tienen para decir sobre nuestro servicio.</p>
        </div>
        <div className="slider-wrap reveal">
          <div className="slider-track" style={{ transform: `translateX(-${currentSlide * 100}%)` }}>
            <div className="slide" style={{ background: 'linear-gradient(135deg, #1b4332, #2E7D32)' }}>
              <div className="slide-bg" aria-hidden="true">🐕</div>
              <div className="slide-content">
                <span className="slide-tag">TESTIMONIO</span>
                <h3>Mascota saludable y feliz</h3>
                <p>"VetCarePro me ayudó a cuidar a mi perro Max. Los veterinarios son muy profesionales y atentos. Ahora duermo tranquilo."</p>
              </div>
            </div>
            <div className="slide" style={{ background: 'linear-gradient(135deg, #0d3b47, #006064)' }}>
              <div className="slide-bg" aria-hidden="true">🐰</div>
              <div className="slide-content">
                <span className="slide-tag">TESTIMONIO</span>
                <h3>Atención rápida y eficiente</h3>
                <p>"Las citas son fáciles de agendar. Siempre hay disponibilidad y los veterinarios son muy atentos con mi conejita Luna."</p>
              </div>
            </div>
            <div className="slide" style={{ background: 'linear-gradient(135deg, #4a1942, #6a1b4d)' }}>
              <div className="slide-bg" aria-hidden="true">🐦</div>
              <div className="slide-content">
                <span className="slide-tag">TESTIMONIO</span>
                <h3>Confianza en línea</h3>
                <p>"No esperaba confiar en una consulta por video, pero el veterinario fue excelente. Mi loro está mejor que nunca."</p>
              </div>
            </div>
            <div className="slide" style={{ background: 'linear-gradient(135deg, #3e2723, #5D4037)' }}>
              <div className="slide-bg" aria-hidden="true">🐱</div>
              <div className="slide-content">
                <span className="slide-tag">TESTIMONIO</span>
                <h3>Recordatorios útiles</h3>
                <p>"Los recordatorios automáticos no me dejan olvidar las vacunas. Mi gato Mino está siempre al día con su salud."</p>
              </div>
            </div>
            <div className="slide" style={{ background: 'linear-gradient(135deg, #1a237e, #283593)' }}>
              <div className="slide-bg" aria-hidden="true">🐠</div>
              <div className="slide-content">
                <span className="slide-tag">TESTIMONIO</span>
                <h3>Historial completo</h3>
                <p>"Tener todo el historial médico en un lugar es increíble. Nunca pierdo información importante sobre mis mascotas."</p>
              </div>
            </div>
          </div>
          <div className="slider-controls">
            <button className="slider-btn" onClick={() => setCurrentSlide((prev) => (prev - 1 + 5) % 5)}>‹</button>
            <div className="slider-dots">
              {[0, 1, 2, 3, 4].map(i => (
                <button 
                  key={i}
                  className={`dot-btn ${i === currentSlide ? 'active' : ''}`}
                  onClick={() => setCurrentSlide(i)}
                  aria-label={`Ir a testimonio ${i + 1}`}
                />
              ))}
            </div>
            <button className="slider-btn" onClick={() => setCurrentSlide((prev) => (prev + 1) % 5)}>›</button>
          </div>
        </div>
      </section>

      {/* ABOUT */}
      <section id="about" className="about reveal" aria-label="Acerca de nosotros">
        <div className="about-content">
          <div className="about-text">
            <span className="section-label">🏥 SOBRE NOSOTROS</span>
            <h2 className="section-title">Cuidando mascotas con <em>profesionalismo</em></h2>
            <p>VetCarePro surge de la necesidad de democratizar el acceso a servicios veterinarios de calidad. Nuestro equipo de veterinarios certificados trabaja 24/7 para que tu mascota siempre tenga la mejor atención.</p>
            <p>Con tecnología moderna y un compromiso genuino con el bienestar animal, hemos ayudado a miles de mascotas a vivir vidas más saludables y felices.</p>
            <div className="about-benefits">
              <div className="benefit">
                <span className="benefit-icon">✓</span>
                <span>Veterinarios certificados</span>
              </div>
              <div className="benefit">
                <span className="benefit-icon">✓</span>
                <span>Disponibilidad 24/7</span>
              </div>
              <div className="benefit">
                <span className="benefit-icon">✓</span>
                <span>Precios accesibles</span>
              </div>
              <div className="benefit">
                <span className="benefit-icon">✓</span>
                <span>Seguridad de datos garantizada</span>
              </div>
            </div>
          </div>
          <div className="about-image" aria-hidden="true">
            <div className="about-card">
              <span className="card-icon">👨‍⚕️</span>
              <p>Expertos en salud animal</p>
            </div>
          </div>
        </div>
      </section>

      {/* CONTACT CTA */}
      <section id="contact" className="contact-cta reveal" aria-label="Llamada a la acción">
        <div className="cta-content">
          <h2>¿Listo para cuidar a tu mascota?</h2>
          <p>Únete a miles de dueños satisfechos. Crea tu cuenta hoy mismo.</p>
          <div className="cta-buttons">
            <button onClick={onStartRegister} className="btn btn-primary">Crear Cuenta Gratis 🚀</button>
            <button onClick={onStartLogin} className="btn btn-outline">Ya tengo cuenta</button>
          </div>
        </div>
      </section>

      {/* FOOTER */}
      <footer role="contentinfo">
        <div className="footer-content">
          <div className="footer-section">
            <div className="footer-logo">🐾 VetCarePro</div>
            <p>Cuidando mascotas, mejorando vidas.</p>
          </div>
          <div className="footer-section">
            <h4>Enlaces</h4>
            <ul>
              <li><a href="#features">Características</a></li>
              <li><a href="#services">Servicios</a></li>
              <li><a href="#about">Acerca de</a></li>
            </ul>
          </div>
          <div className="footer-section">
            <h4>Soporte</h4>
            <ul>
              <li><a href="#">Contacto</a></li>
              <li><a href="#">Preguntas Frecuentes</a></li>
              <li><a href="#">Términos de Uso</a></li>
            </ul>
          </div>
        </div>
        <div className="footer-bottom">
          <span>© 2025 VetCarePro. Todos los derechos reservados.</span>
        </div>
      </footer>
    </div>
  )
}
