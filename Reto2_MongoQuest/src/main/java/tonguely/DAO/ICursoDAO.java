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

    /**
     * Devuelve el curso con ese id, o null si no existe.
     *
     * @param idCurso id del curso
     * @return el curso, o null si no existe
     * @throws TonguelyException si falla MongoDB
     */
    Curso buscarPorId(ObjectId idCurso) throws TonguelyException;

    /**
     * Devuelve todos los cursos.
     *
     * @return lista de cursos (vacía si no hay)
     * @throws TonguelyException si falla MongoDB
     */
    ArrayList<Curso> cargarCursos() throws TonguelyException;

    /**
     * Da de alta un curso nuevo.
     *
     * @param curso curso a insertar
     * @throws TonguelyException si falla MongoDB o rechaza el documento
     */
    void insertar(Curso curso) throws TonguelyException;

    /**
     * Modifica un curso existente; devuelve true si existía.
     *
     * @param curso curso con los datos nuevos (se busca por su id)
     * @return true si el curso existía
     * @throws TonguelyException si falla MongoDB o rechaza los cambios
     */
    boolean actualizar(Curso curso) throws TonguelyException;

    /**
     * Borra un curso; devuelve true si se ha borrado.
     *
     * @param idCurso id del curso
     * @return true si se ha borrado
     * @throws TonguelyException si falla MongoDB
     */
    boolean eliminar(ObjectId idCurso) throws TonguelyException;

    /**
     * Indica si ya existe otro curso con ese nombre (idExcluido: el que se está editando, o null).
     *
     * @param nombre nombre que se quiere comprobar
     * @param idExcluido curso que se está editando, o null
     * @return true si el nombre ya está en uso
     * @throws TonguelyException si falla MongoDB
     */
    boolean existeNombre(String nombre, ObjectId idExcluido) throws TonguelyException;
}
