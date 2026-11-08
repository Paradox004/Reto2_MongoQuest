# Tonguely · MongoQuest

Aplicación de escritorio en Java (JavaFX) con base de datos **MongoDB** para la plataforma de aprendizaje de idiomas **Tonguely**: usuarios, cursos, profesores, inscripciones y actividades.
Reto intermodular 2.3 (**ADT, DIN y SGE**) de 2º DAM.

> Si usas un asistente de IA para trabajar en el proyecto, dale a leer [AGENTS.md](AGENTS.md): tiene el contexto completo y la forma en que queremos que nos ayude.

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
- Los **alumnos** se inscriben en cursos, completan actividades, valoran los cursos y consultan su progreso.
- Estadísticas previstas: progreso medio de los alumnos, usuarios con mayores rachas y alumnos con más progreso.

## Tecnologías

| | Versión |
|---|---|
| Java | 11 |
| JavaFX (`javafx-controls`, `javafx-fxml`) | 13 |
| MongoDB Java Driver (`mongodb-driver-sync`, POJO codec) | 5.13.0 |
| Maven (con `javafx-maven-plugin`) | 3.x |
| MongoDB Community Server | local, `localhost:27017` |
| IDE | Apache NetBeans o Visual Studio Code |
| Diseño de vistas | Scene Builder |
| SGE | Odoo (proyectos, empleados, exportaciones) |

## Arquitectura

La aplicación se separa en capas:

```
Usuario → UI (JavaFX) → Lógica de negocio → Acceso a datos (DAO) → MongoDB
```

| Capa | Descripción | Paquete / carpeta | Estado |
|---|---|---|---|
| UI | Vistas FXML y sus controladores | `src/main/resources/view/`, `tonguely.reto2_mongoquest.Controller` | En desarrollo |
| Lógica de negocio | Reglas y casos de uso (servicios) | `tonguely.reto2_mongoquest.Service` | Pendiente: hoy los controladores llaman directamente a los DAO |
| Acceso a datos | DAO, conexión con MongoDB y log de operaciones | `tonguely.reto2_mongoquest.DAO` | En desarrollo (sin interfaces ni log) |
| Modelo | POJO que mapean los documentos de Mongo | `modelo` | En desarrollo |
| Excepciones | Excepciones propias de la aplicación | `tonguely.reto2_mongoquest.Exception` | Pendiente |
| Base de datos | MongoDB `TonguelyDB` con validaciones de esquema | `docs/` (scripts) | Pendiente: validaciones |

## Estructura del repositorio

```
/
├── AGENTS.md                      # Contexto e instrucciones para asistentes de IA
├── README.MD
├── docs/                          # Documentación, diagramas, BPMN y scripts de Mongo (pendiente de crear)
└── Reto2_MongoQuest/              # Proyecto Maven
    ├── pom.xml
    └── src/main/
        ├── java/
        │   ├── module-info.java
        │   ├── modelo/            # Usuario, Curso, Profesor, Inscripciones, Actividades
        │   └── tonguely/reto2_mongoquest/
        │       ├── App.java       # Punto de entrada y navegación entre vistas
        │       ├── Controller/    # Controladores de las vistas FXML
        │       ├── DAO/           # ConexionMongo y un DAO por colección
        │       └── Exception/     # Excepciones propias (pendiente)
        └── resources/
            ├── view/              # Vistas FXML
            └── style/             # Hojas de estilo CSS (pendiente)
```

## Instalación y ejecución

### 1. Base de datos

1. Instala y arranca MongoDB Community Server en `localhost:27017`.
2. Crea la base de datos `TonguelyDB` con las colecciones `Usuario`, `Cursos`, `Inscripciones` y `Actividades`.
3. _Pendiente: scripts de creación con validación de esquema y datos de ejemplo en `docs/`._

> Para usar transacciones (integridad transaccional en ADT), MongoDB tiene que arrancarse como *replica set*, aunque sea de un solo nodo.

### 2. Configuración

La cadena de conexión está ahora mismo en `DAO/ConexionMongo.java` (`mongodb://localhost:27017`).
_Pendiente: sacarla a un fichero de configuración que no se suba al repositorio._

### 3. Ejecutar la aplicación

Desde la carpeta `Reto2_MongoQuest/`:

```bash
mvn clean javafx:run
```

O desde NetBeans, con **Run Project**.

## Pantallas actuales

