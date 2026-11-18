package tonguely.Main;

import tonguely.Log.GestorLog;
import tonguely.Log.TipoOperacion;
import tonguely.modelo.Profesor;
import tonguely.modelo.Usuario;

/**
 * Guarda quién ha iniciado sesión mientras la aplicación está abierta:
 * un usuario (estudiante) o un profesor (administrador), nunca los dos a la vez.
 *
 * Es estática porque solo hay una sesión a la vez en una aplicación de escritorio,
 * y así cualquier clase (por ejemplo {@link tonguely.Log.GestorLog}) puede saber
 * quién está conectado sin tener que pasar el usuario de pantalla en pantalla.
 */
public final class Sesion {

    /** Usuario conectado, o null si no hay ningún estudiante en sesión. */
    public static Usuario usuarioActual;

    /** Profesor (administrador) conectado, o null si no hay ninguno en sesión. */
    public static Profesor profesorActual;

    /** Clase de utilidades: no se crean objetos de ella. */
    private Sesion() {
    }

    /**
     * Inicia la sesión de un usuario. Si había un profesor conectado, deja de estarlo.
     *
     * @param usuario usuario que ha introducido unas credenciales correctas
     */
    public static void iniciar(Usuario usuario) {
        profesorActual = null;
        usuarioActual = usuario;
    }

    /**
     * Inicia la sesión de un profesor. Si había un usuario conectado, deja de estarlo.
     *
     * @param profesor profesor que ha introducido unas credenciales correctas
     */
    public static void iniciarProfesor(Profesor profesor) {
        usuarioActual = null;
        profesorActual = profesor;
    }

    /**
     * Cierra la sesión actual, sea de usuario o de profesor, y lo registra en el log.
     * Se registra antes de borrar los datos para que la línea lleve el email de
     * quien cierra la sesión.
     */
    public static void cerrar() {
        if (usuarioActual != null || profesorActual != null) {
            GestorLog.registrar(TipoOperacion.LOGOUT, "-", "Cierre de sesión");
        }
        usuarioActual = null;
        profesorActual = null;
    }

    /**
     * @return el usuario conectado, o null si no hay sesión de estudiante
     */
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * @return el profesor conectado, o null si no hay sesión de profesor
     */
    public static Profesor getProfesorActual() {
        return profesorActual;
    }

    /**
     * Identificador de quien está conectado, para el log: el email del usuario
     * o del profesor, o "anonimo" si nadie ha iniciado sesión (por ejemplo, en el registro).
     *
     * @return email de quien está conectado o "anonimo"
     */
    public static String getNombreUsuario() {
        if (usuarioActual != null) {
            return usuarioActual.getEmail();
        }
        if (profesorActual != null) {
            return profesorActual.getEmail();
        }
        return "anonimo";
    }
}
