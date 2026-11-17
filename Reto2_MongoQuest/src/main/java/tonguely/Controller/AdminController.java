package tonguely.Controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import tonguely.Main.App;
import tonguely.Main.Sesion;

/**
 * Controlador del panel de administración (profesores).
 * Las pestañas tienen su propio controlador; aquí solo se gestiona el cierre de sesión.
 */
public class AdminController {

    /**
     * Cierra la sesión del profesor (queda registrado en el log) y vuelve al inicio de sesión.
     *
     * @param event evento del botón "Cerrar sesión"
     */
    @FXML
    private void cerrarSesion(ActionEvent event) {
        Sesion.cerrar();
        try {
            App.setRoot("inicioSesion_1");
        } catch (IOException ex) {
            System.getLogger(AdminController.class.getName())
                    .log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}