package tonguely.DAO;

import java.util.ArrayList;
import org.bson.types.ObjectId;
import tonguely.modelo.Actividades;
import tonguely.Excepciones.TonguelyException;

/**
 * Operaciones de acceso a la colección "Actividades" de MongoDB.
 *
 * Los controladores dependen de esta interfaz y no de cómo se guardan los
 * datos: si mañana se cambiara MongoDB por otra base de datos, bastaría con
 * escribir otra implementación. Todas las operaciones registran su resultado
 * en el log y lanzan {@link TonguelyException} si la base de datos falla.
 */
public interface IActividadesDAO {

    /** Devuelve las actividades de un curso. */
    ArrayList<Actividades> actividadesCurso(ObjectId idCurso) throws TonguelyException;
}
