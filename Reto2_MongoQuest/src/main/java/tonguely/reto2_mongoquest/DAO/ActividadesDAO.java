/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tonguely.reto2_mongoquest.DAO;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.List;
import modelo.Actividades;
import modelo.Curso;

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
    
    public List<Actividades> actividadesCurso(ObjectID idCurso){
        
        return null;
        
    }
}
