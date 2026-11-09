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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import tonguely.modelo.Usuario;
import tonguely.Main.App;
import tonguely.DAO.UsuarioDAO;

/**
 *
 * @author songo
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

    UsuarioDAO dao=new UsuarioDAO();
    
    @FXML
    private void iniciarSesion(ActionEvent event) {

        Usuario usuario=dao.comprobarLogin(emailField.getText(), contrasenaField.getText());
        if(usuario !=null){
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Credenciales Correctas");
        alerta.setHeaderText("Inicio de sesión correcto");
        alerta.setContentText("Las credenciales coinciden.");
        alerta.showAndWait();
            try {
                FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/view/seleccionCurso.fxml")
            );
                Parent root = loader.load();
                SeleccionCursoController controller = loader.getController();
                controller.cargarCursos(usuario.getId());
                App.scene.setRoot(root);
                
            } catch (IOException ex) {
                System.getLogger(InicioSesionController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
        else{
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText("Inicio de sesión incorrecto");
        alerta.setContentText("El usuario o la contraseña no son correctos.");
        alerta.showAndWait();
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
    }
    
}
