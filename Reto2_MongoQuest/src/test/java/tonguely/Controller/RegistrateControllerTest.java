package tonguely.Controller;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import java.io.IOException;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de interfaz de la pantalla "¡Regístrate antes!", la que ve un invitado
 * al intentar entrar en un curso.
 */
public class RegistrateControllerTest extends PruebaVista {

    /**
     * Carga la vista antes de cada test.
     *
     * @param stage ventana que crea TestFX
     * @throws IOException si no se puede cargar el FXML
     */
    @Override
    public void start(Stage stage) throws IOException {
        mostrar(stage, "registrate");
    }

    /** Navegación: "Regístrate gratis" lleva al formulario de registro. */
    @Test
    public void botonRegistrateLlevaAlRegistro() {
        clickOn("#registrarButton");
        verifyThat("#crearButton", isVisible());
    }

    /** Navegación: "¿Ya tienes una cuenta?" lleva al inicio de sesión. */
    @Test
    public void botonIniciarSesionLlevaAlLogin() {
        clickOn("#iniciarSesionButton");
        verifyThat("#emailField", isVisible());
    }
}
