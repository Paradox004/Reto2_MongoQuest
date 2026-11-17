/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package tonguely.Controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
/**
 * FXML Controller class
 *
 * @author Christian
 */
public class ViewLogController implements Initializable {


    @FXML
    private ComboBox<?> cbTipo;
    @FXML
    private CheckBox chBSoloError;
    @FXML
    private TextField tFBuscar;
    @FXML
    private Button bActualizar;
    @FXML
    private TableView<?> tVLog;
    @FXML
    private TableColumn<?, ?> colFecha;
    @FXML
    private TableColumn<?, ?> colUsuario;
    @FXML
    private TableColumn<?, ?> colTipo;
    @FXML
    private TableColumn<?, ?> colColeccion;
    @FXML
    private TableColumn<?, ?> colResultado;
    @FXML
    private TableColumn<?, ?> colDetalle;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void actualizar(ActionEvent event) {
    }

}
