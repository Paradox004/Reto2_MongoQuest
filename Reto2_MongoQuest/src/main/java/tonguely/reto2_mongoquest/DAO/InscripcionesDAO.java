/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tonguely.reto2_mongoquest.DAO;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.List;
import modelo.Inscripciones;
import modelo.Usuario;

/**
 *
 * @author songo
 */
public class InscripcionesDAO {
    
    private MongoCollection<Inscripciones> coleccion;
    
    public InscripcionesDAO(){
        MongoDatabase bd = ConexionMongo.conectar();

        coleccion = bd.getCollection("Inscripciones", Inscripciones.class);
    }
    
    public List<Inscripciones> buscarPorEstudiante(int idEstudiante) {
        ArrayList<Inscripciones> ins=new ArrayList<Inscripciones>();
        ins=coleccion.find(Filters.eq("idEstudiante", idEstudiante)).into(new ArrayList<>());
        return ins;
    }

    public boolean estaInscrito(int idEstudiante, int idCurso) {
    return coleccion.find(
        Filters.and(
            Filters.eq("idEstudiante", idEstudiante),
            Filters.eq("idCurso", idCurso)
        )
        ).first() != null;
    }
}
