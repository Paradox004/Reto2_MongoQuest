/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tonguely.Controller;

import tonguely.Excepciones.Alertas;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tonguely.modelo.Usuario;
import tonguely.Main.App;
import tonguely.DAO.UsuarioDAO;
import tonguely.Excepciones.TonguelyException;

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

    @FXML
    private void iniciarSesion(ActionEvent event) {

        // Si MongoDB falla, el DAO ya lo ha registrado en el log: aquí solo se avisa al usuario
        Usuario usuario;
        try {
            usuario = dao.comprobarLogin(emailField.getText(), contrasenaField.getText());
        } catch (TonguelyException e) {
            Alertas.error("No se ha podido iniciar sesión", e.getMessage());
            return;
        }
        if (usuario != null) {
            Alertas.informacion("Credenciales Correctas",
                    "Inicio de sesión correcto", "Las credenciales coinciden.");
            try {
                FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/view/seleccionCurso.fxml")
                );
                Parent root = loader.load();
                App.scene.setRoot(root);
                Stage stage = (Stage) App.scene.getWindow();
                stage.setWidth(1200);
                stage.setHeight(750);
                stage.centerOnScreen();
                SeleccionCursoController controller = loader.getController();
                controller.cargarCursos(usuario.getId());
                

            } catch (IOException ex) {
                System.getLogger(InicioSesionController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
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
                App.class.getResource("/view/seleccionCurso.fxml")
                );
                Parent root = loader.load();
                App.scene.setRoot(root);
                Stage stage = (Stage) App.scene.getWindow();
                stage.setWidth(1200);
                stage.setHeight(750);
                stage.centerOnScreen();
                SeleccionCursoController controller = loader.getController();
                controller.cargarTodosLosCursos();

            } catch (IOException ex) {
                System.getLogger(InicioSesionController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
    }

}
