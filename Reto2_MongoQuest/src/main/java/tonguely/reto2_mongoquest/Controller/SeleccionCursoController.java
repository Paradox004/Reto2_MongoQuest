/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package tonguely.reto2_mongoquest.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import modelo.Curso;
import modelo.Inscripciones;
import org.bson.types.ObjectId;
import tonguely.reto2_mongoquest.DAO.CursoDAO;
import tonguely.reto2_mongoquest.DAO.InscripcionesDAO;

/**
 * FXML Controller class
 *
 * @author songo
 */
public class SeleccionCursoController implements Initializable {

    @FXML
    private GridPane gridCursos;

    private final InscripcionesDAO inscripcionesDAO =new InscripcionesDAO();

    private final CursoDAO cursoDAO = new CursoDAO();
    
    
    public void cargarCursos(ObjectId idEstudiante) {

    gridCursos.getChildren().clear();

    // Obtener las inscripciones del alumno
    List<Inscripciones> inscripciones =
            inscripcionesDAO.buscarPorEstudiante(idEstudiante);

    int columna = 0;
    int fila = 0;

    for (Inscripciones inscripcion : inscripciones) {

        // Obtener el curso correspondiente
        Curso curso = cursoDAO.buscarPorId(
                inscripcion.getIdCurso());

        if (curso == null) {
            continue;
        }

        // Crear la tarjeta
        VBox tarjeta = new VBox(10);
        tarjeta.setPrefSize(230, 180);
        tarjeta.setMinSize(200, 160);
        tarjeta.setPadding(new Insets(15));

        tarjeta.setStyle(
            "-fx-background-color: #EAF2F8;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-border-color: #C5D6E5;"
        );

        // Nombre del curso
        Label nombre = new Label(curso.getNombre());
        nombre.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        // Estado de inscripción
        Label estado = new Label("Inscrito");
        estado.setStyle("-fx-text-fill: green;");

        // Botón para entrar al curso
        Button entrar = new Button("Entrar al curso");

        entrar.setOnAction(e -> {
            System.out.println("Entrando al curso: " + curso.getNombre());

            // Aquí puedes abrir la pantalla del curso
        });

        tarjeta.getChildren().addAll(
            nombre,estado,entrar
        );

        // Añadir tarjeta al GridPane
        gridCursos.add(tarjeta, columna, fila);

        // Organizar en dos columnas
        columna++;

        if (columna == 2) {
            columna = 0;
            fila++;
        }
    }
}

    /**
     * Initializes the controller class.
     */
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    
    
}
