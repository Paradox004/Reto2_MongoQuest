# Tonguely · MongoQuest

Aplicación de escritorio en Java con base de datos **MongoDB** para gestionar la plataforma de aprendizaje de idiomas **Tonguely**: usuarios, cursos, profesores e inscripciones.
Reto intermodular 2.3 (**ADT, DIN y SGE**) de 2º DAM.

## Equipo

- Anartz
- Christian
- Álvaro

## La empresa

| | |
|---|---|
| **Nombre** | Tonguely |
| **Sector de actividad** | Educación online · enseñanza de idiomas mediante gamificación |
| **Descripción** | Plataforma donde los usuarios realizan cursos y lecciones, ganan logros, mantienen rachas diarias, valoran los cursos y se comunican con tutores u otros alumnos. |

### Modelo de negocio

El negocio se basa en el acceso a cursos y contenidos educativos de distintos idiomas y niveles, pudiendo existir planes o cursos de pago. La gamificación (logros y rachas de estudio) fomenta la constancia de los alumnos.

### Gestión

- Los **administradores** crean, modifican y eliminan cursos y lecciones, y gestionan tutores y usuarios.
- Los **alumnos** se inscriben en cursos, completan lecciones, valoran los cursos y consultan su progreso.
- Estadísticas previstas: progreso medio de los alumnos, usuarios con mayores rachas y alumnos con más progreso.

## Arquitectura

La aplicación está separada en capas:

```
Usuario → UI (escritorio) → Lógica de negocio → Acceso a datos → MongoDB
```

| Capa | Descripción | Paquete / carpeta |
|---|---|---|
| UI | Interfaz de escritorio basada en ventanas | _Pendiente_ |
| Lógica de negocio | Reglas y casos de uso | _Pendiente_ |
| Acceso a datos | Conexión con MongoDB y log de operaciones | _Pendiente_ |
| Base de datos | MongoDB (`TonguelyDb`) con validaciones de esquema | _Pendiente_ |

## Estructura del repositorio

```
/
├── docs/        # Documentación, diagramas y BPMN
├── src/         # Código fuente (estructura por definir)
├── .gitignore
└── README.MD
```

## Requisitos

- JDK _(versión por definir)_
- MongoDB Community Server _(versión por definir)_
- _(Maven / Gradle / Ant, según el proyecto)_
- IDE: Visual Studio Code o Apache NetBeans

## Instalación y ejecución

### 1. Base de datos

_Pendiente: arrancar MongoDB, crear la base de datos `TonguelyDb` y las colecciones con sus validaciones de esquema._

### 2. Configuración

_Pendiente: indicar cómo configurar la cadena de conexión. Las credenciales **no** se suben al repositorio (ver `.gitignore`)._

### 3. Ejecutar la aplicación

_Pendiente._

## Manual de uso

_Pendiente: pantallas, navegación y casos de uso._

## Modelo de datos (ADT)

Base de datos `TonguelyDb`, con 5 colecciones:

| Colección | Campos principales | Información embebida / referencias |
|---|---|---|
| `usuarios` | `_id`, `nombre`, `email`, `contrasena`, `racha` | Embebidos: array `idiomas[]` (`nombre`, `nivel`) y array `logros[]` (`nombre`, `descripcion`) |
| `idiomas` | `_id`, `idioma`, `descripcion` | Embebido: array `curso[]` (`nivel`, `descripcion`, `certificado`) con `profesor[]` (`nombre`, `apellidos`, `email`) |
| `cursos` | `_id`, `nombre`, `precio`, `descripcion`, `certificado` | Embebido: array `profesores[]` (`nombre`, `apellidos`, `email`) |
| `profesores` | `_id`, `nombre`, `apellidos`, `email`, `contrasena`, `sueldo`, `horasTrabajo` | Embebido: objeto `idioma` con array `cursos[]` (`nivel`) |
| `inscripciones` | `_id`, `progreso`, `fechaInicio`, `fechaFin`, `estado` | Referencias: `idEstudiante` (usuario) e `idIdioma.idCurso` (curso) |

### Requisitos mínimos del reto

- [x] 5 colecciones relacionadas entre sí
- [x] Al menos 2 colecciones con información embebida
- [ ] Al menos 1 colección con un array de `_id` que referencie otras colecciones
- [ ] Colecciones creadas con validaciones de esquema
- [ ] Log de operaciones contra la base de datos (fecha, usuario, tipo de operación y errores/excepciones)

### Modelo relacional equivalente

Como referencia, en `docs/` se incluye el esquema equivalente en PostgreSQL (esquema `tonguely`), con tablas para usuarios, idiomas, logros, profesores, cursos, inscripciones, lecciones, lecciones completadas y valoraciones.

## Diagrama de navegación de vistas (DIN)

_Pendiente: diagrama y correspondencia de cada pantalla con su caso de uso._

## Proceso de negocio BPMN (SGE)

Proceso: asignación de tareas a empleados, con alta, modificación y baja de empleados y tareas.

_Pendiente: enlace al archivo BPMN y a la imagen en `docs/`._

## Documentación

- [x] Contextualización de la empresa
- [x] Casos de uso y entidades
- [x] Diagrama del modelo de datos (borrador)
- [ ] Diagrama de navegación de vistas
- [ ] Diagrama BPMN (archivo + imagen)
- [ ] Documento de arquitectura final
