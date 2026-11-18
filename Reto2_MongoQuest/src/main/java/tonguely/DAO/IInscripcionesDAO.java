package tonguely.DAO;

import java.util.Collection;
import java.util.List;
import org.bson.types.ObjectId;
import tonguely.modelo.Inscripciones;
import tonguely.Excepciones.TonguelyException;

/**
 * Operaciones de acceso a la colección "Inscripciones" de MongoDB.
 *
 * Los controladores dependen de esta interfaz y no de cómo se guardan los
 * datos: si mañana se cambiara MongoDB por otra base de datos, bastaría con
 * escribir otra implementación. Todas las operaciones registran su resultado
 * en el log y lanzan {@link TonguelyException} si la base de datos falla.
 */
public interface IInscripcionesDAO {

    /** Devuelve las inscripciones de un estudiante. */
    List<Inscripciones> buscarPorEstudiante(ObjectId idEstudiante) throws TonguelyException;

    /** Indica si un estudiante está inscrito en un curso. */
    boolean estaInscrito(ObjectId idEstudiante, ObjectId idCurso) throws TonguelyException;

    /** Devuelve las inscripciones de varios cursos a la vez. */
    List<Inscripciones> buscarPorCursos(Collection<ObjectId> idsCursos) throws TonguelyException;

    /** Inscribe a un estudiante en un curso (si ya lo estaba, no hace nada). */
    void inscribir(ObjectId idEstudiante, ObjectId idCurso) throws TonguelyException;

    /** Quita a un estudiante de un curso. */
    void desinscribir(ObjectId idEstudiante, ObjectId idCurso) throws TonguelyException;
}
