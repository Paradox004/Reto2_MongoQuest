
package tonguely.DAO;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import tonguely.modelo.Actividades;
import tonguely.modelo.Curso;
import org.bson.types.ObjectId;

/**
 * DAO encargado de gestionar las actividades en MongoDB.
 *
 * @author anartz
 */
public class ActividadesDAO {

    /** Colección de actividades de MongoDB. */
    private MongoCollection<Actividades> actividades;

    /**
     * Constructor que establece la conexión con MongoDB
     * y obtiene la colección de actividades.
     */
    public ActividadesDAO() {
        MongoDatabase bd = ConexionMongo.conectar();

        actividades = bd.getCollection("Actividades", Actividades.class);
    }

    /**
     * Obtiene las actividades asociadas a un curso.
     *
     * @param idCurso identificador del curso.
     * @return lista de actividades del curso.
     */
    public ArrayList<Actividades> actividadesCurso(ObjectId idCurso) {

        ArrayList<Actividades> ac = actividades.find(
                Filters.eq("curso_id", idCurso)
        ).into(new ArrayList<>());

        return ac;
    }
}

