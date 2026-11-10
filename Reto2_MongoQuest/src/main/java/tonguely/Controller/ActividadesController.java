/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import tonguely.Main.App;

/**
 *
 * @author songo
 */
public class ActividadesController {

    ArrayList<Actividades> actividades = new ArrayList<Actividades>();
    Curso curso;
    ObjectId idEstudiante;
    ActividadesDAO dao = new ActividadesDAO();
    int posicion;
    @FXML
    private VBox contenedorActividades;
    @FXML
    private Label labelProgreso;

    public void setCurso(Curso curso, ObjectId idEstudiante) {
        actividades = dao.actividadesCurso(curso.getId());
        posicion = 0;
        System.out.println("Curso: " + curso.getNombre());
        System.out.println("Número de actividades: " + actividades.size());
        mostrarActividad();
        this.curso = curso;
        this.idEstudiante = idEstudiante;
    }

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
                    Alert alerta = new Alert(Alert.AlertType.WARNING);
                    alerta.setContentText("Selecciona una respuesta.");
                    alerta.showAndWait();
                    return;
                }

                RadioButton seleccionada
                        = (RadioButton) grupo.getSelectedToggle();

                if (seleccionada.getText().equalsIgnoreCase(ac.getRespuesta_correcta())) {

                    Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                    alerta.setTitle("¡Muy bien!");
                    alerta.setContentText("¡Respuesta correcta!");
                    alerta.showAndWait();

                    posicion++;
                    mostrarActividad();

                } else {

                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setTitle("Respuesta incorrecta");
                    alerta.setContentText("Respuesta incorrecta");
                    alerta.showAndWait();
                }
            });

            contenedorActividades.getChildren().add(comprobarButton);
        } else if (ac.getTipo().equalsIgnoreCase("pregunta")) {

            TextField respuestaField = new TextField();
            contenedorActividades.getChildren().add(respuestaField);
            respuestaField.setPromptText("Escribe tu respuesta...");
            respuestaField.getStyleClass().add("answer-field");
            Button comprobarButton = new Button("validar");
            contenedorActividades.getChildren().add(comprobarButton);
            comprobarButton.setOnAction(e -> {

                if (respuestaField == null) {
                    Alert alerta = new Alert(Alert.AlertType.WARNING);
                    alerta.setContentText("Selecciona una respuesta.");
                    alerta.showAndWait();
                    return;
                }

                if (respuestaField.getText().equalsIgnoreCase(ac.getRespuesta_correcta())) {

                    Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                    alerta.setTitle("¡Muy bien!");
                    alerta.setContentText("¡Respuesta correcta!");
                    alerta.showAndWait();

                    posicion++;
                    mostrarActividad();

                } else {

                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setTitle("Respuesta incorrecta");
                    alerta.setContentText("Respuesta incorrecta");
                    alerta.showAndWait();
                }
            });
        }
    }

}
