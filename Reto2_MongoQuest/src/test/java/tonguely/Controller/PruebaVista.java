package tonguely.Controller;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import tonguely.Main.App;

/**
 * Base común de los tests de interfaz (TestFX) de los controladores.
 *
 * Reúne lo que todos repiten: cargar una vista en la ventana de TestFX,
 * cerrar los avisos de {@link Alertas} y buscar botones por su texto.
 * Requisito: MongoDB arrancado y los datos de {@code bd/datos_prueba.js} cargados.
 */
abstract class PruebaVista extends ApplicationTest {

    /** Ancho de la ventana de prueba (el mismo que usa la app con el catálogo). */
    private static final int ANCHO = 1200;

    /** Alto de la ventana de prueba. */
    private static final int ALTO = 750;

    /**
     * Carga una vista FXML y la muestra en la ventana de TestFX. También asigna
     * {@code App.scene}, porque los controladores la usan para cambiar de pantalla.
     *
     * @param stage ventana que crea TestFX
     * @param fxml  nombre de la vista sin ".fxml"
     * @return el loader, para poder pedirle el controlador
     * @throws IOException si no se puede cargar el FXML
     */
    protected FXMLLoader mostrar(Stage stage, String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/view/" + fxml + ".fxml"));
        Parent raiz = loader.load();
        App.scene = new Scene(raiz, ANCHO, ALTO);
        App.scene.getStylesheets().add(App.HOJA_ESTILOS);
        stage.setScene(App.scene);
        // Traer la ventana al frente: si Windows no le da el foco, lo que escribe TestFX se pierde.
        // "Siempre encima" solo un momento, para que después no tape los avisos (Alert).
        stage.show();
        stage.setAlwaysOnTop(true);
        stage.toFront();
        stage.requestFocus();
        stage.setAlwaysOnTop(false);
        return loader;
    }

    /**
     * Comprueba que hay un aviso abierto y lo devuelve.
     *
     * @return el panel del aviso
     */
    protected DialogPane dialogo() {
        // El aviso se abre en el hilo de JavaFX: se espera hasta 5 s a que aparezca
        try {
            WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS,
                    () -> lookup(".dialog-pane").tryQuery().isPresent());
        } catch (TimeoutException e) {
            // No ha aparecido: verifyThat falla con un mensaje claro
        }
        verifyThat(".dialog-pane", isVisible());
        return lookup(".dialog-pane").queryAs(DialogPane.class);
    }

    /**
     * Cierra el aviso abierto con "Aceptar" y devuelve su mensaje.
     *
     * @return el texto del aviso
     */
    protected String cerrarDialogo() {
        DialogPane dialogo = dialogo();
        String mensaje = dialogo.getContentText();
        clickOn(dialogo.lookupButton(ButtonType.OK));
        // El controlador sigue trabajando en el hilo de JavaFX al cerrarse el aviso: esperar
        WaitForAsyncUtils.waitForFxEvents();
        return mensaje;
    }

    /**
     * Responde a una confirmación de {@link Alertas#confirmar(String)}.
     *
     * @param si true para pulsar "Sí" y false para pulsar "No"
     */
    protected void responderConfirmacion(boolean si) {
        DialogPane dialogo = dialogo();
        clickOn(dialogo.lookupButton(si ? ButtonType.YES : ButtonType.NO));
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Busca un botón por su texto (para los botones creados desde código, que no tienen fx:id).
     *
     * @param texto texto exacto del botón
     * @return el botón
     */
    protected Button boton(String texto) {
        return lookup((Node n) -> n instanceof Button && texto.equals(((Button) n).getText()))
                .queryButton();
    }

    /** Deja la sesión vacía después de cada test para que no se cuele en el siguiente. */
    @AfterEach
    public void vaciarSesion() {
        DatosPrueba.vaciarSesion();
    }
}
