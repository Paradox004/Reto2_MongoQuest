package tonguely.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import tonguely.Main.App;

/**
 * Pruebas de interfaz (TestFX) del caso de uso "Registrarse".
 *
 * Requisito: MongoDB arrancado en {@code localhost:27017}.
 * {@link DatosPrueba} crea antes el usuario {@code ane@gmail.com} para
 * poder probar el error de email repetido.
 */
public class RegistroControllerTest extends ApplicationTest {

    /**
     * Asegura que existe el usuario de prueba antes de los tests.
     *
     * @throws Exception si MongoDB no está arrancado
     */
    @BeforeAll
    public static void prepararDatos() throws Exception {
        DatosPrueba.asegurarUsuarioPrueba();
    }

    /**
     * Carga la vista de registro en la ventana que crea TestFX. También se
     * asigna {@code App.scene}, porque el controlador la usa para cambiar de pantalla.
     *
     * @param stage ventana que crea TestFX
     * @throws IOException si no se puede cargar el FXML
     */
    @Override
    public void start(Stage stage) throws IOException {
        Parent raiz = new FXMLLoader(App.class.getResource("/view/registro.fxml")).load();
        App.scene = new Scene(raiz, 640, 480);
        stage.setScene(App.scene);
        // Traer la ventana al frente: si Windows no le da el foco, lo que escribe TestFX se pierde.
        // "Siempre encima" solo un momento, para que después no tape los avisos (Alert).
        stage.show();
        stage.setAlwaysOnTop(true);
        stage.toFront();
        stage.requestFocus();
        stage.setAlwaysOnTop(false);
    }

    /**
     * Error del usuario: registrarse con un email que ya existe
     * (en mayúsculas, para comprobar que no distingue mayúsculas).
     *
     * Debe aparecer un aviso de error con el mensaje de la excepción
     * DatoDuplicadoException, y los campos deben quedar vacíos para corregirlos.
     */
    @Test
    public void registroConEmailRepetidoMuestraErrorYLimpiaCampos() {
        clickOn("#nombreField").write("Ane Repetida");
        clickOn("#correoField").write(DatosPrueba.EMAIL.toUpperCase());
        clickOn("#contrasenaField").write("abcd");
        clickOn("#crearButton");

        verifyThat(".dialog-pane", isVisible());
        DialogPane dialogo = lookup(".dialog-pane").queryAs(DialogPane.class);
        assertEquals("Creacion del usuario fallida", dialogo.getHeaderText());
        assertTrue(dialogo.getContentText().startsWith("Ya existe un usuario con el email"),
                "Mensaje inesperado: " + dialogo.getContentText());
        clickOn(dialogo.lookupButton(ButtonType.OK));

        // El controlador vacía los campos en el hilo de JavaFX al cerrarse el aviso: esperar a que acabe
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("", lookup("#nombreField").queryAs(TextField.class).getText());
        assertEquals("", lookup("#correoField").queryAs(TextField.class).getText());
    }

    /**
     * Navegación: el botón "Iniciar sesión" lleva a la pantalla de inicio de sesión.
     */
    @Test
    public void botonIniciarSesionVuelveAlLogin() {
        clickOn("#iniciarButton");
        verifyThat("#iniciarSesionButton", isVisible());
    }
}
