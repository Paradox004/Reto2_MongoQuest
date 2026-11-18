/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tonguely.Controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import tonguely.Main.App;

/**
 *
 * @author songo
 */
public class RegistrateController {

    @FXML
    private ScrollPane root;
    @FXML
    private Button registrarButton;
    @FXML
    private Button iniciarSesionButton;

    @FXML
    private void iniciarSesion(ActionEvent event) {
        try {
            App.setRoot("inicioSesion_1");
        } catch (IOException ex) {
            System.getLogger(InicioSesionController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
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
    
}
