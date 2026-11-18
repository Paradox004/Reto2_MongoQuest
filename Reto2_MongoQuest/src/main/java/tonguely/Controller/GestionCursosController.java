package tonguely.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.bson.types.ObjectId;
import tonguely.DAO.CursoDAO;
import tonguely.DAO.ProfesorDAO;
import tonguely.Excepciones.TonguelyException;
import tonguely.modelo.Curso;
import tonguely.modelo.Profesor;

public class GestionCursosController implements Initializable {

    @FXML private TableView<Curso> tablaCursos;
    @FXML private TableColumn<Curso, String> colNombre;
    @FXML private TableColumn<Curso, Number> colPrecio;
    @FXML private TableColumn<Curso, String> colDescripcion;
    @FXML private TableColumn<Curso, String> colProfesores;

    @FXML private TextField nombreField;
    @FXML private TextField precioField;
    @FXML private TextArea descripcionArea;
    @FXML private ListView<Profesor> listaProfesores;

    private final CursoDAO cursoDAO = new CursoDAO();
    private final ProfesorDAO profesorDAO = new ProfesorDAO();

    private Curso cursoSeleccionado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Lista de profesores (selección múltiple)
        try {
            listaProfesores.setItems(FXCollections.observableArrayList(profesorDAO.buscarTodos()));
        } catch (TonguelyException e) {
            Alertas.error("No se han podido cargar los profesores", e.getMessage());
        }
        listaProfesores.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listaProfesores.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Profesor p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : nombreCompleto(p));
            }
        });

        // Columnas
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colPrecio.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getPrecio()));
        colDescripcion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));
        colProfesores.setCellValueFactory(c -> new SimpleStringProperty(textoProfesores(c.getValue())));

        // Al elegir una fila se rellena el formulario
        tablaCursos.getSelectionModel().selectedItemProperty().addListener((obs, antes, curso) -> {
            if (curso != null) {
                mostrarCurso(curso);
            }
        });

        cargarCursos();
    }

    private void cargarCursos() {
        try {
            tablaCursos.setItems(FXCollections.observableArrayList(cursoDAO.cargarCursos()));
        } catch (TonguelyException e) {
            Alertas.error("No se han podido cargar los cursos", e.getMessage());
        }
    }

    private String nombreCompleto(Profesor p) {
        String ape = p.getApellidos() == null ? "" : " " + p.getApellidos();
        return p.getNombre() + ape;
    }

    private String textoProfesores(Curso curso) {
        if (curso.getProfesores() == null) {
            return "";
        }
        return curso.getProfesores().stream()
                .map(this::nombreCompleto)
                .collect(Collectors.joining(", "));
    }

    private void mostrarCurso(Curso curso) {
        cursoSeleccionado = curso;
        nombreField.setText(curso.getNombre());
        precioField.setText(String.valueOf(curso.getPrecio()));
        descripcionArea.setText(curso.getDescripcion());

        // Los profesores embebidos no tienen _id: se reconocen por email
        listaProfesores.getSelectionModel().clearSelection();
        if (curso.getProfesores() != null) {
            for (Profesor p : listaProfesores.getItems()) {
                boolean asignado = curso.getProfesores().stream()
                        .anyMatch(cp -> cp.getEmail() != null && cp.getEmail().equalsIgnoreCase(p.getEmail()));
                if (asignado) {
                    listaProfesores.getSelectionModel().select(p);
                }
            }
        }
    }

    @FXML
    private void nuevoCurso() {
        cursoSeleccionado = null;
        tablaCursos.getSelectionModel().clearSelection();
        nombreField.clear();
        precioField.clear();
        descripcionArea.clear();
        listaProfesores.getSelectionModel().clearSelection();
        nombreField.requestFocus();
    }

    @FXML
    private void guardarCurso() {
        String nombre = nombreField.getText().trim();
        if (nombre.isEmpty()) {
            Alertas.advertencia("El nombre es obligatorio.");
            return;
        }

        int precio;
        try {
            precio = Integer.parseInt(precioField.getText().trim());
            if (precio < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Alertas.advertencia("El precio debe ser un número entero (≥ 0).");
            return;
        }

        try {
            // Validación: no puede haber dos cursos con el mismo nombre
            ObjectId idActual = (cursoSeleccionado == null) ? null : cursoSeleccionado.getId();
            if (cursoDAO.existeNombre(nombre, idActual)) {
                Alertas.advertencia("Ya existe un curso con el nombre \"" + nombre + "\".");
                return;
            }

            List<Profesor> seleccionados = new ArrayList<>(listaProfesores.getSelectionModel().getSelectedItems());

            // Copia ligera para el curso: nombre, apellidos y email
            List<Profesor> profesores = new ArrayList<>();
            List<ObjectId> idsProfesores = new ArrayList<>();
            for (Profesor p : seleccionados) {
                profesores.add(new Profesor(p.getNombre(), p.getApellidos(), p.getEmail()));
                idsProfesores.add(p.getId());
            }

            String nombreAnterior = (cursoSeleccionado == null) ? null : cursoSeleccionado.getNombre();

            if (cursoSeleccionado == null) {
                Curso nuevo = new Curso();
                rellenar(nuevo, nombre, precio, profesores);
                cursoDAO.insertar(nuevo);
            } else {
                rellenar(cursoSeleccionado, nombre, precio, profesores);
                cursoDAO.actualizar(cursoSeleccionado);
            }

            // Mantiene sincronizado el array "cursos" de cada profesor
            profesorDAO.sincronizarCurso(nombreAnterior, nombre, idsProfesores);

            cargarCursos();
            nuevoCurso();
        } catch (TonguelyException e) {
            // El DAO ya ha registrado el error en el log: aquí solo se avisa al usuario
            Alertas.error("No se pudo guardar el curso", e.getMessage());
        }
    }

    private void rellenar(Curso c, String nombre, int precio, List<Profesor> profesores) {
        c.setNombre(nombre);
        c.setPrecio(precio);
        c.setDescripcion(descripcionArea.getText().trim());
        c.setProfesores(profesores);
    }

    @FXML
    private void eliminarCurso() {
        if (cursoSeleccionado == null) {
            Alertas.advertencia("Selecciona un curso de la tabla.");
            return;
        }
        if (Alertas.confirmar("¿Eliminar el curso \"" + cursoSeleccionado.getNombre() + "\"?")) {
            try {
                cursoDAO.eliminar(cursoSeleccionado.getId());
                profesorDAO.quitarCurso(cursoSeleccionado.getNombre());
                cargarCursos();
                nuevoCurso();
            } catch (TonguelyException e) {
                // El DAO ya ha registrado el error en el log: aquí solo se avisa al usuario
                Alertas.error("No se pudo eliminar el curso", e.getMessage());
            }
        }
    }
}
