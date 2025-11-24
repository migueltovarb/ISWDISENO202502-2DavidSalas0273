# VetCarePro - Modo Terminal (API REST + CLI)

Este proyecto está configurado como API REST pura, sin HTML ni vistas. Todo se consume desde terminal (cURL/Postman) o usando el cliente CLI incluido.

## Requisitos
- JDK 17
- Maven

## 1. Arrancar el backend
```bash
mvn clean spring-boot:run
```
(asegúrate de tener `JAVA_HOME` apuntando al JDK 17).

## 2. Endpoints públicos (sin JWT)
- POST `http://localhost:8080/auth/login`
- POST `http://localhost:8080/auth/register-owner`
- POST `http://localhost:8080/auth/register-veterinarian`

Todo lo demás bajo `/api/**` requiere header `Authorization: Bearer <token>`.

## 3. Ejemplos rápidos (cURL)
Login:
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@mail.com","password":"Admin123"}'
```
Registrar dueño (público):
```bash
curl -X POST http://localhost:8080/auth/register-owner \
  -H "Content-Type: application/json" \
  -d '{"email":"dueno@mail.com","password":"12345","fullName":"Juan"}'
```
Listar mascotas (requiere token):
```bash
TOKEN=TOKEN_AQUI
curl -X GET http://localhost:8080/api/pets \
  -H "Authorization: Bearer $TOKEN"
```
Más ejemplos en `src/main/resources/terminal-examples.txt`.

## 4. Cliente CLI en Java
Hay un cliente interactivo en `src/main/java/com/vetcarepro/cli/TerminalClient.java`.

### Ejecutar el CLI
En una terminal aparte (con el backend corriendo):
```bash
mvn -DskipTests compile
java -cp target/classes:target/dependency/* com.vetcarepro.cli.TerminalClient
```
(En Windows usa `;` en vez de `:` para separar rutas del classpath).

### Qué hace el CLI
- Muestra un menú con opciones: login, registrar dueño/veterinario, listar/registrar mascotas, citas, historial, vacunas, etc.
- Al hacer login guarda el JWT en memoria y lo envía en cada petición protegida.
- Imprime la respuesta JSON en la consola.

## 5. Seguridad
- `formLogin()` y `httpBasic()` deshabilitados.
- Rutas públicas: `/auth/**` y `/api/auth/**` (login/registro), `/favicon.ico`, `/actuator/health`, `/error`.
- Todas las rutas `/api/**` exigen JWT (Bearer).

## 6. Respuestas JSON
- El endpoint raíz `/` devuelve JSON con info de la API.
- Errores de autenticación devuelven `{ "error": "Unauthorized" }`.

¡Listo! El sistema está preparado para operar solo desde terminal.
