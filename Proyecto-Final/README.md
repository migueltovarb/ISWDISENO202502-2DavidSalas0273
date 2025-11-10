# Vet Care Pro

## Descripción general
Vet Care Pro es un backend Spring Boot orientado a clínicas veterinarias. Expone APIs REST para gestionar propietarios, mascotas, veterinarios, citas, vacunas, historial médico y certificados de vacunación. Persiste datos en MongoDB y protege todos los endpoints (excepto autenticación) con JWT y roles (`OWNER`, `VET`, `ADMIN`).

## Cómo ejecutar el backend
1. **Variables de entorno mínimas** (puedes exportarlas en tu shell o colocarlas en un `.env`):
   ```bash
   export MONGODB_URI=mongodb://localhost:27017/vetcarepro
   export JWT_SECRET=bXktc2VjcmV0LWF0LWxlYXN0LTMyLWNoYXJz
   ```
2. **Compilación y pruebas (opcional)**
   ```bash
   cd "proyecto final"
   ./mvnw clean test
   ```
3. **Ejecución**
   ```bash
   cd "proyecto final"
   ./mvnw spring-boot:run
   ```
   El API quedará expuesto en `http://localhost:8080`. Puedes cambiar el puerto con `SERVER_PORT`.

## Variables de entorno relevantes
- `MONGODB_URI` / `MONGODB_DB`: conexión hacia MongoDB.
- `JWT_SECRET`: clave Base64 usada por JWT.
- `JWT_EXPIRATION_MINUTES`: vigencia del token.
- `CERTIFICATE_STORAGE_PATH`: carpeta donde se guardan los PDF; por defecto `certificates/` dentro del proyecto.
- `NOTIFICATION_CHANNELS`: lista separada por comas (`EMAIL,WHATSAPP,SMS`).
- `REMINDER_*`: ventanas de recordatorios para citas y vacunas.
- `GIT_AUTO_PUSH_*`: configuración del watcher de auto push (ver siguiente sección).

## Auto push a Git remoto
- Se incluye un watcher (`com.vetcarepro.service.git.GitAutoPushService`) que usa `WatchService` para monitorear cambios en disco.
- Cuando detecta una modificación, ejecuta `scripts/auto-push.sh` (puedes verlo y editarlo si necesitas ajustes).
- El script hace:
  ```bash
  git add -A
  git commit -m "auto"
  git push origin main
  ```
  Si el remoto `origin` no existe, lo crea apuntando a `https://github.com/migueltovarb/ISWDISENO202502-2DavidSalas0273.git`.
- Para desactivar el mecanismo basta con exportar `GIT_AUTO_PUSH_ENABLED=false` antes de arrancar el backend (en tests viene deshabilitado vía perfil `test`).

## Patrones de diseño aplicados
1. **Builder**: `VaccinationCertificateBuilder` encapsula la construcción del certificado a partir de cita, mascota, propietario, veterinario y vacuna. Se invoca en `VaccinationCertificateService.generateFromAppointment` para garantizar objetos coherentes.
2. **Factory**: `NotificationChannelFactory` resuelve la implementación concreta (`EmailNotificationChannel`, `SmsNotificationChannel`, `WhatsappNotificationChannel`) según el tipo configurado. La fábrica se usa dentro de `NotificationService` para orquestar múltiples canales.
3. **Facade**: `EmailClientFacade` es una fachada sobre `JavaMailSender`, permitiendo que `EmailNotificationChannel` no conozca detalles SMTP.
4. **(Singleton via Spring)**: Servicios como `PdfGeneratorService` funcionan como singletons administrados por Spring, lo que evita múltiples inicializaciones de recursos costosos y garantiza reuse del `certificate.storage-path`.

## Endpoints clave
- `/api/auth/login`, `/api/auth/register/owner`, `/api/auth/register/vet`.
- `/api/owners`, `/api/pets`, `/api/veterinarians`.
- `/api/appointments` (incluye `/complete` para generar certificados PDF automáticamente en citas de vacunación).
- `/api/vaccines`, `/api/medical-history`, `/api/certificates/{id}` para descargar el PDF.

Cada controlador aplica validaciones (`@Valid`) y el `GlobalExceptionHandler` unifica los mensajes de error JSON.
