package tonguely.Log;

/**
 * Tipos de operación que se registran en el fichero de log.
 *
 * Se usa un enum en lugar de un String para que no se puedan escribir
 * tipos con faltas ("INSERTAR", "Insertar", "INSERT"...) y el visor del log
 * pueda filtrar siempre por los mismos valores.
 */
public enum TipoOperacion {
    /** Intento de inicio de sesión (correcto o fallido). */
    LOGIN,
    /** Cierre de sesión. */
    LOGOUT,
    /** Lectura de datos de una colección. */
    CONSULTAR,
    /** Alta de un documento nuevo. */
    INSERTAR,
    /** Modificación de un documento existente. */
    ACTUALIZAR,
    /** Borrado de un documento. */
    BORRAR
}
