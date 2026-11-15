
package tonguely.DAO;

import tonguely.DAO.ConexionMongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.eq;
import java.util.ArrayList;
import tonguely.modelo.Usuario;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * DAO encargado de gestionar los usuarios en MongoDB.
 *
 * @author anartz
 */
public class UsuarioDAO {

    /** Colección de usuarios de MongoDB. */
    private MongoCollection<Usuario> coleccion;

    /**
     * Constructor que establece la conexión con MongoDB
     * y obtiene la colección de usuarios.
     */
    public UsuarioDAO() {

        MongoDatabase bd = ConexionMongo.conectar();

        coleccion = bd.getCollection("Usuario", Usuario.class);
    }

    /**
     * Crea un nuevo usuario si su correo no existe.
     *
     * @param usuario usuario que se quiere registrar.
     * @return true si se crea correctamente, false si ya existe
     * o se produce un error.
     */
    public boolean crearUsuario(Usuario usuario) {
        boolean existe = false;
        for (Usuario u : coleccion.find()) {
            if (u.getEmail().equalsIgnoreCase(usuario.getEmail())) {
                existe = true;
            }
        }
        if (existe) {
            return false;
        } else {
            try {
                coleccion.insertOne(usuario);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Obtiene el identificador de un usuario mediante su correo.
     *
     * @param email correo electrónico del usuario.
     * @return identificador del usuario o null si no existe.
     */
    public ObjectId obtenerIdPorEmail(String email) {
        Usuario usuario = coleccion.find(
                Filters.eq("email", email)).first();
        if (usuario != null) {
            return usuario.getId();
        }
        return null;
    }

    /**
     * Obtiene todos los usuarios registrados.
     *
     * @return lista de usuarios.
     */
    public ArrayList<Usuario> obtenerUsuarios() {

        ArrayList<Usuario> usuarios = new ArrayList<>();

        for (Usuario usuario : coleccion.find()) {
            usuarios.add(usuario);
        }

        return usuarios;
    }

    /**
     * Comprueba las credenciales de inicio de sesión.
     *
     * @param email correo electrónico del usuario.
     * @param password contraseña del usuario.
     * @return usuario si las credenciales son correctas o null si no coinciden.
     */
    public Usuario comprobarLogin(String email, String password) {

        // Buscar al usuario por email
        Usuario usuario = coleccion.find(eq("email", email)).first();

        // Comprobar si existe y si la contraseña coincide
        if (usuario != null
                && usuario.getContraseña().equals(password)) {
            return usuario;
        }

        return null;
    }
}
