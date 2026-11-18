package tonguely.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import tonguely.DAO.InscripcionesDAO;
import tonguely.Excepciones.TonguelyException;
import tonguely.modelo.Curso;
import tonguely.modelo.Usuario;

/**
 * Pruebas de interfaz de la gestión de usuarios del profesor: buscar,
 * ver los datos de un usuario e inscribirlo o quitarlo de sus cursos.
 */
public class GestionUsuariosControllerTest extends PruebaVista {

    /** Estudiante que se inscribe y se desinscribe (en los datos de prueba, Jon). */
    private static final String EMAIL_OTRO = "jon@gmail.com";

    /** Inscripción que crea el test, para deshacerla si falla a medias. */
    private static ObjectId idUsuarioInscrito;

    /** Curso de esa inscripción. */
    private static ObjectId idCursoInscrito;

    /** Tabla de usuarios de la vista. */
    private TableView<Usuario> tabla;

    /** Cursos en común entre el usuario elegido y el profesor. */
    private ListView<Curso> comunes;

    /** Cursos del profesor en los que el usuario todavía no está. */
    private ComboBox<Curso> disponibles;

    /**
     * Deja la base de datos como estaba si el test de inscribir falló a medias.
     *
     * @throws TonguelyException si MongoDB no está arrancado
     */
    @AfterAll
    public static void deshacerInscripcion() throws TonguelyException {
        InscripcionesDAO dao = new InscripcionesDAO();
        if (idUsuarioInscrito != null && dao.estaInscrito(idUsuarioInscrito, idCursoInscrito)) {
            dao.desinscribir(idUsuarioInscrito, idCursoInscrito);
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
        mostrar(stage, "gestionUsuarios");
        tabla = lookup("#tablaUsuarios").queryTableView();
        comunes = lookup("#listaComunes").queryListView();
        disponibles = lookup("#comboDisponibles").queryComboBox();
    }

    /** Sin usuario elegido el panel está vacío y los botones desactivados. */
    @Test
    public void sinSeleccionElPanelEstaVacio() {
        assertEquals("-", etiqueta("#nombreLabel"));
        assertTrue(lookup("#nuevoButton").query().isDisabled());
        assertTrue(lookup("#eliminarButton").query().isDisabled());
    }

    /** El buscador filtra por nombre o por correo, y al vaciarlo vuelven todos. */
    @Test
    public void buscadorFiltraPorNombreOEmail() {
        int total = tabla.getItems().size();
        clickOn("#buscarField").write("jon");
        assertFalse(tabla.getItems().isEmpty());
        for (Usuario u : tabla.getItems()) {
            assertTrue(u.getNombre().toLowerCase().contains("jon")
                    || u.getEmail().toLowerCase().contains("jon"));
        }
        clickOn("#buscarField").eraseText(3);
        assertEquals(total, tabla.getItems().size());
    }

    /**
     * Al elegir un usuario se ven sus datos. Los cursos en común son aquellos
     * en los que está inscrito, y los del desplegable, aquellos en los que no.
     *
     * @throws TonguelyException si MongoDB no está arrancado
     */
    @Test
    public void seleccionarUsuarioMuestraSusDatosYCursos() throws TonguelyException {
        Usuario ane = elegir(DatosPrueba.EMAIL);
        assertEquals(ane.getNombre(), etiqueta("#nombreLabel"));
        assertEquals(ane.getEmail(), etiqueta("#emailLabel"));

        InscripcionesDAO dao = new InscripcionesDAO();
        for (Curso c : comunes.getItems()) {
            assertTrue(dao.estaInscrito(ane.getId(), c.getId()));
        }
        for (Curso c : disponibles.getItems()) {
            assertFalse(dao.estaInscrito(ane.getId(), c.getId()));
        }
    }

    /** Error del usuario: "Quitar del curso" sin elegir un curso de la lista. */
    @Test
    public void quitarSinElegirCursoAvisa() {
        elegir(DatosPrueba.EMAIL);
        if (comunes.getItems().isEmpty()) {
            return; // el botón está desactivado: no hay nada que probar
        }
        clickOn("#eliminarButton");
        assertEquals("Selecciona un curso de la lista de cursos en común.", cerrarDialogo());
    }

    /**
     * Inscribir y quitar: el curso pasa del desplegable a "cursos en común" y,
     * al quitarlo y confirmar, vuelve a su sitio. La base de datos queda como estaba.
     *
     * @throws TonguelyException si MongoDB no está arrancado
     */
    @Test
    public void inscribirYQuitarUsuario() throws TonguelyException {
        Usuario otro = elegir(EMAIL_OTRO);
        assertFalse(disponibles.getItems().isEmpty(),
                EMAIL_OTRO + " ya está en todos los cursos del profesor: carga bd/datos_prueba.js");
        int enComun = comunes.getItems().size();
        Curso curso = disponibles.getItems().get(0);
        idUsuarioInscrito = otro.getId();
        idCursoInscrito = curso.getId();

        // Inscribir
        interact(() -> disponibles.getSelectionModel().select(curso));
        clickOn("#nuevoButton");
        assertEquals(enComun + 1, comunes.getItems().size());
        assertTrue(new InscripcionesDAO().estaInscrito(otro.getId(), curso.getId()));

        // Quitar (con confirmación)
        Curso enLista = null;
        for (Curso c : comunes.getItems()) {
            if (c.getId().equals(curso.getId())) {
                enLista = c;
            }
        }
        final Curso aQuitar = enLista;
        interact(() -> comunes.getSelectionModel().select(aQuitar));
        clickOn("#eliminarButton");
        responderConfirmacion(true);
        assertEquals(enComun, comunes.getItems().size());
        assertFalse(new InscripcionesDAO().estaInscrito(otro.getId(), curso.getId()));
    }

    /**
     * Selecciona en la tabla al usuario con ese email.
     *
     * @param email email del usuario
     * @return el usuario elegido
     */
    private Usuario elegir(String email) {
        Usuario elegido = null;
        for (Usuario u : tabla.getItems()) {
            if (email.equalsIgnoreCase(u.getEmail())) {
                elegido = u;
            }
        }
        assertNotNull(elegido, "No existe el usuario " + email + ": carga bd/datos_prueba.js");
        final Usuario u = elegido;
        interact(() -> tabla.getSelectionModel().select(u));
        return elegido;
    }

    /**
     * @param id fx:id de la etiqueta
     * @return su texto
     */
    private String etiqueta(String id) {
        return lookup(id).queryAs(Label.class).getText();
    }
}
