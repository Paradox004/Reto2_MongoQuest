
package tonguely.DAO;

import tonguely.DAO.ConexionMongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.List;
import tonguely.modelo.Inscripciones;
import tonguely.modelo.Usuario;
import org.bson.types.ObjectId;

/**
 * DAO encargado de gestionar las inscripciones en MongoDB.
 *
 * @author anartz
 */
public class InscripcionesDAO {

    /** Colección de inscripciones de MongoDB. */
    private MongoCollection<Inscripciones> coleccion;

    /**
     * Constructor que establece la conexión con MongoDB
     * y obtiene la colección de inscripciones.
     */
    public InscripcionesDAO() {
        MongoDatabase bd = ConexionMongo.conectar();

        coleccion = bd.getCollection("Inscripciones", Inscripciones.class);
    }

    /**
     * Busca las inscripciones de un estudiante.
     *
     * @param idEstudiante identificador del estudiante.
     * @return lista de inscripciones del estudiante.
     */
    public List<Inscripciones> buscarPorEstudiante(ObjectId idEstudiante) {
        ArrayList<Inscripciones> ins = new ArrayList<Inscripciones>();
        ins = coleccion.find(
                Filters.eq("idEstudiante", idEstudiante)
        ).into(new ArrayList<>());
        return ins;
    }

    /**
     * Comprueba si un estudiante está inscrito en un curso.
     *
     * @param idEstudiante identificador del estudiante.
     * @param idCurso identificador del curso.
     * @return true si está inscrito, false en caso contrario.
     */
    public boolean estaInscrito(ObjectId idEstudiante, ObjectId idCurso) {
        return coleccion.find(
                Filters.and(
                        Filters.eq("idEstudiante", idEstudiante),
                        Filters.eq("idCurso", idCurso)
                )
        ).first() != null;
    }
}
