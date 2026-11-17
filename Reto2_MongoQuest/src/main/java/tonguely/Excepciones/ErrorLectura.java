package tonguely.Excepciones;

/**
 * Se lanza cuando el fichero de log existe pero no se puede leer
 * (sin permisos, bloqueado por otro programa o con una codificación distinta de UTF-8).
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
