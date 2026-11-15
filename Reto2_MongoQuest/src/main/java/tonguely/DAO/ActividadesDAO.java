package tonguely.DAO;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import org.bson.types.ObjectId;
import tonguely.Excepciones.ConexionException;
import tonguely.Excepciones.TonguelyException;
import tonguely.Log.GestorLog;
import tonguely.Log.TipoOperacion;
import tonguely.modelo.Actividades;

/**
 * Acceso a la colección "Actividades" de MongoDB.
 *
 * @author songo
 */
public class ActividadesDAO {

    /** Nombre de la colección, usado también en el log. */
    private static final String COLECCION = "Actividades";

    private final MongoCollection<Actividades> actividades;

    /** Obtiene la colección de actividades a partir de la conexión común. */
    public ActividadesDAO() {
        MongoDatabase bd = ConexionMongo.conectar();
        actividades = bd.getCollection(COLECCION, Actividades.class);
    }

    /**
     * Devuelve las actividades de un curso.
     *
     * @param idCurso id del curso
     * @return lista de actividades (vacía si el curso no tiene ninguna)
     * @throws ConexionException si MongoDB no responde
     */
    public ArrayList<Actividades> actividadesCurso(ObjectId idCurso) throws TonguelyException {
        try {
            ArrayList<Actividades> ac = actividades.find(Filters.eq("curso_id", idCurso))
                    .into(new ArrayList<>());
            GestorLog.registrar(TipoOperacion.CONSULTAR, COLECCION,
                    "Actividades del curso " + idCurso + " (" + ac.size() + ")");
            return ac;
        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.CONSULTAR, COLECCION, e);
            throw new ConexionException("No se han podido cargar las actividades.", e);
        }
    }
}
