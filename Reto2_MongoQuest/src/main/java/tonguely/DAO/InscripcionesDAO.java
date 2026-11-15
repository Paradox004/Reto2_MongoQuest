package tonguely.DAO;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import tonguely.Excepciones.ConexionException;
import tonguely.Excepciones.TonguelyException;
import tonguely.Log.GestorLog;
import tonguely.Log.TipoOperacion;
import tonguely.modelo.Inscripciones;

/**
 * Acceso a la colección "Inscripciones" de MongoDB.
 *
 * @author anartz
 */
public class InscripcionesDAO {

    /** Nombre de la colección, usado también en el log. */
    private static final String COLECCION = "Inscripciones";

    private final MongoCollection<Inscripciones> coleccion;

    /** Obtiene la colección de inscripciones a partir de la conexión común. */
    public InscripcionesDAO() {
        MongoDatabase bd = ConexionMongo.conectar();
        coleccion = bd.getCollection(COLECCION, Inscripciones.class);
    }

    /**
     * Devuelve las inscripciones de un estudiante.
     *
     * @param idEstudiante id del estudiante
     * @return lista de inscripciones (vacía si no tiene ninguna)
     * @throws ConexionException si MongoDB no responde
     */
    public List<Inscripciones> buscarPorEstudiante(ObjectId idEstudiante) throws TonguelyException {
        try {
            List<Inscripciones> ins = coleccion.find(Filters.eq("idEstudiante", idEstudiante))
                    .into(new ArrayList<>());
            GestorLog.registrar(TipoOperacion.CONSULTAR, COLECCION,
                    "Inscripciones del estudiante (" + ins.size() + ")");
            return ins;
        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.CONSULTAR, COLECCION, e);
            throw new ConexionException("No se han podido cargar tus cursos.", e);
        }
    }

    /**
     * Indica si un estudiante está inscrito en un curso.
     *
     * @param idEstudiante id del estudiante
     * @param idCurso id del curso
     * @return true si existe la inscripción
     * @throws ConexionException si MongoDB no responde
     */
    public boolean estaInscrito(ObjectId idEstudiante, ObjectId idCurso) throws TonguelyException {
        try {
            boolean inscrito = coleccion.find(Filters.and(
                    Filters.eq("idEstudiante", idEstudiante),
                    Filters.eq("idCurso", idCurso)
            )).first() != null;
            GestorLog.registrar(TipoOperacion.CONSULTAR, COLECCION,
                    "Comprobar inscripción en curso " + idCurso);
            return inscrito;
        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.CONSULTAR, COLECCION, e);
            throw new ConexionException("No se ha podido comprobar la inscripción.", e);
        }
    }
}
