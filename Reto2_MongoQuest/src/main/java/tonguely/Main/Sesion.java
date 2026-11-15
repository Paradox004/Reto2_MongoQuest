package tonguely.Main;

import tonguely.modelo.Usuario;

/**
 * Guarda el usuario que ha iniciado sesión mientras la aplicación está abierta.
 *
 * Es estática porque solo hay una sesión a la vez en una aplicación de escritorio,
 * y así cualquier clase (por ejemplo {@link tonguely.Log.GestorLog}) puede saber
 * quién está conectado sin tener que pasar el usuario de pantalla en pantalla.
 */
public final class Sesion {

    /** Usuario conectado, o null si nadie ha iniciado sesión. */
    private static Usuario usuarioActual;

    /** Clase de utilidades: no se crean objetos de ella. */
    private Sesion() {
    }

    /**
     * Inicia la sesión de un usuario.
     *
     * @param usuario usuario que ha introducido unas credenciales correctas
     */
    public static void iniciar(Usuario usuario) {
        usuarioActual = usuario;
    }

    /** Cierra la sesión actual. */
    public static void cerrar() {
        usuarioActual = null;
    }

    /**
     * @return el usuario conectado, o null si no hay sesión
     */
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Identificador del usuario para el log: su email, o "anonimo"
     * si todavía no ha iniciado sesión (por ejemplo, en el registro).
     *
     * @return email del usuario conectado o "anonimo"
     */
    public static String getNombreUsuario() {
        if (usuarioActual == null) {
            return "anonimo";
        }
        return usuarioActual.getEmail();
    }
}
