Tarea 3

Tarea 3 con registro y login, junto con docker

Tabla de Contenidos

Configuración
Estructura del Proyecto
Uso
Dependencias
Contacto
Configuración

Prerrequisitos
Docker y Docker Compose instalados en tu sistema
Java Development Kit (JDK) 11 o superior (para desarrollo local)
Ejecutar con Docker Compose
Clona el repositorio.
Navega hasta el directorio del proyecto.
Ejecuta la aplicación con Docker Compose:
docker-compose up
La aplicación estará disponible en http://localhost:8080

Configuración para Desarrollo Local
Clona el repositorio.
Navega hasta el directorio del proyecto.
Compila el proyecto:
./mvnw clean install
Ejecuta la aplicación:
./mvnw spring-boot:run
Estructura del Proyecto

├── src/                  # Archivos fuente
│   ├── main/
│   │   ├── java/         # Código Java
│   │   └── resources/    # Archivos de configuración
│   └── test/             # Archivos de prueba
├── Dockerfile            # Configuración de Docker
├── docker-compose.yml    # Configuración de Docker Compose
├── pom.xml               # Dependencias de Maven
└── README.md             # Este archivo
Uso

Accede a la aplicación en http://localhost:8080 después de iniciarla.

Endpoints de la API:

GET /api/login - Te redirecciona a la página de login
POST /api/register - Crear un nuevo usuario

Dependencias

Spring Boot
Spring Web
Docker


Creado por Ivan Enrique Perez Cadena
