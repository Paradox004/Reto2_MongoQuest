/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tonguely.Controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tonguely.DAO.ProfesorDAO;
import tonguely.modelo.Usuario;
import tonguely.Main.App;
import tonguely.DAO.UsuarioDAO;
import tonguely.Excepciones.TonguelyException;
import tonguely.modelo.Profesor;

/**
 *
 * @author anartz
 */
public class InicioSesionController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField contrasenaField;
    @FXML
    private Button iniciarSesionButton;
    @FXML
    private Button registrarButton;
    @FXML
    private Button invitadoButton;

    UsuarioDAO dao = new UsuarioDAO();
    ProfesorDAO profesorDao = new ProfesorDAO();

    @FXML
    private void iniciarSesion(ActionEvent event) {
        String email = emailField.getText();
        String contrasena = contrasenaField.getText();

        // 1. Comprobar primero si es un profesor y, si no, si es un usuario.
        //    Los DAO inician la Sesion y registran el login en el log.
        //    Si MongoDB falla, el DAO ya lo ha registrado: aquí solo se avisa al usuario.
        Profesor profesor;
        Usuario usuario = null;
        try {
            profesor = profesorDao.comprobarLogin(email, contrasena);
            if (profesor == null) {
                usuario = dao.comprobarLogin(email, contrasena);
            }
        } catch (TonguelyException e) {
            Alertas.error("No se ha podido iniciar sesión", e.getMessage());
            return;
        }

        // 2. Profesor: panel de administración
        if (profesor != null) {
            Alertas.informacion("Credenciales Correctas", "Inicio de sesión correcto",
                    "Bienvenido/a, profesor/a " + profesor.getNombre() + ".");
            try {
                App.setRoot("admin");
            } catch (IOException ex) {
                System.getLogger(InicioSesionController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            return;
        }

        // 3. Usuario: pantalla principal; si no, credenciales incorrectas
        if (usuario != null) {
            Alertas.informacion("Credenciales Correctas", "Inicio de sesión correcto",
                    "Las credenciales coinciden.");
            try {
                App.setRoot("principalUsuario");
                App.setRoot("principalUsuario");
                Stage stage = (Stage) App.scene.getWindow();
                stage.setMaximized(true);
            } catch (IOException ex) {
                System.getLogger(InicioSesionController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            return;
        } else {
            Alertas.error("Inicio de sesión incorrecto", "El usuario o la contraseña no son correctos.");
            emailField.setText("");
            contrasenaField.setText("");
        }
    }

    @FXML
    private void registrarUsuario(ActionEvent event) {
        try {
            App.setRoot("registro");
        } catch (IOException ex) {
            System.getLogger(InicioSesionController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void entradaInvitado(ActionEvent event) {
         try {
                FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/view/todosLosCursos.fxml")
                );
                Parent root = loader.load();
                App.scene.setRoot(root);
                Stage stage = (Stage) App.scene.getWindow();
                stage.setWidth(1200);
                stage.setHeight(750);
                stage.centerOnScreen();
                TodosCursosController controller = loader.getController();

            } catch (IOException ex) {
                System.getLogger(InicioSesionController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
    }

}