| Vista (FXML) | Controlador | Qué hace |
|---|---|---|
| `inicioSesion_1.fxml` | `InicioSesionController` | Inicio de sesión con email y contraseña |
| `registro.fxml` | `RegistroController` | Alta de un usuario nuevo |
| `seleccionCurso.fxml` | `SeleccionCursoController` | Tarjetas con los cursos en los que está inscrito el alumno |
| `actividades.fxml` | `ActividadesController` | Realización de las actividades de un curso (en desarrollo) |

### Diseño de las actividades

Las actividades se pintan con **una sola vista** y **un componente por tipo de ejercicio**; no hay vistas por curso ni por idioma. El contenido sale de la colección `Actividades`.

- Tipos de ejercicio: **TEST** (opción múltiple) y **HUECO** (rellenar el hueco).
- Cada tipo es un componente JavaFX con su propio FXML (`fx:root`) que implementa la interfaz común `EjercicioView`.
- `EjercicioFactory` elige el componente según el campo `tipo` de la actividad.
- La corrección de las respuestas es lógica de negocio y va en `ActividadService`, no en los componentes.

## Manual de uso

_Pendiente: pantallas, navegación y casos de uso._

## Modelo de datos (ADT)

### Diseño previsto

Base de datos `TonguelyDB`:

| Colección | Campos principales | Información embebida / referencias |
|---|---|---|
| `usuarios` | `_id`, `nombre`, `email`, `contrasena`, `racha` | Embebidos: array `idiomas[]` (`nombre`, `nivel`) y array `logros[]` (`nombre`, `descripcion`). Previsto: array `cursos[]` de `_id` |
| `idiomas` | `_id`, `idioma`, `descripcion` | Embebido: array `curso[]` (`nivel`, `descripcion`, `certificado`) con `profesor[]` (`nombre`, `apellidos`, `email`) |
| `cursos` | `_id`, `nombre`, `precio`, `descripcion`, `certificado` | Embebido: array `profesores[]` (`nombre`, `apellidos`, `email`) |
| `profesores` | `_id`, `nombre`, `apellidos`, `email`, `contrasena`, `sueldo`, `horasTrabajo` | Embebido: objeto `idioma` con array `cursos[]` (`nivel`) |
| `inscripciones` | `_id`, `progreso`, `fechaInicio`, `fechaFin`, `estado` | Referencias: `idEstudiante` (usuario) e `idCurso` (curso) |
| `actividades` | `_id`, `idCurso`, `orden`, `tipo`, `enunciado`, `opciones[]`, `respuestaCorrecta` | Referencia: `idCurso` (curso) |

### Estado en el código

Las colecciones que usa hoy la aplicación son `Usuario`, `Cursos`, `Inscripciones` y `Actividades`. Hay que unificar los nombres de colecciones y campos entre el código, la base de datos y este documento.

### Requisitos mínimos del reto

- [x] 5 colecciones relacionadas entre sí (en el diseño)
- [x] Al menos 2 colecciones con información embebida
- [ ] Al menos 1 colección con un array de `_id` que referencie otras colecciones
- [ ] Colecciones creadas con validaciones de esquema
- [ ] Log de operaciones en fichero (fecha, usuario, tipo de operación y errores/excepciones)

### Modelo relacional equivalente

Como referencia, la documentación del grupo incluye el esquema equivalente en PostgreSQL (esquema `tonguely`), con tablas para usuarios, idiomas, logros, profesores, cursos, inscripciones, lecciones, lecciones completadas y valoraciones.

## Diagrama de navegación de vistas (DIN)

_En proceso: diagrama y correspondencia de cada pantalla con su caso de uso (CU-01, CU-02…)._

## Gestión del proyecto y BPMN (SGE)

Hecho en Odoo: configuración de la empresa, empleados y usuarios, proyecto MongoQuest con sus tareas, exportaciones y BPMN del proceso de asignación de tareas a empleados.

_Pendiente: subir a `docs/` el archivo `.bpmn`, su imagen y la documentación de la gestión._

## Documentación

- [x] Contextualización de la empresa
- [x] Casos de uso y entidades
- [x] Diagrama del modelo de datos (borrador)
- [x] SGE (Odoo y BPMN)
- [ ] Diagrama y listado de casos de uso por tipo de usuario
- [ ] Diagrama de navegación de vistas
- [ ] Documento de diseño de la interfaz (aspecto y comportamiento)
- [ ] Javadoc
- [ ] Documento de arquitectura final
