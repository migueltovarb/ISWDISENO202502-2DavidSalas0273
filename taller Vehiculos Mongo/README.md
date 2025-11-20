# Cars Backend

API REST para gestionar vehículos (CRUD) usando Java 21, Spring Boot 3.5.7 y MongoDB.

## Estructura del proyecto

src/main/java/com/example/Backend/
  - controller/
  - service/
  - service/impl/
  - repository/
  - model/
  - dto/
  - mapper/
  - factory/
  - exception/

## Patrones y decisiones de arquitectura

- Repository Pattern: `VehicleRepository extends MongoRepository<Vehicle, String>` para separar la persistencia.
- Service Layer Pattern: `VehicleService` y `VehicleServiceImpl` contienen la lógica de negocio; el controlador delega.
- DTO Pattern: `VehicleDTO` se usa para las operaciones REST, evitando exponer la entidad directamente.
- Mapper Pattern: `VehicleMapper` convierte entre DTO y entidad.
- Factory Pattern: `VehicleFactory` crea entidades desde DTOs; útil para centralizar reglas de creación.
- Optional Pattern: El repository devuelve `Optional<Vehicle>` y el servicio maneja NotFound de forma centralizada.
- Global Exception Handler: `ApiExceptionHandler` con `@ControllerAdvice` para errores 404, 400 y 500.

## Endpoints

POST   /api/vehicles     -> Crear vehículo
GET    /api/vehicles     -> Listar vehículos
GET    /api/vehicles/{id} -> Obtener vehículo por id
PUT    /api/vehicles/{id} -> Actualizar vehículo
DELETE /api/vehicles/{id} -> Eliminar vehículo

Ejemplo JSON para crear:

```
{
  "marca": "Toyota",
  "modelo": "Corolla",
  "anio": 2020,
  "color": "Blanco"
}
```

Ejemplo respuesta (201 Created):

```
{
  "id": "653c3f...",
  "marca": "Toyota",
  "modelo": "Corolla",
  "anio": 2020,
  "color": "Blanco"
}
```

## Cómo ejecutar

1. Asegúrate de tener Java 21 y Maven instalados.
2. Levanta una instancia de MongoDB (localmente en `mongodb://localhost:27017`).
3. Ejecuta desde la raíz del proyecto (`Backend`):

```bash
mvn clean package
mvn spring-boot:run
```

## Menú en consola

Puedes ejecutar el mismo backend en modo interactivo (sin servidor web) pasando propiedades al arrancar:

```bash
mvn clean package
mvn spring-boot:run -DskipTests -Dspring-boot.run.arguments="--app.cli.enabled=true --spring.main.web-application-type=none"
```

Con `app.cli.enabled=true` se activa un `CommandLineRunner` que abre un menú para listar, crear, actualizar y eliminar vehículos usando directamente `VehicleService`. Para salir selecciona la opción `0`.

## Tests

Se incluyen pruebas unitarias con Mockito (servicio), MockMvc (controlador) y pruebas para mapper/factory.
