/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package tonguely.reto2_mongoquest.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import modelo.Usuario;
import tonguely.reto2_mongoquest.App;
import tonguely.reto2_mongoquest.DAO.UsuarioDAO;
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
    UsuarioDAO udao=new UsuarioDAO();
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void creacionUsuario(ActionEvent event) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombreField.getText());
        usuario.setEmail(correoField.getText());
        usuario.setContraseña(contrasenaField.getText());
        usuario.setRacha(0);
        usuario.setCursos(new ArrayList<>());
        if(udao.crearUsuario(usuario)){
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Usuario creado con exito");
        alerta.setHeaderText("Usuario creado");
        alerta.setContentText("Usuario creado con exito!");
        alerta.showAndWait();
            try {
                App.setRoot("inicioSesion_1");
            } catch (IOException ex) {
                System.getLogger(RegistroController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
        else{
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText("Creacion del usuario fallida");
        alerta.setContentText("Fallo en la creacion de usuario");
        alerta.showAndWait();
        nombreField.setText("");
        correoField.setText("");
        contrasenaField.setText("");
        }
    }
    
    @FXML
    private void abrirIniciarVentana(ActionEvent event) {
        try {
            App.setRoot("inicioSesion_1");
        } catch (IOException ex) {
            System.getLogger(RegistroController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void abrirVentanaInvitado(ActionEvent event) {
    }

}
