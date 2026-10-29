package tonguely.reto2_mongoquest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

@Override
public void start(Stage stage) throws IOException {
    System.out.println("5. Iniciando JavaFX");

    scene = new Scene(loadFXML("InicioSesion_1"), 640, 480);

    System.out.println("6. Creando escena");

    stage.setScene(scene);
    stage.show();

    System.out.println("7. Ventana mostrada");
}


    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        System.out.println("2. Cargando FXML");
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


    public static void main(String[] args) {
        launch();
    }

}