/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tonguely.reto2_mongoquest.DAO;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import modelo.Curso;
import org.bson.types.ObjectId;

/**
 *
 * @author songo
 */
public class CursoDAO {
        private MongoCollection<Curso> coleccion;
    
        public CursoDAO(){
        MongoDatabase bd = ConexionMongo.conectar();

        coleccion = bd.getCollection("Cursos", Curso.class);
    }
        public Curso buscarPorId(ObjectId idCurso) {

        return coleccion.find(
        Filters.eq("_id", idCurso)).first();
    }
       
}
