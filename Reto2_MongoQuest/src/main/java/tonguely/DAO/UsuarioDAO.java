package tonguely.DAO;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.bson.types.ObjectId;
import tonguely.Excepciones.ConexionException;
import tonguely.Excepciones.DatoDuplicadoException;
import tonguely.Excepciones.TonguelyException;
import tonguely.Log.GestorLog;
import tonguely.Log.TipoOperacion;
import tonguely.Main.Sesion;
import tonguely.modelo.Usuario;

/**
 * Acceso a la colección "Usuario" de MongoDB.
 *
 * Cada método registra en el log si la operación ha ido bien o mal y,
 * si MongoDB falla, lanza una {@link ConexionException} para que el
 * controlador muestre el error al usuario.
 */
public class UsuarioDAO {

    /** Nombre de la colección, usado también en el log. */
    private static final String COLECCION = "Usuario";

    private final MongoCollection<Usuario> coleccion;

    /** Obtiene la colección de usuarios a partir de la conexión común. */
    public UsuarioDAO() {
        MongoDatabase bd = ConexionMongo.conectar();
        coleccion = bd.getCollection(COLECCION, Usuario.class);
    }

    /**
     * Da de alta un usuario nuevo si su email no está ya registrado.
     *
     * @param usuario usuario a insertar
     * @throws DatoDuplicadoException si ya existe un usuario con ese email
     * @throws ConexionException si MongoDB no responde o falla la inserción
     */
    public void crearUsuario(Usuario usuario) throws TonguelyException {
        try {
            // 1. Buscar un usuario con el mismo email, sin distinguir mayúsculas
            //    (Pattern.quote evita que caracteres como "." o "+" del email
            //    se interpreten como parte de la expresión regular)
            String patron = "^" + Pattern.quote(usuario.getEmail()) + "$";
            Usuario existente = coleccion.find(Filters.regex("email", patron, "i")).first();

            // 2. Si existe, no se inserta: se registra y se avisa con una excepción propia.
            //    No hay excepción de Mongo detrás, por eso la causa es null.
            if (existente != null) {
                GestorLog.registrarError(TipoOperacion.INSERTAR, COLECCION,
                        "Email ya registrado: " + usuario.getEmail());
                throw new DatoDuplicadoException(
                        "Ya existe un usuario con el email " + usuario.getEmail(), null);
            }

            // 3. Insertar y registrar la operación correcta
            coleccion.insertOne(usuario);
            GestorLog.registrar(TipoOperacion.INSERTAR, COLECCION,
                    "Usuario creado: " + usuario.getEmail());

        } catch (MongoException e) {
            // 4. Error de MongoDB: se registra antes del throw y se encadena la causa
            GestorLog.registrarError(TipoOperacion.INSERTAR, COLECCION, e);
            throw new ConexionException("No se ha podido crear el usuario. "
                    + "Comprueba que la base de datos está arrancada.", e);
        }
    }

    /**
     * Busca el id de un usuario a partir de su email.
     *
     * @param email email del usuario
     * @return el id del usuario, o null si no existe
     * @throws ConexionException si MongoDB no responde
     */
    public ObjectId obtenerIdPorEmail(String email) throws TonguelyException {
        try {
            Usuario usuario = coleccion.find(Filters.eq("email", email)).first();
            GestorLog.registrar(TipoOperacion.CONSULTAR, COLECCION, "Buscar id de " + email);
            return usuario != null ? usuario.getId() : null;
        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.CONSULTAR, COLECCION, e);
            throw new ConexionException("No se ha podido consultar el usuario.", e);
        }
    }

    /**
     * Devuelve todos los usuarios de la colección.
     *
     * @return lista de usuarios (vacía si no hay ninguno)
     * @throws ConexionException si MongoDB no responde
     */
    public ArrayList<Usuario> obtenerUsuarios() throws TonguelyException {
        try {
            ArrayList<Usuario> usuarios = coleccion.find().into(new ArrayList<>());
            GestorLog.registrar(TipoOperacion.CONSULTAR, COLECCION,
                    "Listar usuarios (" + usuarios.size() + ")");
            return usuarios;
        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.CONSULTAR, COLECCION, e);
            throw new ConexionException("No se ha podido obtener la lista de usuarios.", e);
        }
    }

    /**
     * Comprueba las credenciales de un usuario. Si son correctas, inicia
     * la sesión para que las siguientes líneas del log lleven su email.
     *
     * @param email email introducido
     * @param password contraseña introducida (no se escribe nunca en el log)
     * @return el usuario si las credenciales son correctas, o null si no lo son
     * @throws ConexionException si MongoDB no responde
     */
    public Usuario comprobarLogin(String email, String password) throws TonguelyException {
        try {
            // 1. Buscar al usuario por email
            Usuario usuario = coleccion.find(Filters.eq("email", email)).first();

            // 2. Credenciales incorrectas: no es un error del programa, se devuelve null
            if (usuario == null || !usuario.getContraseña().equals(password)) {
                GestorLog.registrarError(TipoOperacion.LOGIN, COLECCION,
                        "Credenciales incorrectas para " + email);
                return null;
            }

            // 3. Credenciales correctas: iniciar sesión y registrar el acceso
            Sesion.iniciar(usuario);
            GestorLog.registrar(TipoOperacion.LOGIN, COLECCION, "Inicio de sesión correcto");
            return usuario;

        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.LOGIN, COLECCION, e);
            throw new ConexionException("No se ha podido comprobar el inicio de sesión. "
                    + "Comprueba que la base de datos está arrancada.", e);
        }
    }
}
