package tonguely.Excepciones;

/**
 * Excepción personalizada para manejar errores en la aplicación Tonguely.
 */
public class TonguelyException extends Exception {
    /**
     * Constructor de la excepción personalizada TonguelyException.
     *
     * @param message El mensaje de error que describe la excepción.
     * @param cause La causa raíz de la excepción.  
     */
    public TonguelyException(String message, Throwable cause) {
        super(message, cause);
    }
}
