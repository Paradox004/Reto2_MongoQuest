package tonguely.DAO;

import java.util.List;
import org.bson.types.ObjectId;
import tonguely.modelo.Profesor;
import tonguely.Excepciones.TonguelyException;

/**
 * Operaciones de acceso a la colección "Profesores" de MongoDB.
 *
 * Los controladores dependen de esta interfaz y no de cómo se guardan los
 * datos: si mañana se cambiara MongoDB por otra base de datos, bastaría con
 * escribir otra implementación. Todas las operaciones registran su resultado
 * en el log y lanzan {@link TonguelyException} si la base de datos falla.
 */
public interface IProfesorDAO {

    /** Devuelve el profesor si las credenciales son correctas (e inicia su sesión), o null si no lo son. */
    Profesor comprobarLogin(String email, String password) throws TonguelyException;

    /** Devuelve el profesor con ese email, o null si no existe. */
    Profesor buscarPorEmail(String email) throws TonguelyException;

    /** Devuelve todos los profesores. */
    List<Profesor> buscarTodos() throws TonguelyException;

    /** Quita un curso (por nombre) de todos los profesores. */
    void quitarCurso(String nombreCurso) throws TonguelyException;

    /** Añade un curso (por nombre) a un profesor. */
    void agregarCurso(ObjectId idProfesor, String nombreCurso) throws TonguelyException;

    /** Deja los profesores indicados como únicos asignados al curso. */
    void sincronizarCurso(String nombreAnterior, String nombreNuevo, List<ObjectId> idsProfesores) throws TonguelyException;
}
