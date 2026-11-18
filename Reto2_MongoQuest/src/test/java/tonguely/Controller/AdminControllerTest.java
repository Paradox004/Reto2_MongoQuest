package tonguely.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tonguely.Main.Sesion;

/**
 * Pruebas de interfaz del panel de administración (caso de uso del profesor).
 */
public class AdminControllerTest extends PruebaVista {

    /**
     * Inicia la sesión del profesor y carga el panel antes de cada test.
     *
     * @param stage ventana que crea TestFX
     * @throws Exception si MongoDB no está arrancado o falla el FXML
     */
    @Override
    public void start(Stage stage) throws Exception {
        DatosPrueba.iniciarProfesor();
        mostrar(stage, "admin");
    }

    /** El panel tiene las pestañas de cursos, usuarios y log. */
    @Test
    public void muestraLasTresPestanas() {
        TabPane pestanas = lookup(".tab-pane").queryAs(TabPane.class);
        List<String> titulos = new ArrayList<>();
        for (Tab tab : pestanas.getTabs()) {
            titulos.add(tab.getText());
        }
        assertEquals(Arrays.asList("Cursos", "Usuarios", "Log"), titulos);
    }

    /** "Cerrar sesión" vacía la sesión del profesor y vuelve al inicio de sesión. */
    @Test
    public void cerrarSesionVuelveAlLogin() {
        clickOn("#cerrarSesionButton");
        verifyThat("#emailField", isVisible());
        assertNull(Sesion.getProfesorActual());
    }
}
