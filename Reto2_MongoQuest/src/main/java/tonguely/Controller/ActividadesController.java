/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tonguely.Controller;

import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import tonguely.modelo.Actividades;
import tonguely.modelo.Curso;
import org.bson.types.ObjectId;
import tonguely.DAO.ActividadesDAO;

/**
 *
 * @author songo
 */
public class ActividadesController {
    ArrayList<Actividades> actividades=new ArrayList<Actividades>();
    Curso curso;
    ActividadesDAO dao=new ActividadesDAO();
    int posicion;
    @FXML
    private VBox contenedorActividades;
    @FXML
    private Label labelProgreso;
    
    public void setCurso(Curso curso) {
        
        this.curso=curso;
    }
    
    @FXML
    public void initialize() {
        actividades=dao.actividadesCurso(curso.getId());
        posicion = 0;
        mostrarActividad();
    }
    
    private void mostrarActividad() {

    contenedorActividades.getChildren().clear();

    if (posicion >= actividades.size()) {
        contenedorActividades.getChildren().add(
            new Label("¡Has terminado las actividades!")
        );
        return;
    }

    Actividades ac = actividades.get(posicion);

    labelProgreso.setText(
        "Pregunta " + (posicion + 1) + " de " + actividades.size()
    );

    Label pregunta = new Label(ac.getPregunta());
    contenedorActividades.getChildren().add(pregunta);

    if (ac.getTipo().equalsIgnoreCase("test")) {

        ArrayList<String> opciones = ac.getOpciones();
        ToggleGroup grupo = new ToggleGroup();

        for (String o : opciones) {
            RadioButton radio = new RadioButton(o);
            radio.setToggleGroup(grupo);
            contenedorActividades.getChildren().add(radio);
        }

        Button comprobarButton = new Button("Comprobar");

        comprobarButton.setOnAction(e -> {

            if (grupo.getSelectedToggle() == null) {
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setContentText("Selecciona una respuesta.");
                alerta.showAndWait();
                return;
            }

            RadioButton seleccionada =
                (RadioButton) grupo.getSelectedToggle();

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
    }
    else if (ac.getTipo().equalsIgnoreCase("responder")) {

        

    }
    }
    
}
