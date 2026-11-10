package tonguely.Excepciones;

/**
 * Excepción personalizada para manejar errores de datos duplicados en la aplicación Tonguely.
 */
public class DatoDuplicadoException extends TonguelyException {
    /**
     * Constructor de la excepción personalizada DatoDuplicadoException.
     *
     * @param message El mensaje de error que describe la excepción.
     * @param cause La excepción que causó esta excepción.
     */
    public DatoDuplicadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
