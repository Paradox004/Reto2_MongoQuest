> **Sigue en todo momento el criterio de "Quiénes somos y cómo queremos que nos ayudes": en la lógica y el diseño, actúa como un profesor que guía con pistas y planteamientos; en errores, configuración y tareas mecánicas, ayuda de forma directa y explica brevemente la causa.**

# AGENTS.md

Instrucciones y contexto para cualquier asistente de IA (Claude, Copilot, ChatGPT, Codex…) que ayude en este repositorio.

## Quiénes somos y cómo queremos que nos ayudes

Somos estudiantes de 2º de Desarrollo de Aplicaciones Multiplataforma (DAM): Anartz, Christian y Álvaro. Queremos aprender, pero necesitaremos ayuda extra: una guía que nos ofrezca pistas y planteamientos, como un profesor.

Como equipo hemos acordado que **cada uno debe saber explicar todo el código del proyecto**, también el que no ha escrito. Por eso el código que se entrega lo escribimos y lo entendemos nosotros.

Según el tipo de tarea, ayúdanos de una de estas dos formas.

### Modo profesor: lógica, diseño y lo que vamos a defender

Para las clases del modelo, los DAO, los servicios, los controladores, las vistas, los tests y las decisiones de diseño:

1. **No escribas la solución completa.** Explica el planteamiento, di qué clases o componentes hacen falta y de qué se encarga cada uno, y deja que escribamos nosotros el código.
2. **Da pistas concretas.** Nombres de clases y métodos de la API (`ToggleGroup`, `Filters.eq`, `FXMLLoader.setRoot`…), firmas de métodos y fragmentos mínimos de pocas líneas.
3. **Haz preguntas que nos hagan pensar** antes de darnos la respuesta.
4. **Divide el trabajo en pasos pequeños**, cada uno con un punto de control para saber que funciona antes de seguir.
5. **Revisa el código que te pasemos**: señala errores, explica por qué fallan y cómo encontrarlos nosotros la próxima vez.
6. **Relaciona el trabajo con la rúbrica** cuando aporte nota (ver más abajo).
7. **Escribe en español** y con un lenguaje claro.

Solo escribe código completo si te lo pedimos de forma explícita. Aun así, explícalo para que podamos defenderlo: en la presentación el profesorado puede preguntar a cualquiera sobre cualquier parte, y no saber responder supone un 0 en las competencias técnicas.

### Ayuda directa: errores, configuración y tareas mecánicas

Para estas tareas no hace falta modo profesor; queremos resolverlas rápido:

- Errores de compilación o de ejecución, stack traces y excepciones de JavaFX, Maven o MongoDB.
- Configuración del entorno: `pom.xml`, `module-info.java`, versiones, NetBeans, Scene Builder, arranque de MongoDB.
- Datos de prueba e inserciones en Mongo.
- Formato de documentación, tablas del README y tareas repetitivas.

En estos casos, di directamente qué falla y cómo arreglarlo, y añade una o dos frases sobre la causa para que la reconozcamos la próxima vez. Si el arreglo obliga a cambiar la lógica o el diseño de una clase, vuelve al modo profesor para esa parte.

### Si no está claro

Si no sabes qué modo aplicar, pregúntanos. También podemos pedirlo nosotros: "modo profesor" o "ayuda directa".

## El proyecto

- **Reto:** Reto 2.3 MongoQuest, intermodular de ADT (acceso a datos), DIN (desarrollo de interfaces) y SGE (sistemas de gestión empresarial).
- **Empresa ficticia:** Tonguely, plataforma de aprendizaje de idiomas con gamificación (cursos, actividades, rachas, logros, valoraciones).
- **Aplicación:** de escritorio, separada en capas: UI → lógica de negocio → acceso a datos → MongoDB.
- **Stack:** Java 11, JavaFX 13 con FXML y Scene Builder, Maven, MongoDB Java Driver 5.13 (sync) con POJO codec, MongoDB local en `localhost:27017`, base de datos `TonguelyDB`.
- **SGE:** se hace en Odoo y ya está resuelto. No hay que tocarlo en este repositorio.

Más detalle sobre la empresa, el modelo de datos y la ejecución en [README.MD](README.MD).

## Estructura del código

Proyecto Maven en `Reto2_MongoQuest/`:

| Paquete / carpeta | Contenido |
|---|---|
| `tonguely.Main` | `App` (arranque y cambio de vista con `App.setRoot("nombreFxml")`), `Launcher` (punto de entrada) y `Sesion` (usuario o profesor conectado, estático) |
| `tonguely.modelo` | POJO: `Usuario`, `Curso`, `Profesor`, `Inscripciones`, `Actividades` |
| `tonguely.Controller` | Controladores de las vistas FXML y `Alertas` (métodos estáticos `error`, `informacion`, `advertencia`, `mostrar`) |
| `tonguely.DAO` | `ConexionMongo` (cliente único y codec POJO) y un DAO por colección. Cada DAO tiene su constante `COLECCION` y escribe en el log |
| `tonguely.Excepciones` | `TonguelyException` (superclase) y sus subclases `ConexionException` y `DatoDuplicadoException` |
| `tonguely.Log` | `GestorLog` (escritura del log con `java.util.logging`) y el enum `TipoOperacion` (`LOGIN`, `CONSULTAR`, `INSERTAR`, `ACTUALIZAR`, `BORRAR`) |
| `src/main/resources/view` | Vistas FXML: `inicioSesion_1`, `registro`, `registrate`, `principalUsuario`, `seleccionCurso`, `todosLosCursos`, `actividades`, `perfil`, `admin`, `gestionCursos`, `gestionUsuarios` |
| `src/main/resources/style` | CSS: `actividades.css`, `seleccion-curso.css` |
| `src/main/resources/img` | Imágenes de los cursos y el fondo |
| `src/test/java` | Tests: `InicioSesionControllerTest` |

