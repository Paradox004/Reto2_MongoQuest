/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import static tonguely.Main.App.scene;

/**
 *
 * @author songo
 */
public class InicioSesionControllerTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("5. Iniciando JavaFX");

        scene = new Scene(loadFXML("inicioSesion_1"), 640, 480);

        System.out.println("6. Creando escena");

        stage.setScene(scene);
        stage.show();

        System.out.println("7. Ventana mostrada");

    }

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

    @Test
        public void testErrorSiEmailSinArroba() {
            clickOn("#emailField").write("nini");
            clickOn("#iniciarSesionButton");
            verifyThat(".dialog-pane", isVisible());
            DialogPane dialogo = lookup(".dialog-pane").queryAs(DialogPane.class);
            assertEquals("El usuario o la contraseña no son correctos.", dialogo.getContentText());
            clickOn(dialogo.lookupButton(ButtonType.OK)); 
        }
        
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
