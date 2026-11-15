
package tonguely.DAO;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import tonguely.modelo.Curso;
import org.bson.types.ObjectId;

/**
 * DAO encargado de gestionar los cursos en MongoDB.
 *
 * @author anartz
 */
public class CursoDAO {

    /** Colección de cursos de MongoDB. */
    private final MongoCollection<Curso> coleccion;

    /**
     * Constructor que establece la conexión con MongoDB
     * y obtiene la colección de cursos.
     */
    public CursoDAO() {
        MongoDatabase bd = ConexionMongo.conectar();

        coleccion = bd.getCollection("Cursos", Curso.class);
    }

    /**
     * Busca un curso mediante su identificador.
     *
     * @param idCurso identificador del curso.
     * @return curso encontrado o null si no existe.
     */
    public Curso buscarPorId(ObjectId idCurso) {

        return coleccion.find(
                Filters.eq("_id", idCurso)).first();
    }
}
