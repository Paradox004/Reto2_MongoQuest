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

    public ArrayList<Curso> cargarCursos() throws TonguelyException {
        try {
            return coleccion.find().into(new ArrayList<>());
        } catch (MongoException e) {
            throw new ConexionException("No se ha podido cargar los cursos.", e);
        }
    }

    public void insertar(Curso curso) {
        coleccion.insertOne(curso);
    }

    public boolean actualizar(Curso curso) {
        Bson cambios = Updates.combine(
                Updates.set("nombre", curso.getNombre()),
                Updates.set("precio", curso.getPrecio()),
                Updates.set("descripcion", curso.getDescripcion()),
                Updates.set("profesores", curso.getProfesores())
        );
        return coleccion.updateOne(Filters.eq("_id", curso.getId()), cambios).getMatchedCount() > 0;
    }

    public boolean eliminar(ObjectId idCurso) {
        return coleccion.deleteOne(Filters.eq("_id", idCurso)).getDeletedCount() > 0;
    }

    // true si ya hay OTRO curso con ese nombre (idExcluido = curso que se está editando, o null)
    public boolean existeNombre(String nombre, ObjectId idExcluido) {
        Pattern exacto = Pattern.compile("^" + Pattern.quote(nombre) + "$", Pattern.CASE_INSENSITIVE);
        Bson filtro = Filters.regex("nombre", exacto);
        if (idExcluido != null) {
            filtro = Filters.and(filtro, Filters.ne("_id", idExcluido));
        }
        return coleccion.countDocuments(filtro) > 0;
    }
}

    
