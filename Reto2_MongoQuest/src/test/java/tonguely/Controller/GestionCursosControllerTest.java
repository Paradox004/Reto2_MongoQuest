package tonguely.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tonguely.DAO.CursoDAO;
import tonguely.DAO.ProfesorDAO;
import tonguely.Excepciones.TonguelyException;
import tonguely.modelo.Curso;

/**
 * Pruebas de interfaz de la gestión de cursos del administrador:
 * listar, seleccionar, validaciones del formulario, crear y eliminar.
 */
public class GestionCursosControllerTest extends PruebaVista {

    /** Nombre del curso que crea y borra el test de alta y baja. */
    private static final String CURSO_PRUEBA = "Curso TestFX";

    /** Tabla de cursos de la vista. */
    private TableView<Curso> tabla;

    /**
     * Borra el curso de prueba si quedó de una ejecución que falló a medias.
     *
     * @throws TonguelyException si MongoDB no está arrancado
     */
    @BeforeAll
    @AfterAll
    public static void borrarCursoPrueba() throws TonguelyException {
        CursoDAO dao = new CursoDAO();
        for (Curso c : dao.cargarCursos()) {
            if (CURSO_PRUEBA.equals(c.getNombre())) {
                dao.eliminar(c.getId());
            }
        }
    }

    /**
     * Inicia la sesión del profesor y carga la vista antes de cada test.
     *
     * @param stage ventana que crea TestFX
     * @throws Exception si MongoDB no está arrancado o falla el FXML
     */
    @Override
    public void start(Stage stage) throws Exception {
        DatosPrueba.iniciarProfesor();
        mostrar(stage, "gestionCursos");
        tabla = lookup("#tablaCursos").queryTableView();
    }

    /**
     * La tabla muestra todos los cursos y la lista todos los profesores.
     *
     * @throws TonguelyException si MongoDB no está arrancado
     */
    @Test
    public void muestraCursosYProfesores() throws TonguelyException {
        assertEquals(new CursoDAO().cargarCursos().size(), tabla.getItems().size());
        ListView<?> profesores = lookup("#listaProfesores").queryListView();
        assertEquals(new ProfesorDAO().buscarTodos().size(), profesores.getItems().size());
    }

    /** Al elegir un curso se rellena el formulario con sus datos y sus profesores. */
    @Test
    public void seleccionarCursoRellenaFormulario() {
        Curso curso = tabla.getItems().get(0);
        interact(() -> tabla.getSelectionModel().select(curso));

        assertEquals(curso.getNombre(), texto("#nombreField"));
        assertEquals(String.valueOf(curso.getPrecio()), texto("#precioField"));
        ListView<?> profesores = lookup("#listaProfesores").queryListView();
        int esperados = curso.getProfesores() == null ? 0 : curso.getProfesores().size();
        assertEquals(esperados, profesores.getSelectionModel().getSelectedItems().size());
    }

    /** "Nuevo" vacía el formulario y quita la selección de la tabla. */
    @Test
    public void nuevoVaciaElFormulario() {
        interact(() -> tabla.getSelectionModel().select(0));
        clickOn("#nuevoButton");
        assertEquals("", texto("#nombreField"));
        assertEquals("", texto("#precioField"));
        assertTrue(tabla.getSelectionModel().isEmpty());
    }

    /** Error del usuario: guardar sin nombre. */
    @Test
    public void guardarSinNombreAvisa() {
        clickOn("#guardarButton");
        assertEquals("El nombre es obligatorio.", cerrarDialogo());
    }

    /** Error del usuario: el precio tiene que ser un entero mayor o igual que 0. */
    @Test
    public void guardarConPrecioIncorrectoAvisa() {
        clickOn("#nombreField").write(CURSO_PRUEBA);
        clickOn("#precioField").write("abc");
        clickOn("#guardarButton");
        assertEquals("El precio debe ser un número entero (≥ 0).", cerrarDialogo());

        clickOn("#precioField").eraseText(3).write("-5");
        clickOn("#guardarButton");
        assertEquals("El precio debe ser un número entero (≥ 0).", cerrarDialogo());
    }

    /** Error del usuario: no puede haber dos cursos con el mismo nombre. */
    @Test
    public void guardarNombreRepetidoAvisa() {
        String existente = tabla.getItems().get(0).getNombre();
        clickOn("#nombreField").write(existente);
        clickOn("#precioField").write("0");
        clickOn("#guardarButton");
        assertTrue(cerrarDialogo().startsWith("Ya existe un curso con el nombre"));
    }

    /** Error del usuario: eliminar sin haber elegido un curso. */
    @Test
    public void eliminarSinSeleccionAvisa() {
        clickOn("#eliminarButton");
        assertEquals("Selecciona un curso de la tabla.", cerrarDialogo());
    }

    /**
     * Alta y baja completa: se crea un curso, al eliminarlo se cancela la
     * primera vez (sigue en la tabla) y se confirma la segunda (desaparece).
     */
    @Test
    public void crearYEliminarCurso() {
        clickOn("#nombreField").write(CURSO_PRUEBA);
        clickOn("#precioField").write("5");
        clickOn("#descripcionArea").write("Curso creado por el test");
        clickOn("#guardarButton");
        assertTrue(existeEnTabla(CURSO_PRUEBA), "El curso nuevo no aparece en la tabla");

        Curso nuevo = buscarEnTabla(CURSO_PRUEBA);
        interact(() -> tabla.getSelectionModel().select(nuevo));
        clickOn("#eliminarButton");
        responderConfirmacion(false);
        assertTrue(existeEnTabla(CURSO_PRUEBA), "Al pulsar \"No\" el curso no debe borrarse");

        clickOn("#eliminarButton");
        responderConfirmacion(true);
        assertFalse(existeEnTabla(CURSO_PRUEBA), "Al pulsar \"Sí\" el curso debe desaparecer");
    }

    /**
     * @param id fx:id del campo
     * @return el texto del campo
     */
    private String texto(String id) {
        return lookup(id).queryAs(TextField.class).getText();
    }

    /**
     * @param nombre nombre del curso
     * @return el curso de la tabla con ese nombre, o null si no está
     */
    private Curso buscarEnTabla(String nombre) {
        for (Curso c : tabla.getItems()) {
            if (nombre.equals(c.getNombre())) {
                return c;
            }
        }
        return null;
    }

    /**
     * @param nombre nombre del curso
     * @return true si el curso aparece en la tabla
     */
    private boolean existeEnTabla(String nombre) {
        return buscarEnTabla(nombre) != null;
    }
}
