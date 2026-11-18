package tonguely.Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import tonguely.DAO.ConexionMongo;

/**
 * JavaFX App
 */
public class App extends Application {

    public static Scene scene;

    /**
     * Hoja de estilos común de la aplicación. Se añade a la escena una sola vez:
     * como las pantallas cambian con scene.setRoot(), todas la heredan.
     * Alertas también la usa para que los avisos tengan el mismo aspecto.
     */
    public static final String HOJA_ESTILOS = App.class.getResource("/style/tonguely.css").toExternalForm();

@Override
public void start(Stage stage) throws IOException {

    scene = new Scene(loadFXML("inicioSesion_1"), 640, 480);
    scene.getStylesheets().add(HOJA_ESTILOS);
    stage.setTitle("Tonguely");


    stage.setScene(scene);
    stage.show();

}


    /**
     * JavaFX lo llama al cerrar la ventana: se cierra la conexión con MongoDB
     * para no dejar recursos abiertos.
     */
    @Override
    public void stop() {
        ConexionMongo.cerrar();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    
private static Parent loadFXML(String fxml) throws IOException {

    URL url = App.class.getResource("/view/" + fxml + ".fxml");

    if (url == null) {
        throw new IOException("No se encuentra el archivo FXML: " + fxml);
    }


    FXMLLoader fxmlLoader = new FXMLLoader(url);

    Parent root = fxmlLoader.load();

    return root;
}


    public static void main(String[] args) {
        launch(args);
    }

    
}