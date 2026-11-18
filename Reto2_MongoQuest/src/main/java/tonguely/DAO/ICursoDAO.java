package tonguely.DAO;

import java.util.ArrayList;
import org.bson.types.ObjectId;
import tonguely.modelo.Curso;
import tonguely.Excepciones.TonguelyException;

/**
 * Operaciones de acceso a la colección "Cursos" de MongoDB.
 *
 * Los controladores dependen de esta interfaz y no de cómo se guardan los
 * datos: si mañana se cambiara MongoDB por otra base de datos, bastaría con
 * escribir otra implementación. Todas las operaciones registran su resultado
 * en el log y lanzan {@link TonguelyException} si la base de datos falla.
 */
public interface ICursoDAO {

    /** Devuelve el curso con ese id, o null si no existe. */
    Curso buscarPorId(ObjectId idCurso) throws TonguelyException;

    /** Devuelve todos los cursos. */
    ArrayList<Curso> cargarCursos() throws TonguelyException;

    /** Da de alta un curso nuevo. */
    void insertar(Curso curso) throws TonguelyException;

    /** Modifica un curso existente; devuelve true si existía. */
    boolean actualizar(Curso curso) throws TonguelyException;

    /** Borra un curso; devuelve true si se ha borrado. */
    boolean eliminar(ObjectId idCurso) throws TonguelyException;

    /** Indica si ya existe otro curso con ese nombre (idExcluido: el que se está editando, o null). */
    boolean existeNombre(String nombre, ObjectId idExcluido) throws TonguelyException;
}
