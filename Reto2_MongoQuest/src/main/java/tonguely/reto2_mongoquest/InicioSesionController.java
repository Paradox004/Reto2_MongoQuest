/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package tonguely.reto2_mongoquest;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.bson.Document;
import tonguely.reto2_mongoquest.DAO.ConexionMongo;
/**
 * FXML Controller class
 *
 * @author songo
 */
public class InicioSesionController implements Initializable {


    @FXML
    private Button IniciarSesionButton;
    @FXML
    private Button InvitadoButton;
    @FXML
    private Label EmailLabel;
    @FXML
    private Label ContrasenaLabel;
    @FXML
    private TextField EmailField;
    @FXML
    private TextField ContrasenaField;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void mostrarUsuarios() {

        MongoDatabase bd = ConexionMongo.conectar();
        MongoCollection<Document> usuarios =
                bd.getCollection("Usuario");

        for (Document usuario : usuarios.find()) {
            System.out.println(usuario.toJson());
        }
    }
    
}
