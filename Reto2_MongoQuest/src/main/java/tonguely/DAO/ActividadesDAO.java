/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tonguely.DAO;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.List;
import tonguely.modelo.Actividades;
import tonguely.modelo.Curso;
import org.bson.types.ObjectId;

/**
 *
 * @author songo
 */
public class ActividadesDAO {
    
    private MongoCollection<Actividades> actividades;
    
    public ActividadesDAO(){
        MongoDatabase bd = ConexionMongo.conectar();

        actividades = bd.getCollection("Actividades", Actividades.class);
    }
    
    public ArrayList<Actividades> actividadesCurso(ObjectId idCurso){
        
        ArrayList<Actividades> ac=actividades.find(Filters.eq("curso_id",idCurso)).into(new ArrayList<>());
        return ac;
    }
}
