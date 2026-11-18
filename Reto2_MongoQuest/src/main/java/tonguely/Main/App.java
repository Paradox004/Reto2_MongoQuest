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

    /** Ancho mínimo de la ventana, para que ninguna pantalla quede demasiado pequeña. */
    private static final double ANCHO_MINIMO = 640;

    /** Alto mínimo de la ventana. */
    private static final double ALTO_MINIMO = 480;

@Override
public void start(Stage stage) throws IOException {

    // Sin ancho ni alto: la escena toma el tamaño preferido de la vista (prefWidth/prefHeight del FXML)
    scene = new Scene(loadFXML("inicioSesion_1"));
    scene.getStylesheets().add(HOJA_ESTILOS);
    stage.setTitle("Tonguely");
    stage.setMinWidth(ANCHO_MINIMO);
    stage.setMinHeight(ALTO_MINIMO);

    // Cada vez que cambia la pantalla, la ventana se adapta a la vista nueva.
    // Se escucha la propiedad root de la escena, así sirve tanto para App.setRoot("...")
    // como para los controladores que hacen App.scene.setRoot(root) directamente.
    scene.rootProperty().addListener((propiedad, vistaAnterior, vistaNueva) -> ajustarVentana(stage, vistaNueva));

    stage.setScene(scene);
    stage.show();

}

    /**
     * Ajusta el tamaño de la ventana al tamaño preferido de la vista nueva y la centra.
     * Si el usuario ha maximizado la ventana, se respeta y no se cambia nada.
     *
     * @param stage ventana de la aplicación
     * @param vista vista que se acaba de mostrar
     */
    private static void ajustarVentana(Stage stage, Parent vista) {
        if (stage.isMaximized() || stage.isFullScreen()) {
            return;
        }
        // La ventana es algo más grande que la escena por el borde y la barra de título
        double bordeAncho = stage.getWidth() - scene.getWidth();
        double bordeAlto = stage.getHeight() - scene.getHeight();

        double ancho = Math.max(vista.prefWidth(-1), ANCHO_MINIMO);
        double alto = Math.max(vista.prefHeight(-1), ALTO_MINIMO);

        stage.setWidth(ancho + bordeAncho);
        stage.setHeight(alto + bordeAlto);
        stage.centerOnScreen();
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