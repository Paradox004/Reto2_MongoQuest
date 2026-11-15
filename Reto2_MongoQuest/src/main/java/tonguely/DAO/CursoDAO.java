package tonguely.DAO;

import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import tonguely.Excepciones.ConexionException;
import tonguely.Excepciones.TonguelyException;
import tonguely.Log.GestorLog;
import tonguely.Log.TipoOperacion;
import tonguely.modelo.Curso;

/**
 * Acceso a la colección "Cursos" de MongoDB.
 *
 * @author anartz
 */
public class CursoDAO {

    /** Nombre de la colección, usado también en el log. */
    private static final String COLECCION = "Cursos";

    private final MongoCollection<Curso> coleccion;

    /** Obtiene la colección de cursos a partir de la conexión común. */
    public CursoDAO() {
        MongoDatabase bd = ConexionMongo.conectar();
        coleccion = bd.getCollection(COLECCION, Curso.class);
    }

    /**
     * Busca un curso por su id.
     *
     * @param idCurso id del curso
     * @return el curso, o null si no existe
     * @throws ConexionException si MongoDB no responde
     */
    public Curso buscarPorId(ObjectId idCurso) throws TonguelyException {
        try {
            Curso curso = coleccion.find(Filters.eq("_id", idCurso)).first();
            GestorLog.registrar(TipoOperacion.CONSULTAR, COLECCION, "Buscar curso " + idCurso);
            return curso;
        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.CONSULTAR, COLECCION, e);
            throw new ConexionException("No se ha podido cargar el curso.", e);
        }
    }
}
