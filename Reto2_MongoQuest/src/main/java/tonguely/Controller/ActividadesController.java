
package tonguely.Controller;

import java.io.IOException;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import tonguely.modelo.Actividades;
import tonguely.modelo.Curso;
import org.bson.types.ObjectId;
import tonguely.DAO.ActividadesDAO;
import tonguely.Excepciones.TonguelyException;
import tonguely.Main.App;

/**
 * Controlador encargado de mostrar y gestionar las actividades de un curso.
 *
 * @author anartz
 */
public class ActividadesController {

    /** Lista de actividades del curso. */
    ArrayList<Actividades> actividades = new ArrayList<Actividades>();

    /** Curso seleccionado. */
    Curso curso;

    /** Identificador del estudiante. */
    ObjectId idEstudiante;

    /** DAO para acceder a las actividades. */
    ActividadesDAO dao = new ActividadesDAO();

    /** Posición de la actividad actual. */
    int posicion;

    /** Contenedor de las actividades. */
    @FXML
    private VBox contenedorActividades;

    /** Etiqueta del progreso. */
    @FXML
    private Label labelProgreso;

    /**
     * Establece el curso y carga sus actividades.
     *
     * @param curso curso seleccionado.
     * @param idEstudiante identificador del estudiante.
     */
    public void setCurso(Curso curso, ObjectId idEstudiante) {
        // Se guardan antes de mostrar nada: el botón "Volver" necesita idEstudiante
        this.curso = curso;
        this.idEstudiante = idEstudiante;

        // Si MongoDB falla, el DAO ya lo ha registrado en el log: aquí solo se avisa
        try {
            actividades = dao.actividadesCurso(curso.getId());
        } catch (TonguelyException e) {
            Alertas.error("No se han podido cargar las actividades", e.getMessage());
            actividades = new ArrayList<>();
        }
        posicion = 0;
        System.out.println("Curso: " + curso.getNombre());
        System.out.println("Número de actividades: " + actividades.size());
        mostrarActividad();
    }

    /**
     * Muestra la actividad actual y comprueba las respuestas del estudiante.
     */
    private void mostrarActividad() {
        System.out.println("Mostrando actividad...");
        System.out.println("Actividades: " + actividades.size());
        contenedorActividades.getChildren().clear();
        contenedorActividades.getStyleClass().add("activity-container");

        if (posicion >= actividades.size()) {

            Label finalizado = new Label("¡Has terminado las actividades!");
            finalizado.getStyleClass().add("finish-label");
            contenedorActividades.getChildren().add(finalizado);

            Button volverButton = new Button("Volver a los cursos");
            volverButton.getStyleClass().add("primary-button");
            contenedorActividades.getChildren().add(volverButton);
            volverButton.setOnAction(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            App.class.getResource("/view/seleccionCurso.fxml")
                    );
                    Parent root = loader.load();
                    SeleccionCursoController controller = loader.getController();
                    controller.cargarCursos(idEstudiante);
                    App.scene.setRoot(root);

                } catch (IOException ex) {
                    System.getLogger(InicioSesionController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            });
            return;
        }

        Actividades ac = actividades.get(posicion);

        labelProgreso.setText(
                "Pregunta " + (posicion + 1) + " de " + actividades.size()
        );
        labelProgreso.getStyleClass().add("progress-label");
        Label pregunta = new Label(ac.getPregunta());
        pregunta.getStyleClass().add("question-label");
        contenedorActividades.getChildren().add(pregunta);

        // Mostrar preguntas de tipo test.
        if (ac.getTipo().equalsIgnoreCase("test")) {

            ArrayList<String> opciones = ac.getOpciones();
            ToggleGroup grupo = new ToggleGroup();

            for (String o : opciones) {
                RadioButton radio = new RadioButton(o);
                radio.setToggleGroup(grupo);
                radio.getStyleClass().add("answer-option");
                contenedorActividades.getChildren().add(radio);
            }

            Button comprobarButton = new Button("Comprobar");
            comprobarButton.getStyleClass().add("primary-button");
            comprobarButton.setOnAction(e -> {

                if (grupo.getSelectedToggle() == null) {
                    Alertas.advertencia("Selecciona una respuesta.");
                    return;
                }

                RadioButton seleccionada
                        = (RadioButton) grupo.getSelectedToggle();

                if (seleccionada.getText().equalsIgnoreCase(ac.getRespuesta_correcta())) {

                    Alertas.informacion("¡Muy bien!", null, "¡Respuesta correcta!");

                    posicion++;
                    mostrarActividad();

                } else {

                    Alertas.mostrar(Alert.AlertType.ERROR, "Respuesta incorrecta", null, "Respuesta incorrecta");
                }
            });

            contenedorActividades.getChildren().add(comprobarButton);

        // Mostrar preguntas de respuesta escrita.
        } else if (ac.getTipo().equalsIgnoreCase("pregunta")) {

            TextField respuestaField = new TextField();
            contenedorActividades.getChildren().add(respuestaField);
            respuestaField.setPromptText("Escribe tu respuesta...");
            respuestaField.getStyleClass().add("answer-field");
            Button comprobarButton = new Button("validar");
            contenedorActividades.getChildren().add(comprobarButton);
            comprobarButton.setOnAction(e -> {

                if (respuestaField == null) {
                    Alertas.advertencia("Selecciona una respuesta.");
                    return;
                }

                if (respuestaField.getText().equalsIgnoreCase(ac.getRespuesta_correcta())) {

                    Alertas.informacion("¡Muy bien!", null, "¡Respuesta correcta!");

                    posicion++;
                    mostrarActividad();

                } else {

                    Alertas.mostrar(Alert.AlertType.ERROR, "Respuesta incorrecta", null, "Respuesta incorrecta");
                }
            });
        }
    }
}
