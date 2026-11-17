package tonguely.Controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import tonguely.Main.App;

public class AdminController {

    @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            App.setRoot("inicioSesion_1");
        } catch (IOException ex) {
            System.getLogger(AdminController.class.getName())
                    .log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}