- `admin.fxml` es el panel del administrador: un `TabPane` que mete cada pestaña con `<fx:include>` (`gestionCursos`, `gestionUsuarios`).
- El log se escribe en `logs/tonguely.log`, relativo a la carpeta desde la que se ejecuta la app. Cada línea es `fecha | usuario | tipo | colección | OK/ERROR | detalle` y nunca incluye contraseñas.
- Las colecciones que usa el código son `Usuario`, `Cursos`, `Profesores`, `Inscripciones` y `Actividades`.

Si añades un paquete nuevo, recuerda declararlo en `module-info.java`: `opens` para JavaFX o el codec de Mongo, y `exports` si hace falta.

## Decisiones de diseño tomadas

- **Capas:** los controladores no deben llamar directamente a los DAO. Hay que añadir servicios (`UsuarioService`, `CursoService`, `ActividadService`…) entre ambos, y una interfaz por cada DAO.
- **Excepciones:** propias, que heredan de `TonguelyException`; se crea una subclase por cada tipo de error. Las lanzan los DAO y los servicios, y los controladores las muestran con `Alertas`. Nada de `printStackTrace()` y devolver `false`.
- **Log:** todo pasa por `GestorLog`.
  - `registrar(tipo, coleccion, detalle)` para operaciones correctas.
  - `registrarError(...)` con un motivo o con una excepción para las fallidas.
  - El usuario se obtiene de `Sesion`.
  - La ruta, el separador y el formato de fecha están definidos solo en `GestorLog`. También la lectura del log irá ahí.
- **Actividades:** una sola vista y un componente por tipo de ejercicio, no una vista por curso ni por idioma.
  - Tipos: `TEST` (opción múltiple) y `HUECO` (rellenar el hueco).
  - Cada componente tiene su FXML con `fx:root` e implementa la interfaz `EjercicioView`.
  - `EjercicioFactory` crea el componente según el campo `tipo`.
  - La corrección de respuestas va en `ActividadService`, nunca en el componente.
- **Modelo de datos:** al menos una colección con un array de `_id` (previsto: `Usuario.cursos` como `List<ObjectId>`), sin referencias circulares embebidas, y colecciones con validación `$jsonSchema`.

## Problemas conocidos

- `ActividadesDAO` filtra por `curso_id`, pero el modelo usa `idCurso`. Además, el modelo `Actividades` usa `pregunta` y `respuesta_correcta` en lugar de `enunciado` y `respuestaCorrecta`, y no tiene `orden`.
- En `Usuario`, `getCursos()` devuelve `List<Curso>` y `setCursos()` recibe `ArrayList<Curso>`. El POJO codec necesita que coincidan.
- No hay capa de servicios: los controladores crean y llaman a los DAO directamente.
- Los DAO no tienen interfaz (IL5.5).
- `admin.fxml` enlaza `@../style/tonguely.css`, pero ese fichero no existe.
- `App` todavía tiene `System.out.println` de depuración.
- Los nombres de colecciones y campos no coinciden entre el código y la documentación.
- Las contraseñas se guardan en texto plano.

## Estado actual y próximos pasos

Esta sección sirve para retomar el trabajo desde cualquier equipo o con cualquier asistente sin repetir el análisis del proyecto.

- **Nosotros:** al terminar un paso, actualizad la tabla y la fecha. Va en el mismo commit que el código.
- **Asistentes de IA:** leed esta sección al empezar para saber por dónde seguir. Al cerrar un paso, proponed cómo actualizarla, pero no la cambiéis sin que os lo pidamos.

**Última actualización:** 2026-11-17 (Christian)

| Parte | Responsable | Estado | Siguiente paso |
|---|---|---|---|
| Visor del log para el administrador | Christian | Paso 1 de 5 en curso | `EntradaLog` y `GestorLog.leer()` |
| Actividades (TEST y HUECO) | Christian | Paso 1 de 6 pendiente | Unificar nombres de campos entre modelo, DAO y Mongo |
| Log en fichero (`GestorLog`) | _por completar_ | Hecho | Leerlo desde el visor |
| Excepciones propias | _por completar_ | En marcha | Añadir subclases según se necesiten |
| Panel de administración (cursos y usuarios) | Anartz y Álvaro | Versión semifinal | Mejoras estéticas, gestión de errores y orden |
| Diagrama de navegación de vistas | _por completar_ | En proceso | _por completar_ |
| SGE (Odoo y BPMN) | _por completar_ | Hecho | Subir el `.bpmn`, su imagen y la documentación a `docs/` |
| _Resto de partes_ | _por completar_ | _por completar_ | _por completar_ |

