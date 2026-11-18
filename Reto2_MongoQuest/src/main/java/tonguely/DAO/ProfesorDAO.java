package tonguely.DAO;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import tonguely.Excepciones.ConexionException;
import tonguely.Excepciones.TonguelyException;
import tonguely.Log.GestorLog;
import tonguely.Log.TipoOperacion;
import tonguely.Main.Sesion;
import tonguely.modelo.Profesor;

/**
 * Acceso a la colección "Profesores" de MongoDB.
 *
 * Cada método registra en el log si la operación ha ido bien o mal y,
 * si MongoDB falla, lanza una {@link ConexionException} para que el
 * controlador muestre el error al usuario.
 *
 * @author anartz
 */
public class ProfesorDAO {

    /** Nombre de la colección, usado también en el log. */
    private static final String COLECCION = "Profesores";

    private final MongoCollection<Profesor> coleccion;

    /** Obtiene la colección de profesores a partir de la conexión común. */
    public ProfesorDAO() {
        MongoDatabase bd = ConexionMongo.conectar();
        coleccion = bd.getCollection(COLECCION, Profesor.class);
    }

    /**
     * Comprueba las credenciales de un profesor. Si son correctas, inicia su
     * sesión para que las siguientes líneas del log lleven su email.
     *
     * Si el email no es de ningún profesor no se registra nada: el login
     * prueba después con los usuarios, y es UsuarioDAO quien registra el
     * resultado final.
     *
     * @param email email introducido
     * @param password contraseña introducida (no se escribe nunca en el log)
     * @return el profesor si las credenciales son correctas, o null si no lo son
     * @throws ConexionException si MongoDB no responde
     */
    public Profesor comprobarLogin(String email, String password) throws TonguelyException {
        try {
            Profesor profesor = coleccion.find(Filters.eq("email", email)).first();

            // 1. No es un profesor: se deja seguir al login de usuarios
            if (profesor == null) {
                return null;
            }

            // 2. Es un profesor pero la contraseña no coincide
            if (profesor.getContraseña() == null || !profesor.getContraseña().equals(password)) {
                GestorLog.registrarError(TipoOperacion.LOGIN, COLECCION,
                        "Credenciales incorrectas para el profesor " + email);
                return null;
            }

            // 3. Correcto: iniciar sesión y registrar el acceso
            Sesion.iniciarProfesor(profesor);
            GestorLog.registrar(TipoOperacion.LOGIN, COLECCION, "Inicio de sesión de profesor");
            return profesor;

        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.LOGIN, COLECCION, e);
            throw new ConexionException("No se ha podido comprobar el inicio de sesión. "
                    + "Comprueba que la base de datos está arrancada.", e);
        }
    }

    /**
     * Busca un profesor por su email.
     *
     * @param email email del profesor
     * @return el profesor, o null si no existe
     * @throws ConexionException si MongoDB no responde
     */
    public Profesor buscarPorEmail(String email) throws TonguelyException {
        try {
            Profesor profesor = coleccion.find(Filters.eq("email", email)).first();
            GestorLog.registrar(TipoOperacion.CONSULTAR, COLECCION, "Buscar profesor " + email);
            return profesor;
        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.CONSULTAR, COLECCION, e);
            throw new ConexionException("No se ha podido consultar el profesor.", e);
        }
    }

    /**
     * Devuelve todos los profesores.
     *
     * @return lista de profesores (vacía si no hay ninguno)
     * @throws ConexionException si MongoDB no responde
     */
    public List<Profesor> buscarTodos() throws TonguelyException {
        try {
            List<Profesor> profesores = coleccion.find().into(new ArrayList<>());
            GestorLog.registrar(TipoOperacion.CONSULTAR, COLECCION,
                    "Listar profesores (" + profesores.size() + ")");
            return profesores;
        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.CONSULTAR, COLECCION, e);
            throw new ConexionException("No se han podido cargar los profesores.", e);
        }
    }

    /**
     * Quita un curso (por nombre) del array "cursos" de todos los profesores.
     *
     * @param nombreCurso nombre del curso que se quita; si es null no se hace nada
     * @throws ConexionException si MongoDB no responde
     */
    public void quitarCurso(String nombreCurso) throws TonguelyException {
        if (nombreCurso == null) {
            return;
        }
        try {
            long modificados = coleccion.updateMany(
                    Filters.exists("cursos"),
                    Updates.pull("cursos", new Document("nombre", nombreCurso))).getModifiedCount();
            GestorLog.registrar(TipoOperacion.ACTUALIZAR, COLECCION,
                    "Quitar curso " + nombreCurso + " (" + modificados + " profesores)");
        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.ACTUALIZAR, COLECCION, e);
            throw new ConexionException("No se ha podido quitar el curso a los profesores.", e);
        }
    }

    /**
     * Añade un curso (solo con su nombre, como ya está en la BD) a un profesor.
     *
     * @param idProfesor id del profesor
     * @param nombreCurso nombre del curso que se añade
     * @throws ConexionException si MongoDB no responde
     */
    public void agregarCurso(ObjectId idProfesor, String nombreCurso) throws TonguelyException {
        try {
            coleccion.updateOne(
                    Filters.eq("_id", idProfesor),
                    Updates.push("cursos", new Document("nombre", nombreCurso)));
            GestorLog.registrar(TipoOperacion.ACTUALIZAR, COLECCION,
                    "Asignar curso " + nombreCurso + " al profesor " + idProfesor);
        } catch (MongoException e) {
            GestorLog.registrarError(TipoOperacion.ACTUALIZAR, COLECCION, e);
            throw new ConexionException("No se ha podido asignar el curso al profesor.", e);
        }
    }

    /**
     * Deja los profesores indicados como únicos asignados al curso.
     * Cada paso (quitar y añadir) ya se registra en el log por separado.
     *
     * @param nombreAnterior nombre que tenía el curso, o null si es nuevo
     * @param nombreNuevo nombre actual del curso
     * @param idsProfesores profesores que deben quedar asignados
     * @throws ConexionException si MongoDB no responde
     */
    public void sincronizarCurso(String nombreAnterior, String nombreNuevo,
            List<ObjectId> idsProfesores) throws TonguelyException {
        quitarCurso(nombreAnterior);
        if (nombreAnterior == null || !nombreAnterior.equals(nombreNuevo)) {
            quitarCurso(nombreNuevo); // evita duplicados
        }
        for (ObjectId id : idsProfesores) {
            agregarCurso(id, nombreNuevo);
        }
    }
}
