package tonguely.Controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
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
import tonguely.Log.TipoOperacion;
/**
 * Controlador del visor del log (pestaña "Log" del panel de administración).
 *
 * Lee el fichero de log con {@link GestorLog#leer()}, lo muestra en una tabla
 * con la entrada más reciente arriba y permite filtrarlo por tipo de operación,
 * por "Solo errores" y por texto (usuario o detalle).
 *
 * @author Christian
 */
public class ViewLogController implements Initializable {


    @FXML
    private ComboBox<String> cbTipo;
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

    /** Opción del desplegable que muestra todos los tipos. */
    private static final String TODOS = "Todos";

    /** Entradas del log que se muestran en la tabla, después de aplicar los filtros. */
    private FilteredList<EntradaLog> logFiltrado;
    /**
     * Configura una sola vez de dónde saca cada columna su dato y cómo se
     * muestra la fecha, rellena el desplegable de tipos y carga el log.
     *
     * @param url no se usa
     * @param rb no se usa
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

        // Desplegable de tipos: "Todos" y después cada valor del enum TipoOperacion.
        // Se rellena antes de cargarDatos() porque aplicarFiltro() lee su valor.
        cbTipo.getItems().add(TODOS);
        for (TipoOperacion tipo : TipoOperacion.values()) {
            cbTipo.getItems().add(tipo.name());
        }
        cbTipo.setValue(TODOS);

        // El buscador filtra mientras se escribe
        tFBuscar.textProperty().addListener((obs, antes, ahora) -> aplicarFiltro(null));

        cargarDatos();
    }
    
    /**
     * Botón "Actualizar": vuelve a leer el fichero de log. Las columnas no se
     * vuelven a configurar y se mantienen los filtros que hubiera elegidos.
     *
     * @param event evento del botón (no se usa)
     */
    @FXML
    private void actualizar(ActionEvent event) {
        cargarDatos();
    }

    /**
     * Lee el log, lo ordena de más reciente a más antiguo y lo muestra en la
     * tabla aplicando los filtros actuales. Si no se puede leer, avisa al usuario.
     */
    private void cargarDatos(){
        try {
            List<EntradaLog> log = GestorLog.leer();
            log.sort(Comparator.comparing(EntradaLog::getFecha).reversed());
            logFiltrado = new FilteredList<>(FXCollections.observableArrayList(log));
            tVLog.setItems(logFiltrado);
            aplicarFiltro(null);
        } catch (ErrorLectura e) {
            Alertas.error("Error al cargar los datos del log", e.getMessage());
        }
    }

    /**
     * Muestra solo las entradas que cumplen los tres filtros a la vez:
     * el tipo elegido, "Solo errores" y el texto del buscador (usuario o detalle).
     * Se llama al cambiar el desplegable o la casilla, al escribir en el
     * buscador y después de cada carga de datos.
     *
     * @param event evento del control que ha cambiado (no se usa)
     */
    @FXML
    private void aplicarFiltro(ActionEvent event) {
        if (logFiltrado == null) {
            return; // todavía no se han cargado los datos
        }

        // Se leen los controles una vez, fuera del predicado
        String tipo = cbTipo.getValue();
        boolean soloErrores = chBSoloError.isSelected();
        String texto = tFBuscar.getText().trim().toLowerCase();

        logFiltrado.setPredicate(entrada -> {
            // 1. Tipo: con "Todos" pasan todas; si no, el tipo tiene que coincidir
            boolean pasaTipo = TODOS.equals(tipo) || entrada.getTipo().equals(tipo);

            // 2. Solo errores: si la casilla no está marcada pasan todas
            boolean pasaError = !soloErrores || entrada.getResultado().equals("ERROR");

            // 3. Buscador: vacío, o el texto aparece en el usuario o en el detalle
            boolean pasaTexto = texto.isEmpty()
                    || entrada.getUsuario().toLowerCase().contains(texto)
                    || entrada.getDetalle().toLowerCase().contains(texto);

            return pasaTipo && pasaError && pasaTexto;
        });
    }

}
