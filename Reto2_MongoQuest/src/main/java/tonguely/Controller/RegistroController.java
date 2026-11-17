package tonguely.Controller;

import tonguely.Excepciones.Alertas;
import java.io.IOException;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import tonguely.modelo.Usuario;
import tonguely.Main.App;
import tonguely.DAO.UsuarioDAO;
import tonguely.Excepciones.DatoDuplicadoException;
import tonguely.Excepciones.TonguelyException;

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
        try {
            // 1. Intentar crear el usuario (el DAO registra el resultado en el log)
            udao.crearUsuario(usuario);

            Alertas.informacion("Usuario creado con exito", "Usuario creado", "Usuario creado con exito!");
            try {
                App.setRoot("inicioSesion_1");
            } catch (IOException ex) {
                System.getLogger(RegistroController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }

        } catch (DatoDuplicadoException e) {
            // 2. Email repetido: error del usuario, se limpian los campos para que lo corrija
            Alertas.error("Creacion del usuario fallida", e.getMessage());
            nombreField.setText("");
            correoField.setText("");
            contrasenaField.setText("");

        } catch (TonguelyException e) {
            // 3. Cualquier otro error (MongoDB caído...): se avisa sin borrar lo escrito
            Alertas.error("Creacion del usuario fallida", e.getMessage());
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
