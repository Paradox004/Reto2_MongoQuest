package tonguely.Excepciones;

/**
 * Excepción personalizada para manejar errores de datos duplicados en la aplicación Tonguely.
 */
public class ErrorLectura extends TonguelyException {
    /**
     * Constructor de la excepción personalizada ErrorLectura.
     *
     * @param message El mensaje de error que describe la excepción.
     * @param cause La excepción que causó esta excepción.
     */
    public ErrorLectura(String message, Throwable cause) {
        super(message, cause);
    }
}
