package tonguely.Controller;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import org.testfx.framework.junit5.ApplicationTest;
import tonguely.Main.App;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import static tonguely.Main.App.scene;

/**
 * Pruebas de interfaz (TestFX) de la pantalla de inicio de sesión.
 *
 * Cada test arranca la vista {@code inicioSesion_1.fxml}, simula lo que haría
 * el usuario (escribir en los campos y pulsar el botón) y comprueba el mensaje
 * del {@code Alert} que muestra {@code InicioSesionController}.
 *
 * Requisito para ejecutarlas: MongoDB arrancado en {@code localhost:27017}.
 * El usuario de prueba {@code ane@gmail.com} / {@code 1234} lo crea
 * {@link DatosPrueba} antes de los tests si todavía no existe.
 *
 * @author anartz
 */
public class InicioSesionControllerTest extends ApplicationTest {

    /**
     * Se ejecuta una vez antes de todos los tests: asegura que existe el
     * usuario de prueba, para que los tests no dependan de los datos de cada equipo.
     *
     * @throws Exception si MongoDB no está arrancado
     */
    @BeforeAll
    public static void prepararDatos() throws Exception {
        DatosPrueba.asegurarUsuarioPrueba();
    }

    /**
     * Prepara la ventana antes de cada test: carga la vista de inicio de sesión
     * y la muestra en el {@link Stage} que proporciona TestFX.
     *
     * @param stage ventana que crea TestFX para la prueba.
     * @throws IOException si no se encuentra o no se puede cargar el FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("5. Iniciando JavaFX");

        scene = new Scene(loadFXML("inicioSesion_1"), 640, 480);

        System.out.println("6. Creando escena");

        stage.setScene(scene);
        // Traer la ventana al frente: si Windows no le da el foco, lo que escribe TestFX se pierde.
        // "Siempre encima" solo un momento, para que después no tape los avisos (Alert).
        stage.show();
        stage.setAlwaysOnTop(true);
        stage.toFront();
        stage.requestFocus();
        stage.setAlwaysOnTop(false);

        System.out.println("7. Ventana mostrada");

    }

    /**
     * Caso de uso: inicio de sesión correcto.
     *
     * Comprueba que el botón está habilitado, introduce unas credenciales
     * válidas y verifica que aparece el aviso "Las credenciales coinciden.".
     * Al final cierra el aviso pulsando Aceptar.
     */
    @Test
    public void probarInicio() {
        verifyThat("#iniciarSesionButton", isEnabled());
        clickOn("#emailField").write("ane@gmail.com");
        clickOn("#contrasenaField").write("1234");
        clickOn("#iniciarSesionButton");
        verifyThat(".dialog-pane", isVisible());
            DialogPane dialogo = lookup(".dialog-pane").queryAs(DialogPane.class);
            assertEquals("Las credenciales coinciden.", dialogo.getContentText());
            clickOn(dialogo.lookupButton(ButtonType.OK)); 
    }

    /**
     * Caso de uso: inicio de sesión fallido.
     *
     * Escribe un email sin arroba y sin contraseña, pulsa el botón y verifica
     * que aparece el aviso de error "El usuario o la contraseña no son correctos.".
     * Al final cierra el aviso pulsando Aceptar.
     */
    @Test
        public void testErrorSiEmailSinArroba() {
            clickOn("#emailField").write("nini");
            clickOn("#iniciarSesionButton");
            verifyThat(".dialog-pane", isVisible());
            DialogPane dialogo = lookup(".dialog-pane").queryAs(DialogPane.class);
            assertEquals("El usuario o la contraseña no son correctos.", dialogo.getContentText());
            clickOn(dialogo.lookupButton(ButtonType.OK)); 
        }
        
    /**
     * Carga una vista FXML de la carpeta {@code /view/}.
     * Es una copia del método privado de {@link App}, que no se puede usar
     * desde el test.
     *
     * @param fxml nombre del fichero sin la extensión ".fxml".
     * @return nodo raíz de la vista cargada.
     * @throws IOException si el fichero no existe o tiene errores.
     */
    private static Parent loadFXML(String fxml) throws IOException {
        System.out.println("1. Buscando FXML: " + fxml);

        URL url = App.class.getResource("/view/" + fxml + ".fxml");

        if (url == null) {
            throw new IOException("No se encuentra el archivo FXML: " + fxml);
        }

        System.out.println("2. Archivo FXML encontrado: " + url);

        FXMLLoader fxmlLoader = new FXMLLoader(url);

        System.out.println("3. Cargando FXML...");
        Parent root = fxmlLoader.load();

        System.out.println("4. FXML cargado correctamente");
        return root;
    }
}