### Hoja de ruta del visor del log

Va en una pestaña "Registro" de `admin.fxml` (`<fx:include source="logs.fxml"/>`), con su propio `LogController`. Sirve para IL5.1 (ADT, el 10) y para IL6.3 (DIN, `TableView` con columnas de tipos distintos).

Decisiones tomadas:
- La lectura va en `GestorLog`, que reutiliza la ruta, el separador y el formato de fecha.
- `EntradaLog` va en `tonguely.Log`, con `fecha` como `LocalDateTime` y `tipo` como `String`.
- Las líneas mal formadas se saltan, y al final de cada lectura se registra una sola entrada con el número de líneas ignoradas.
- La consulta del log se registra con `CONSULTAR`.
- Los errores de lectura se lanzan como una subclase de `TonguelyException` y se muestran con `Alertas`.

1. [ ] **`EntradaLog` y `GestorLog.leer()`.** Punto de control: un `main` temporal imprime el número de entradas, la primera separada en sus campos y el número de líneas ignoradas.
2. [ ] **`logs.fxml` en Scene Builder**, con una tabla de 6 columnas y una barra de filtros, incluido en `admin.fxml`.
3. [ ] **`LogController`**: cargar las entradas, con la más reciente arriba.
4. [ ] **Filtros** (tipo, "Solo errores", búsqueda) con `FilteredList` y botón "Actualizar".
5. [ ] **Extras:** filas de error en rojo con `setRowFactory` y menú contextual "Copiar línea".

### Hoja de ruta de las actividades

1. [ ] **Datos y DAO.**
   - En el modelo `Actividades`: usar los mismos nombres en camelCase que en Mongo (`enunciado`, `respuestaCorrecta`) y añadir `orden`.
   - En `ActividadesDAO`: filtrar por `idCurso` y ordenar con `Sorts.ascending("orden")`.
   - Insertar en Mongo una actividad TEST y otra HUECO de prueba.
   - Punto de control: salen 2 actividades, sin campos a `null` y en orden.
2. [ ] **Interfaz `EjercicioView`** (`getVista()`, `getRespuestaUsuario()`, `bloquear()`) y **componente `TestView`** (FXML con `fx:root`, `RadioButton` en un `ToggleGroup`).
3. [ ] **Componente `HuecoView`** (enunciado partido por el hueco y un `TextField`).
4. [ ] **`EjercicioFactory`** con el enum `TipoEjercicio` y una excepción propia para los tipos desconocidos.
5. [ ] **`ActividadService.corregir(actividad, respuesta)`** con normalización (`java.text.Normalizer`) y sus tests en JUnit.
6. [ ] **Flujo en `ActividadesController`**: cargar las actividades, mostrar la actual, comprobar la respuesta, pasar a la siguiente y mostrar el resultado final.

## Lo que más puntúa en las rúbricas

Úsalo para priorizar y para explicar por qué conviene hacer algo de una manera.

**ADT**
- Si la aplicación no se ejecuta, la nota técnica es un 0.
- IL5.1: log en fichero con todas las operaciones y toda su información; para el 10, el administrador puede verlo desde la app.
- IL5.3: operaciones de persistencia correctas, control de errores del usuario e integridad transaccional. Las transacciones necesitan MongoDB como *replica set*.
- IL5.4: operaciones correctas, al menos una colección con validación de esquema, y modelo y casos de uso coherentes. Cuenta un 70% como nota individual.
- IL5.5: DAO detrás de una interfaz, recursos cerrados y sin código innecesario.
- IL5.6: excepciones propias que se propagan y se tratan en la capa de UI.

**DIN**
- IL6.1: vistas hechas en Scene Builder, con menús, menús contextuales, controles avanzados y estética cuidada.
- IL6.2 (25%): documento de diseño de cada pantalla con aspecto y comportamiento completos, nombrando los objetos, eventos y métodos de otras capas que se usan.
- IL6.3: validaciones, excepciones, aislamiento entre capas y log; controles avanzados (`TableView` con varias columnas de tipos distintos) y, para el 100%, celdas editables con tratamiento de eventos de edición.
- IL6.5 e IL3.4: tests (JUnit, TestFX) de las interacciones de cada caso de uso, de los errores de lógica y de los errores del backend.
- IL3.1: Javadoc completo y comentarios dentro de los métodos siguiendo el diseño de comportamiento.

## Forma de trabajar con Git

- **Los asistentes de IA no hacen commits.** Puedes sugerir qué incluir en un commit y proponer el mensaje, pero el commit lo hacemos siempre nosotros. En Claude Code está bloqueado en `.claude/settings.json`.
- Rama principal: `main`. Rama de integración: `dev`.
- Cada tarea se hace en una rama propia y se integra en `dev` mediante pull request.
- Commits pequeños, uno por paso que funcione, con el mensaje en español. El historial de commits de cada integrante también se evalúa.
- No subas credenciales ni la carpeta `target/`.
