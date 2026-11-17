package tonguely.DAO;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
    
    /**
     * Devuelve las inscripciones de varios cursos a la vez.
     *
     * @param idsCursos ids de los cursos
     * @return lista de inscripciones (vacía si no se pasa ningún curso)
     * @throws ConexionException si MongoDB no responde
     */
    public List<Inscripciones> buscarPorCursos(Collection<ObjectId> idsCursos) throws TonguelyException {
        if (idsCursos == null || idsCursos.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            List<Inscripciones> ins = coleccion.find(Filters.in("idCurso", idsCursos))
                    .into(new ArrayList<>());
            GestorLog.registrar(TipoOperacion.CONSULTAR, COLECCION,
                    "Inscripciones de " + idsCursos.size() + " cursos (" + ins.size() + ")");
            return ins;
        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.CONSULTAR, COLECCION, e);
            throw new ConexionException("No se han podido cargar las inscripciones.", e);
        }
    }

    /**
     * Inscribe a un estudiante en un curso. Si ya estaba inscrito, no hace nada.
     *
     * @param idEstudiante id del estudiante
     * @param idCurso id del curso
     * @throws ConexionException si MongoDB no responde
     */
    public void inscribir(ObjectId idEstudiante, ObjectId idCurso) throws TonguelyException {
        // 1. Evitar inscripciones repetidas (estaInscrito ya registra y lanza si falla)
        if (estaInscrito(idEstudiante, idCurso)) {
            return;
        }

        // 2. Crear la inscripción con sus valores iniciales
        Inscripciones ins = new Inscripciones();
        ins.setIdEstudiante(idEstudiante);
        ins.setIdCurso(idCurso);
        ins.setProgreso(0);
        ins.setFechaInicio(new Date());
        ins.setEstado("En curso");

        // 3. Insertar y registrar
        try {
            coleccion.insertOne(ins);
            GestorLog.registrar(TipoOperacion.INSERTAR, COLECCION,
                    "Inscribir estudiante " + idEstudiante + " en curso " + idCurso);
        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.INSERTAR, COLECCION, e);
            throw new ConexionException("No se ha podido realizar la inscripción.", e);
        }
    }

    /**
     * Quita a un estudiante de un curso.
     *
     * @param idEstudiante id del estudiante
     * @param idCurso id del curso
     * @throws ConexionException si MongoDB no responde
     */
    public void desinscribir(ObjectId idEstudiante, ObjectId idCurso) throws TonguelyException {
        try {
            long borradas = coleccion.deleteMany(Filters.and(
                    Filters.eq("idEstudiante", idEstudiante),
                    Filters.eq("idCurso", idCurso))).getDeletedCount();
            GestorLog.registrar(TipoOperacion.BORRAR, COLECCION,
                    "Quitar estudiante " + idEstudiante + " del curso " + idCurso
                    + " (" + borradas + ")");
        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.BORRAR, COLECCION, e);
            throw new ConexionException("No se ha podido quitar la inscripción.", e);
        }
    }
}
