/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tonguely.reto2_mongoquest.Controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import tonguely.reto2_mongoquest.App;
import tonguely.reto2_mongoquest.DAO.UsuarioDAO;

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

        boolean si=dao.comprobarLogin(emailField.getText(), contrasenaField.getText());
        if(si){
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Credenciales Correctas");
        alerta.setHeaderText("Inicio de sesión correcto");
        alerta.setContentText("Las credenciales coinciden.");
        alerta.showAndWait();
            try {
                App.setRoot("seleccionCurso");
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
    }

    @FXML
    private void entradaInvitado(ActionEvent event) {
    }
    
}
