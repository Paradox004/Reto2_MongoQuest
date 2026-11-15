
package tonguely.Controller;

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
import tonguely.modelo.Usuario;
import tonguely.Main.App;
import tonguely.DAO.UsuarioDAO;

/**
 * Controlador encargado de gestionar el registro de usuarios.
 *
 * @author anartz
 */
public class RegistroController {

    /** Campo del correo electrónico. */
    @FXML
    private TextField correoField;

    /** Campo de la contraseña. */
    @FXML
    private PasswordField contrasenaField;

    /** Botón para crear un usuario. */
    @FXML
    private Button crearButton;

    /** Botón para iniciar sesión. */
    @FXML
    private Button iniciarButton;

    /** Botón para acceder como invitado. */
    @FXML
    private Button invitadoButton;

    /** Campo del nombre del usuario. */
    @FXML
    private TextField nombreField;

    /** DAO para gestionar los usuarios. */
    UsuarioDAO udao = new UsuarioDAO();

    /**
     * Inicializa el controlador.
     *
     * @param url ubicación del archivo FXML.
     * @param rb recursos utilizados por el controlador.
     */

    /**
     * Crea un nuevo usuario con los datos introducidos.
     *
     * @param event evento que ejecuta la creación del usuario.
     */
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
            alerta.setContentText("Ya existe un usuario con ese mail");
            alerta.showAndWait();
            nombreField.setText("");
            correoField.setText("");
            contrasenaField.setText("");
        }
    }

    /**
     * Abre la ventana de inicio de sesión.
     *
     * @param event evento que ejecuta la acción.
     */
    @FXML
    private void abrirIniciarVentana(ActionEvent event) {
        try {
            App.setRoot("inicioSesion_1");
        } catch (IOException ex) {
            System.getLogger(RegistroController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    /**
     * Abre la ventana de acceso como invitado.
     *
     * @param event evento que ejecuta la acción.
     */
    @FXML
    private void abrirVentanaInvitado(ActionEvent event) {
    }

}
