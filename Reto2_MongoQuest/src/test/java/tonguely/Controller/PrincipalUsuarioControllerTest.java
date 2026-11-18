package tonguely.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tonguely.DAO.InscripcionesDAO;
import tonguely.Excepciones.TonguelyException;
import tonguely.Main.Sesion;
import tonguely.modelo.Usuario;

/**
 * Pruebas de interfaz de la pantalla principal del estudiante.
 */
public class PrincipalUsuarioControllerTest extends PruebaVista {

    /** Estudiante con la sesión iniciada. */
    private Usuario estudiante;

    /**
     * Inicia la sesión del estudiante y carga la pantalla antes de cada test.
     *
     * @param stage ventana que crea TestFX
     * @throws Exception si MongoDB no está arrancado o falla el FXML
     */
    @Override
    public void start(Stage stage) throws Exception {
        estudiante = DatosPrueba.iniciarEstudiante();
        mostrar(stage, "principalUsuario");
    }

    /** La cabecera saluda al estudiante por su nombre y muestra su racha. */
    @Test
    public void muestraBienvenidaYRacha() {
        assertEquals("BIENVENIDO " + estudiante.getNombre() + "!",
                lookup("#bienvenidaLabel").queryAs(Label.class).getText());
        assertEquals(String.valueOf(estudiante.getRacha()),
                lookup("#rachaLabel").queryAs(Label.class).getText());
    }

    /**
     * "Mis cursos" tiene una tarjeta por cada curso en el que está inscrito.
     *
     * @throws TonguelyException si MongoDB no está arrancado
     */
    @Test
    public void muestraUnaTarjetaPorCursoInscrito() throws TonguelyException {
        int inscripciones = new InscripcionesDAO().buscarPorEstudiante(estudiante.getId()).size();
        Parent misCursos = lookup("#misCursos").query();
        assertEquals(inscripciones, misCursos.lookupAll(".curso-card").size());
    }

    /** El botón de perfil abre la pantalla "Mi perfil". */
    @Test
    public void botonPerfilAbrePerfil() {
        clickOn("#perfilButton");
        verifyThat("#tablaInscripciones", isVisible());
    }

    /** "Cerrar sesión" vacía la sesión y vuelve al inicio de sesión. */
    @Test
    public void cerrarSesionVuelveAlLogin() {
        clickOn("#cerrarSesionButton");
        verifyThat("#emailField", isVisible());
        assertNull(Sesion.getUsuarioActual());
    }
}
