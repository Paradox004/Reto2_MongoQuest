/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tonguely.reto2_mongoquest.Controller;

import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import modelo.Actividades;
import modelo.Curso;
import org.bson.types.ObjectId;
import tonguely.reto2_mongoquest.DAO.ActividadesDAO;

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
    
    public void setCurso(Curso curso) {
        
        this.curso=curso;
    }
    
    public void cargarActividades(){
        actividades=dao.actividadesCurso(curso.getId());
        posicion=0;
        mostrarActividad();
    }

    private void mostrarActividad() {
        
        contenedorActividades.getChildren().clear();
        
        if(posicion>=actividades.size()){
            contenedorActividades.getChildren().add(new Label("¡Has terminado las actividades!"));
            return;
        }
    }
    
}
