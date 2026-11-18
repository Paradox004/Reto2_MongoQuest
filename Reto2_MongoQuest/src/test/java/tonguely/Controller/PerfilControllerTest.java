package tonguely.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tonguely.DAO.InscripcionesDAO;
import tonguely.Excepciones.TonguelyException;
import tonguely.Main.Sesion;
import tonguely.modelo.Usuario;

/**
 * Pruebas de interfaz de la pantalla "Mi perfil" del estudiante.
 */
public class PerfilControllerTest extends PruebaVista {

    /** Estudiante con la sesión iniciada. */
    private Usuario estudiante;

    /**
     * Inicia la sesión del estudiante y carga el perfil antes de cada test.
     *
     * @param stage ventana que crea TestFX
     * @throws Exception si MongoDB no está arrancado o falla el FXML
     */
    @Override
    public void start(Stage stage) throws Exception {
        estudiante = DatosPrueba.iniciarEstudiante();
        mostrar(stage, "perfil");
    }

    /** Se ven el nombre, el correo y la racha del estudiante conectado. */
    @Test
    public void muestraDatosDelUsuario() {
        assertEquals(estudiante.getNombre(), lookup("#nombreLabel").queryAs(Label.class).getText());
        assertEquals(estudiante.getEmail(), lookup("#emailLabel").queryAs(Label.class).getText());
        assertEquals(estudiante.getRacha() + " días", lookup("#rachaLabel").queryAs(Label.class).getText());
    }

    /**
     * La tabla tiene una fila por inscripción y la columna "Curso" muestra el
     * nombre del curso (no "-", que es lo que sale si no lo encuentra).
     *
     * @throws TonguelyException si MongoDB no está arrancado
     */
    @Test
    public void tablaTieneUnaFilaPorInscripcion() throws TonguelyException {
        int inscripciones = new InscripcionesDAO().buscarPorEstudiante(estudiante.getId()).size();
        TableView<?> tabla = lookup("#tablaInscripciones").queryTableView();
        assertEquals(inscripciones, tabla.getItems().size());
        for (int i = 0; i < inscripciones; i++) {
            assertNotEquals("-", tabla.getColumns().get(0).getCellData(i));
        }
    }

    /** "Mis cursos" vuelve a la pantalla principal. */
    @Test
    public void volverLlevaALaPantallaPrincipal() {
        clickOn("#volverButton");
        verifyThat("#bienvenidaLabel", isVisible());
    }

    /** "Cerrar sesión" vacía la sesión y vuelve al inicio de sesión. */
    @Test
    public void cerrarSesionVuelveAlLogin() {
        clickOn("#cerrarSesionButton");
        verifyThat("#emailField", isVisible());
        assertNull(Sesion.getUsuarioActual());
    }
}
