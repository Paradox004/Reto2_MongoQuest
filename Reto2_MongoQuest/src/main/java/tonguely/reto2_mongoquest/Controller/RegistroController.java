/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package tonguely.reto2_mongoquest.Controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
/**
 * FXML Controller class
 *
 * @author songo
 */
public class RegistroController implements Initializable {


    @FXML
    private TextField correoField;
    @FXML
    private PasswordField contrasenaField;
    @FXML
    private Button crearButton;
    @FXML
    private Button iniciarButton;
    @FXML
    private Button invitadoButton;
    @FXML
    private TextField nombreField;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void creacionUsuario(ActionEvent event) {
    }

    @FXML
    private void abrirIniciarVentana(ActionEvent event) {
    }

    @FXML
    private void abrirVentanaInvitado(ActionEvent event) {
    }

}
