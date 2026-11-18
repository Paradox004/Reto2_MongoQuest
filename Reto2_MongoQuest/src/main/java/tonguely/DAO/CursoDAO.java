package tonguely.DAO;

import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.bson.conversions.Bson;

import tonguely.Excepciones.ConexionException;
import tonguely.Excepciones.TonguelyException;
import tonguely.Log.GestorLog;
import tonguely.Log.TipoOperacion;
import tonguely.modelo.Curso;

/**
 * Acceso a la colección "Cursos" de MongoDB mediante POJOs.
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

    /**
     * Devuelve todos los cursos.
     *
     * @return lista de cursos (vacía si no hay ninguno)
     * @throws ConexionException si MongoDB no responde
     */
    public ArrayList<Curso> cargarCursos() throws TonguelyException {
        try {
            ArrayList<Curso> cursos = coleccion.find().into(new ArrayList<>());
            GestorLog.registrar(TipoOperacion.CONSULTAR, COLECCION,
                    "Listar cursos (" + cursos.size() + ")");
            return cursos;
        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.CONSULTAR, COLECCION, e);
            throw new ConexionException("No se han podido cargar los cursos.", e);
        }
    }

    /**
     * Da de alta un curso nuevo.
     *
     * @param curso curso a insertar
     * @throws ConexionException si MongoDB no responde o rechaza el documento
     */
    public void insertar(Curso curso) throws TonguelyException {
        try {
            coleccion.insertOne(curso);
            GestorLog.registrar(TipoOperacion.INSERTAR, COLECCION, "Curso creado: " + curso.getNombre());
        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.INSERTAR, COLECCION, e);
            throw new ConexionException("No se ha podido crear el curso.", e);
        }
    }

    /**
     * Modifica el nombre, el precio, la descripción y los profesores de un curso.
     *
     * @param curso curso con los datos nuevos (se busca por su id)
     * @return true si el curso existía
     * @throws ConexionException si MongoDB no responde o rechaza los cambios
     */
    public boolean actualizar(Curso curso) throws TonguelyException {
        Bson cambios = Updates.combine(
                Updates.set("nombre", curso.getNombre()),
                Updates.set("precio", curso.getPrecio()),
                Updates.set("descripcion", curso.getDescripcion()),
                Updates.set("profesores", curso.getProfesores())
        );
        try {
            boolean existia = coleccion.updateOne(Filters.eq("_id", curso.getId()), cambios)
                    .getMatchedCount() > 0;
            if (existia) {
                GestorLog.registrar(TipoOperacion.ACTUALIZAR, COLECCION,
                        "Curso modificado: " + curso.getNombre());
            } else {
                GestorLog.registrarError(TipoOperacion.ACTUALIZAR, COLECCION,
                        "No existe el curso " + curso.getId());
            }
            return existia;
        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.ACTUALIZAR, COLECCION, e);
            throw new ConexionException("No se ha podido modificar el curso.", e);
        }
    }

    /**
     * Borra un curso.
     *
     * @param idCurso id del curso
     * @return true si se ha borrado
     * @throws ConexionException si MongoDB no responde
     */
    public boolean eliminar(ObjectId idCurso) throws TonguelyException {
        try {
            boolean borrado = coleccion.deleteOne(Filters.eq("_id", idCurso)).getDeletedCount() > 0;
            if (borrado) {
                GestorLog.registrar(TipoOperacion.BORRAR, COLECCION, "Curso borrado: " + idCurso);
            } else {
                GestorLog.registrarError(TipoOperacion.BORRAR, COLECCION,
                        "No existe el curso " + idCurso);
            }
            return borrado;
        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.BORRAR, COLECCION, e);
            throw new ConexionException("No se ha podido borrar el curso.", e);
        }
    }

    /**
     * Indica si ya hay OTRO curso con ese nombre, sin distinguir mayúsculas.
     *
     * @param nombre nombre que se quiere comprobar
     * @param idExcluido curso que se está editando (para no compararlo consigo mismo), o null
     * @return true si el nombre ya está en uso
     * @throws ConexionException si MongoDB no responde
     */
    public boolean existeNombre(String nombre, ObjectId idExcluido) throws TonguelyException {
        Pattern exacto = Pattern.compile("^" + Pattern.quote(nombre) + "$", Pattern.CASE_INSENSITIVE);
        Bson filtro = Filters.regex("nombre", exacto);
        if (idExcluido != null) {
            filtro = Filters.and(filtro, Filters.ne("_id", idExcluido));
        }
        try {
            boolean existe = coleccion.countDocuments(filtro) > 0;
            GestorLog.registrar(TipoOperacion.CONSULTAR, COLECCION, "Comprobar nombre " + nombre);
            return existe;
        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.CONSULTAR, COLECCION, e);
            throw new ConexionException("No se ha podido comprobar el nombre del curso.", e);
        }
    }
}

    
