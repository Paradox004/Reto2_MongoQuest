package tonguely.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.bson.types.ObjectId;
import tonguely.DAO.CursoDAO;
import tonguely.DAO.InscripcionesDAO;
import tonguely.DAO.ProfesorDAO;
import tonguely.DAO.UsuarioDAO;
import tonguely.Excepciones.TonguelyException;
import tonguely.Main.Sesion;
import tonguely.modelo.Curso;
import tonguely.modelo.Inscripciones;
import tonguely.modelo.Profesor;
import tonguely.modelo.Usuario;

public class GestionUsuariosController implements Initializable {

    @FXML private TextField buscarField;
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, Number> colRacha;

    @FXML private Label nombreLabel;
    @FXML private Label emailLabel;
    @FXML private Label rachaLabel;
    @FXML private ListView<Curso> listaComunes;
    @FXML private ComboBox<Curso> comboDisponibles;
    @FXML private Button nuevoButton;
    @FXML private Button eliminarButton;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final InscripcionesDAO inscripcionesDAO = new InscripcionesDAO();
    private final CursoDAO cursoDAO = new CursoDAO();
    private final ProfesorDAO profesorDAO = new ProfesorDAO();

    private final ObservableList<Usuario> usuarios = FXCollections.observableArrayList();
    private final Set<ObjectId> alumnos = new HashSet<ObjectId>(); // usuarios en algún curso del profesor
    private List<Curso> misCursos = new ArrayList<Curso>();
    private Usuario seleccionado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            // Columnas
            colNombre.setCellValueFactory(
                    new Callback<TableColumn.CellDataFeatures<Usuario, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<Usuario, String> datos) {
                            return new SimpleStringProperty(datos.getValue().getNombre());
                        }
                    });
            
            colEmail.setCellValueFactory(
                    new Callback<TableColumn.CellDataFeatures<Usuario, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<Usuario, String> datos) {
                            return new SimpleStringProperty(datos.getValue().getEmail());
                        }
                    });
            
            colRacha.setCellValueFactory(
                    new Callback<TableColumn.CellDataFeatures<Usuario, Number>, ObservableValue<Number>>() {
                        @Override
                        public ObservableValue<Number> call(TableColumn.CellDataFeatures<Usuario, Number> datos) {
                            return new SimpleIntegerProperty(datos.getValue().getRacha());
                        }
                    });
            
            // Lista de usuarios + búsqueda
            usuarios.setAll(usuarioDAO.obtenerUsuarios());
            final FilteredList<Usuario> filtrados = new FilteredList<Usuario>(usuarios);
            buscarField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> obs, String antes, String texto) {
                    final String t = (texto == null) ? "" : texto.toLowerCase().trim();
                    filtrados.setPredicate(new Predicate<Usuario>() {
                        @Override
                        public boolean test(Usuario u) {
                            if (t.isEmpty()) {
                                return true;
                            }
                            boolean porNombre = u.getNombre() != null
                                    && u.getNombre().toLowerCase().contains(t);
                            boolean porEmail = u.getEmail() != null
                                    && u.getEmail().toLowerCase().contains(t);
                            return porNombre || porEmail;
                        }
                    });
                }
            });
            tablaUsuarios.setItems(filtrados);
            
            // Filas verdes / rojas
            tablaUsuarios.setRowFactory(new Callback<TableView<Usuario>, TableRow<Usuario>>() {
                @Override
                public TableRow<Usuario> call(TableView<Usuario> tabla) {
                    final TableRow<Usuario> fila = new TableRow<Usuario>() {
                        @Override
                        protected void updateItem(Usuario u, boolean vacio) {
                            super.updateItem(u, vacio);
                            pintar(this);
                        }
                    };
                    fila.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> obs, Boolean antes, Boolean ahora) {
                            pintar(fila);
                        }
                    });
                    return fila;
                }
            });
            
            // Nombre del curso en la lista de cursos en común
            listaComunes.setCellFactory(new Callback<ListView<Curso>, ListCell<Curso>>() {
                @Override
                public ListCell<Curso> call(ListView<Curso> lista) {
                    return new ListCell<Curso>() {
                        @Override
                        protected void updateItem(Curso c, boolean vacio) {
                            super.updateItem(c, vacio);
                            if (vacio || c == null) {
                                setText(null);
                            } else {
                                setText(c.getNombre());
                            }
                        }
                    };
                }
            });
            
            // Nombre del curso en el desplegable
            comboDisponibles.setConverter(new StringConverter<Curso>() {
                @Override
                public String toString(Curso c) {
                    return c == null ? "" : c.getNombre();
                }
                
                @Override
                public Curso fromString(String s) {
                    return null;
                }
            });
            
            // Al elegir un usuario se recalcula todo (por si cambiaron los cursos del profesor)
            tablaUsuarios.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Usuario>() {
                @Override
                public void changed(ObservableValue<? extends Usuario> obs, Usuario antes, Usuario u) {
                    seleccionado = u;
                    recalcular();
                }
            });
            
            recalcular();
        } catch (TonguelyException e) {
            // El DAO ya ha registrado el error en el log: aquí solo se avisa al usuario
            Alertas.error("No se han podido cargar los usuarios", e.getMessage());
        }
    }

    private void pintar(TableRow<Usuario> fila) {
        Usuario u = fila.getItem();
        if (u == null || fila.isEmpty()) {
            fila.setStyle("");
            return;
        }
        boolean verde = alumnos.contains(u.getId());
        if (fila.isSelected()) {
            if (verde) {
                fila.setStyle("-fx-background-color: #4CAF50;");
            } else {
                fila.setStyle("-fx-background-color: #E53935;");
            }
        } else {
            if (verde) {
                fila.setStyle("-fx-background-color: #C8E6C9;");
            } else {
                fila.setStyle("-fx-background-color: #FFCDD2;");
            }
        }
    }

    // Cursos que imparte el profesor que ha iniciado sesión
    private List<Curso> cursosDelProfesor() throws TonguelyException {
        List<Curso> resultado = new ArrayList<Curso>();
        Profesor sesion = Sesion.profesorActual;
        if (sesion == null) {
            return resultado;
        }
        Profesor yo = profesorDAO.buscarPorEmail(sesion.getEmail());
        if (yo == null) {
            yo = sesion;
        }

        Set<String> nombres = new HashSet<String>();
        if (yo.getCursos() != null) {
            for (Curso c : yo.getCursos()) {
                if (c.getNombre() != null) {
                    nombres.add(c.getNombre());
                }
            }
        }

        for (Curso c : cursoDAO.cargarCursos()) {
            boolean porNombre = nombres.contains(c.getNombre());
            boolean porEmail = false;
            if (c.getProfesores() != null) {
                for (Profesor p : c.getProfesores()) {
                    if (p.getEmail() != null && p.getEmail().equalsIgnoreCase(yo.getEmail())) {
                        porEmail = true;
                    }
                }
            }
            if (porNombre || porEmail) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    private void recalcular() {
        alumnos.clear();
        try {
            misCursos = cursosDelProfesor();

            List<ObjectId> ids = new ArrayList<ObjectId>();
            for (Curso c : misCursos) {
                ids.add(c.getId());
            }
            for (Inscripciones i : inscripcionesDAO.buscarPorCursos(ids)) {
                alumnos.add(i.getIdEstudiante());
            }
        } catch (TonguelyException e) {
            // El DAO ya ha registrado el error en el log: aquí solo se avisa al usuario
            Alertas.error("No se han podido cargar tus cursos y alumnos", e.getMessage());
            misCursos = new ArrayList<Curso>();
        }

        tablaUsuarios.refresh();
        actualizarPanel();
    }

    private void actualizarPanel() {
        if (seleccionado == null) {
            nombreLabel.setText("-");
            emailLabel.setText("-");
            rachaLabel.setText("-");
            listaComunes.getItems().clear();
            comboDisponibles.getItems().clear();
            nuevoButton.setDisable(true);
            eliminarButton.setDisable(true);
            return;
        }

        nombreLabel.setText(seleccionado.getNombre());
        emailLabel.setText(seleccionado.getEmail());
        rachaLabel.setText(String.valueOf(seleccionado.getRacha()));

        Set<ObjectId> inscritos = new HashSet<ObjectId>();
        try {
            for (Inscripciones i : inscripcionesDAO.buscarPorEstudiante(seleccionado.getId())) {
                inscritos.add(i.getIdCurso());
            }
        } catch (TonguelyException e) {
            Alertas.error("No se han podido cargar las inscripciones del usuario", e.getMessage());
        }

        List<Curso> comunes = new ArrayList<Curso>();
        List<Curso> disponibles = new ArrayList<Curso>();
        for (Curso c : misCursos) {
            if (inscritos.contains(c.getId())) {
                comunes.add(c);
            } else {
                disponibles.add(c);
            }
        }

        listaComunes.setItems(FXCollections.observableArrayList(comunes));
        comboDisponibles.setItems(FXCollections.observableArrayList(disponibles));
        comboDisponibles.getSelectionModel().clearSelection();
        nuevoButton.setDisable(disponibles.isEmpty());
        eliminarButton.setDisable(comunes.isEmpty());
    }

    // Inscribir al usuario en uno de mis cursos donde aún no está
    @FXML
    private void nuevoUsuario() {
        Curso curso = comboDisponibles.getSelectionModel().getSelectedItem();
        if (seleccionado == null || curso == null) {
            Alertas.advertencia("Elige un curso de la lista desplegable.");
            return;
        }
        try {
            inscripcionesDAO.inscribir(seleccionado.getId(), curso.getId());
        } catch (TonguelyException e) {
            Alertas.error("No se ha podido inscribir al usuario", e.getMessage());
        }
        recalcular();
    }

    // Quitar al usuario de un curso en común
    @FXML
    private void eliminarUsuario() {
        Curso curso = listaComunes.getSelectionModel().getSelectedItem();
        if (seleccionado == null || curso == null) {
            Alertas.advertencia("Selecciona un curso de la lista de cursos en común.");
            return;
        }
        if (Alertas.confirmar("¿Quitar a " + seleccionado.getNombre()
                + " del curso " + curso.getNombre() + "?")) {
            try {
                inscripcionesDAO.desinscribir(seleccionado.getId(), curso.getId());
            } catch (TonguelyException e) {
                Alertas.error("No se ha podido quitar al usuario del curso", e.getMessage());
            }
            recalcular();
        }
    }
}