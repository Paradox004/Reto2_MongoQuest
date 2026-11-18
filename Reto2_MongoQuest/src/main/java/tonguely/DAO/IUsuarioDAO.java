package tonguely.DAO;

import java.util.ArrayList;
import org.bson.types.ObjectId;
import tonguely.modelo.Usuario;
import tonguely.Excepciones.TonguelyException;

/**
 * Operaciones de acceso a la colección "Usuario" de MongoDB.
 *
 * Los controladores dependen de esta interfaz y no de cómo se guardan los
 * datos: si mañana se cambiara MongoDB por otra base de datos, bastaría con
 * escribir otra implementación. Todas las operaciones registran su resultado
 * en el log y lanzan {@link TonguelyException} si la base de datos falla.
 */
public interface IUsuarioDAO {

    /**
     * Da de alta un usuario si su email no está registrado. Lanza DatoDuplicadoException si ya existe.
     *
     * @param usuario usuario a insertar
     * @throws TonguelyException DatoDuplicadoException si el email ya existe, o ConexionException si falla MongoDB
     */
    void crearUsuario(Usuario usuario) throws TonguelyException;

    /**
     * Devuelve el id del usuario con ese email, o null si no existe.
     *
     * @param email email del usuario
     * @return el id, o null si no existe
     * @throws TonguelyException si falla MongoDB
     */
    ObjectId obtenerIdPorEmail(String email) throws TonguelyException;

    /**
     * Devuelve todos los usuarios.
     *
     * @return lista de usuarios (vacía si no hay)
     * @throws TonguelyException si falla MongoDB
     */
    ArrayList<Usuario> obtenerUsuarios() throws TonguelyException;

    /**
     * Devuelve el usuario si las credenciales son correctas (e inicia su sesión), o null si no lo son.
     *
     * @param email email introducido
     * @param password contraseña introducida
     * @return el usuario, o null si las credenciales no son correctas
     * @throws TonguelyException si falla MongoDB
     */
    Usuario comprobarLogin(String email, String password) throws TonguelyException;
}
