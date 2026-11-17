package tonguely.Controller;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.bson.types.ObjectId;
import tonguely.DAO.CursoDAO;
import tonguely.DAO.InscripcionesDAO;
import tonguely.Excepciones.TonguelyException;
import tonguely.Main.App;
import tonguely.Main.Sesion;
import tonguely.modelo.Curso;
import tonguely.modelo.Inscripciones;
import tonguely.modelo.Usuario;

public class PerfilController implements Initializable {

    @FXML private Label nombreLabel;
    @FXML private Label emailLabel;
    @FXML private Label rachaLabel;
    @FXML private TableView<Inscripciones> tablaInscripciones;
    @FXML private TableColumn<Inscripciones, String> colCurso;
    @FXML private TableColumn<Inscripciones, String> colIdioma2;
    @FXML private TableColumn<Inscripciones, String> colEstado;
    @FXML private TableColumn<Inscripciones, String> colFechaInicio;
    @FXML private TableColumn<Inscripciones, String> colFechaFin;

    private final CursoDAO cursoDAO = new CursoDAO();
    private final InscripcionesDAO inscripcionesDAO = new InscripcionesDAO();
    private final Map<ObjectId, Curso> cacheCursos = new HashMap<>();
    private final SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Usuario u = Sesion.usuarioActual;
        if (u == null) {
            return;
        }
        nombreLabel.setText(u.getNombre());
        emailLabel.setText(u.getEmail());
        rachaLabel.setText(u.getRacha() + " días");

        colCurso.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Inscripciones, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Inscripciones, String> datos) {
                Curso curso = curso(datos.getValue());
                return new SimpleStringProperty(curso == null ? "-" : curso.getNombre());
            }
        });

        colIdioma2.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Inscripciones, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Inscripciones, String> datos) {
                Curso curso = curso(datos.getValue());
                return new SimpleStringProperty(curso == null ? "-" : curso.getDescripcion());
            }
        });

        colEstado.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Inscripciones, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Inscripciones, String> datos) {
                return new SimpleStringProperty(datos.getValue().getEstado());
            }
        });

        colFechaInicio.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Inscripciones, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Inscripciones, String> datos) {
                return new SimpleStringProperty(fecha(datos.getValue().getFechaInicio()));
            }
        });

        colFechaFin.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Inscripciones, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Inscripciones, String> datos) {
                return new SimpleStringProperty(fecha(datos.getValue().getFechaFin()));
            }
        });

        try {
            tablaInscripciones.setItems(FXCollections.observableArrayList(
                    inscripcionesDAO.buscarPorEstudiante(u.getId())));
        } catch (TonguelyException ex) {
            System.getLogger(PerfilController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    private Curso curso(Inscripciones ins) {
        ObjectId idCurso = ins.getIdCurso();
        if (!cacheCursos.containsKey(idCurso)) {
            try {
                cacheCursos.put(idCurso, cursoDAO.buscarPorId(idCurso));
            } catch (TonguelyException ex) {
                System.getLogger(PerfilController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
        return cacheCursos.get(idCurso);
    }

    private String fecha(Date d) {
        if (d == null) {
            return "-";
        }
        return formato.format(d);
    }

    @FXML
    private void volver() {
        try {
            App.setRoot("principalUsuario");
        } catch (IOException ex) {
            System.getLogger(PerfilController.class.getName())
                    .log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void cerrarSesion() {
        Sesion.usuarioActual = null;
        try {
            App.setRoot("inicioSesion_1");
        } catch (IOException ex) {
            System.getLogger(PerfilController.class.getName())
                    .log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}