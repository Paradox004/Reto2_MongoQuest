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

    /**
     * Devuelve el profesor si las credenciales son correctas (e inicia su sesión), o null si no lo son.
     *
     * @param email email introducido
     * @param password contraseña introducida
     * @return el profesor, o null si no es profesor o la contraseña no coincide
     * @throws TonguelyException si falla MongoDB
     */
    Profesor comprobarLogin(String email, String password) throws TonguelyException;

    /**
     * Devuelve el profesor con ese email, o null si no existe.
     *
     * @param email email del profesor
     * @return el profesor, o null si no existe
     * @throws TonguelyException si falla MongoDB
     */
    Profesor buscarPorEmail(String email) throws TonguelyException;

    /**
     * Devuelve todos los profesores.
     *
     * @return lista de profesores (vacía si no hay)
     * @throws TonguelyException si falla MongoDB
     */
    List<Profesor> buscarTodos() throws TonguelyException;

    /**
     * Quita un curso (por nombre) de todos los profesores.
     *
     * @param nombreCurso nombre del curso; si es null no se hace nada
     * @throws TonguelyException si falla MongoDB
     */
    void quitarCurso(String nombreCurso) throws TonguelyException;

    /**
     * Añade un curso (por nombre) a un profesor.
     *
     * @param idProfesor id del profesor
     * @param nombreCurso nombre del curso
     * @throws TonguelyException si falla MongoDB
     */
    void agregarCurso(ObjectId idProfesor, String nombreCurso) throws TonguelyException;

    /**
     * Deja los profesores indicados como únicos asignados al curso.
     *
     * @param nombreAnterior nombre que tenía el curso, o null si es nuevo
     * @param nombreNuevo nombre actual del curso
     * @param idsProfesores profesores que deben quedar asignados
     * @throws TonguelyException si falla MongoDB
     */
    void sincronizarCurso(String nombreAnterior, String nombreNuevo, List<ObjectId> idsProfesores) throws TonguelyException;
}
