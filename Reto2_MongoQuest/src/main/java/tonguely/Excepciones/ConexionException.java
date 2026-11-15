package tonguely.Excepciones;

/**
 * Excepción personalizada para manejar errores de conexión con la base de datos de MongoDB.
 */
public class ConexionException extends TonguelyException {
    /**
     * Constructor de la excepción personalizada ConexionException.
     *
     * @param message El mensaje de error que describe la excepción.
     * @param cause La excepción que causó esta excepción.
     */
    public ConexionException(String message, Throwable cause) {
        super(message, cause);
    }
}
