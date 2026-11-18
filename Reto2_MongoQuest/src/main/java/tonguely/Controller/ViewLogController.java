/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package tonguely.Controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import tonguely.Excepciones.ErrorLectura;
import tonguely.Log.EntradaLog;
import tonguely.Log.GestorLog;
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
    private TableView<EntradaLog> tVLog;
    @FXML
    private TableColumn<EntradaLog, LocalDateTime> colFecha;
    @FXML
    private TableColumn<EntradaLog, String> colUsuario;
    @FXML
    private TableColumn<EntradaLog, String> colTipo;
    @FXML
    private TableColumn<EntradaLog, String> colColeccion;
    @FXML
    private TableColumn<EntradaLog, String> colResultado;
    @FXML
    private TableColumn<EntradaLog, String> colDetalle;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colColeccion.setCellValueFactory(new PropertyValueFactory<>("coleccion"));
        colResultado.setCellValueFactory(new PropertyValueFactory<>("resultado"));
        colDetalle.setCellValueFactory(new PropertyValueFactory<>("detalle"));
        
        colFecha.setCellFactory(col -> new TableCell<EntradaLog,LocalDateTime>(){
            @Override 
            protected  void updateItem(LocalDateTime fecha, boolean  vacia){
                super.updateItem(fecha, vacia);
                if(vacia || fecha == null){
                    setText(null);
                }
                else{
                    setText(fecha.format(GestorLog.FORMATO_FECHA));
                }
            }
        });
        cargarDatos();
    }    
    
    @FXML
    private void actualizar(ActionEvent event) {
        cargarDatos();
    }

    private void cargarDatos(){
        try {
            List<EntradaLog> log = GestorLog.leer();
            log.sort(Comparator.comparing(EntradaLog::getFecha).reversed());
            tVLog.setItems(FXCollections.observableArrayList(log));
        } catch (ErrorLectura e) {
            Alertas.error("Error al cargar los datos del log", e.getMessage());
        }
    }

}
