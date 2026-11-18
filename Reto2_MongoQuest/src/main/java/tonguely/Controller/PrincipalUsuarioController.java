package tonguely.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import tonguely.Main.App;
import tonguely.Main.Sesion;
import tonguely.modelo.Usuario;

public class PrincipalUsuarioController implements Initializable {

    @FXML private Label bienvenidaLabel;
    @FXML private Label rachaLabel;

    // Controlador del seleccionCurso.fxml incluido (fx:id="misCursos" + "Controller")
    @FXML private SeleccionCursoController misCursosController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Usuario u = Sesion.usuarioActual;
        if (u != null) {
            bienvenidaLabel.setText("BIENVENIDO " + u.getNombre() + "!");
            rachaLabel.setText(String.valueOf(u.getRacha()));
            misCursosController.cargarCursos();
        }
    }

    @FXML
    private void irAPerfil() {
        try {
            App.setRoot("perfil");
        } catch (IOException ex) {
            System.getLogger(PrincipalUsuarioController.class.getName())
                    .log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void cerrarSesion() {
        Sesion.cerrar();
        try {
            App.setRoot("inicioSesion_1");
        } catch (IOException ex) {
            System.getLogger(PrincipalUsuarioController.class.getName())
                    .log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}