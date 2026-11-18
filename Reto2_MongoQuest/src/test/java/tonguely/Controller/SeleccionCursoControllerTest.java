package tonguely.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import java.util.HashSet;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tonguely.DAO.CursoDAO;
import tonguely.DAO.InscripcionesDAO;
import tonguely.Excepciones.TonguelyException;
import tonguely.modelo.Inscripciones;
import tonguely.modelo.Usuario;

/**
 * Pruebas de interfaz de "Mis cursos" (los cursos en los que está inscrito el estudiante).
 */
public class SeleccionCursoControllerTest extends PruebaVista {

    /** Estudiante con la sesión iniciada. */
    private Usuario estudiante;

    /**
     * Inicia la sesión, carga la vista y llama a {@code cargarCursos()},
     * igual que hacen la pantalla principal y el botón "Volver" de las actividades.
     *
     * @param stage ventana que crea TestFX
     * @throws Exception si MongoDB no está arrancado o falla el FXML
     */
    @Override
    public void start(Stage stage) throws Exception {
        estudiante = DatosPrueba.iniciarEstudiante();
        SeleccionCursoController controlador = mostrar(stage, "seleccionCurso").getController();
        controlador.cargarCursos();
    }

    /**
     * Las tarjetas son exactamente las de los cursos en los que está inscrito.
     *
     * @throws TonguelyException si MongoDB no está arrancado
     */
    @Test
    public void muestraLosCursosInscritos() throws TonguelyException {
        Set<String> esperados = new HashSet<>();
        CursoDAO cursoDAO = new CursoDAO();
        for (Inscripciones i : new InscripcionesDAO().buscarPorEstudiante(estudiante.getId())) {
            esperados.add(cursoDAO.buscarPorId(i.getIdCurso()).getNombre());
        }

        Set<String> mostrados = new HashSet<>();
        for (Node n : lookup(".curso-nombre").queryAll()) {
            mostrados.add(((Label) n).getText());
        }
        assertEquals(esperados, mostrados);
    }

    /** "Entrar al curso" abre las actividades empezando por la primera pregunta. */
    @Test
    public void entrarAlCursoAbreActividades() {
        assertFalse(lookup(".boton-entrar").queryAll().isEmpty(),
                "El estudiante de prueba no tiene cursos: carga bd/datos_prueba.js");
        clickOn(lookup(".boton-entrar").nth(0).queryButton());
        verifyThat("#labelProgreso", isVisible());
        assertTrue(lookup("#labelProgreso").queryAs(Label.class).getText().startsWith("Pregunta 1 de"));
    }
}
