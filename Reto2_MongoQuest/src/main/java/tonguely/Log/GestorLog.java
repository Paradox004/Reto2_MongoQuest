package tonguely.Log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import tonguely.Excepciones.ErrorLectura;
import tonguely.Main.Sesion;

/**
 * Registra en un fichero todas las operaciones de acceso a datos usando
 * {@link java.util.logging.Logger}.
 *
 * Cada operación ocupa una línea con campos fijos separados por " | ":
 * <pre>
 * fecha y hora | usuario | tipo | colección | OK/ERROR | detalle
 * 2026-11-15 18:30:12 | ana@mail.com | LOGIN | Usuario | OK | Inicio de sesión correcto
 * </pre>
 * Así el visor del log puede separar cada línea con {@code split(" \\| ")}
 * y filtrar por tipo (pestaña de inicios de sesión) o por resultado
 * (pestaña de errores).
 *
 * Nunca se escribe la contraseña del usuario.
 */
public final class GestorLog {

    /** Ruta del fichero de log, relativa a la carpeta desde la que se ejecuta la aplicación. */
    private static final String RUTA = "logs/tonguely.log";

    /** Valor del campo "colección" cuando la operación es sobre el propio log y no sobre MongoDB. */
    private static final String COLECCION_LOG = "log";

    /** Separador entre campos de una línea. */
    private static final String SEPARADOR = " | ";

    /** Formato de la fecha y la hora de cada línea. */
    private static final DateTimeFormatter FORMATO_FECHA
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Logger de la aplicación. Es thread-safe, no hace falta synchronized. */
    private static final Logger LOGGER = Logger.getLogger("tonguely");

    /*
     * Bloque estático: se ejecuta una sola vez, la primera vez que se usa la clase.
     * Configura el Logger para que escriba en el fichero con nuestro formato.
     */
    static {
        try {
            // 1. Crear la carpeta logs/ si no existe (FileHandler no la crea)
            Files.createDirectories(Paths.get(RUTA).getParent());

            // 2. FileHandler escribe en el fichero; "true" = añadir al final, no sobrescribir
            FileHandler fichero = new FileHandler(RUTA, true);
            fichero.setEncoding("UTF-8");
            fichero.setFormatter(new FormatoLinea());
            LOGGER.addHandler(fichero);

            // 3. No reenviar al logger padre, que lo sacaría también por consola
            LOGGER.setUseParentHandlers(false);

        } catch (IOException e) {
            // Si no se puede abrir el fichero, la aplicación sigue funcionando:
            // un fallo del log no debe impedir que el usuario trabaje, y el DAO
            // que lo llama no puede mostrar alertas.
            System.err.println("No se ha podido abrir el fichero de log: " + e.getMessage());
        }
    }

    /** Clase de utilidades: no se crean objetos de ella. */
    private GestorLog() {
    }

    /**
     * Registra una operación que ha terminado bien (nivel INFO).
     *
     * @param tipo tipo de operación realizada
     * @param coleccion colección de MongoDB sobre la que se ha trabajado
     * @param detalle información extra de la operación (nunca contraseñas)
     */
    public static void registrar(TipoOperacion tipo, String coleccion, String detalle) {
        LOGGER.log(Level.INFO, componer(tipo, coleccion, "OK", detalle));
    }

    /**
     * Registra una operación fallida que no viene de una excepción, por ejemplo
     * unas credenciales incorrectas o un email repetido (nivel WARNING).
     *
     * @param tipo tipo de operación que se intentaba hacer
     * @param coleccion colección de MongoDB afectada
     * @param motivo explicación del fallo
     */
    public static void registrarError(TipoOperacion tipo, String coleccion, String motivo) {
        LOGGER.log(Level.WARNING, componer(tipo, coleccion, "ERROR", motivo));
    }

    /**
     * Registra una operación fallida a causa de una excepción (nivel SEVERE).
     * Se guarda el nombre de la clase de la excepción y su mensaje.
     *
     * @param tipo tipo de operación que se intentaba hacer
     * @param coleccion colección de MongoDB afectada
     * @param error excepción que ha provocado el fallo
     */
    public static void registrarError(TipoOperacion tipo, String coleccion, Throwable error) {
        LOGGER.log(Level.SEVERE, componer(tipo, coleccion, "ERROR",
                error.getClass().getSimpleName() + ": " + error.getMessage()));
    }

    /**
     * Monta la parte de la línea que va después de la fecha,
     * con los campos siempre en el mismo orden.
     */
    private static String componer(TipoOperacion tipo, String coleccion,
            String resultado, String detalle) {
        return limpiar(Sesion.getNombreUsuario())
                + SEPARADOR + tipo
                + SEPARADOR + limpiar(coleccion)
                + SEPARADOR + resultado
                + SEPARADOR + limpiar(detalle);
    }

    /**
     * Evita que un texto rompa el formato de la línea: quita los saltos de
     * línea y el carácter separador. Si el texto es nulo devuelve "-".
     */
    private static String limpiar(String texto) {
        if (texto == null || texto.isBlank()) {
            return "-";
        }
        return texto.replace("|", "/").replace("\r", " ").replace("\n", " ");
    }

    /**
     * Formato de cada línea del fichero. Por defecto FileHandler escribe en XML;
     * con este Formatter se escribe una sola línea de texto: la fecha del
     * registro seguida del mensaje.
     */
    private static class FormatoLinea extends Formatter {

        @Override
        public String format(LogRecord registro) {
            String fecha = LocalDateTime
                    .ofInstant(registro.getInstant(), ZoneId.systemDefault())
                    .format(FORMATO_FECHA);
            return fecha + SEPARADOR + registro.getMessage() + System.lineSeparator();
        }
    }

    /**
     * Lee el fichero de log y devuelve sus líneas convertidas en {@link EntradaLog}.
     *
     * Las líneas mal formadas se saltan y, al terminar, se registra un único
     * aviso con cuántas se han ignorado. La propia consulta también queda
     * registrada en el log.
     *
     * @return las entradas del log; lista vacía si el fichero todavía no existe
     * @throws ErrorLectura si el fichero existe pero no se puede leer
     */
    public static List<EntradaLog> leer() throws ErrorLectura{
        // Si todavía no se ha escrito nada, no es un error: el log está vacío
        if (!Files.exists(Paths.get(RUTA))) {
            return new ArrayList<>();
        }

        try {
            List<String> lineas = Files.readAllLines(Paths.get(RUTA), StandardCharsets.UTF_8);
            List<EntradaLog> entradas = lineas.stream()
                        .map(linea -> parsear(linea))
                        .filter(entrada -> entrada != null)
                        .collect(Collectors.toList());

            // Las líneas que faltan en "entradas" son las que parsear() ha descartado
            int ignoradas = lineas.size() - entradas.size();
            if (ignoradas > 0) {
                registrarError(TipoOperacion.CONSULTAR, COLECCION_LOG,
                        "Lectura del log: " + ignoradas + " líneas ignoradas por formato incorrecto");
            }
            registrar(TipoOperacion.CONSULTAR, COLECCION_LOG,
                    "Lectura del log: " + entradas.size() + " entradas");

            return entradas;
        } catch (IOException e) {
            throw new ErrorLectura("Ha surgido un error con la lectura del archivo.",e);
        }
    }
    private static EntradaLog parsear(String linea){
        if(linea != null){
            String[] lineaParseada = linea.split(" \\| ",6);
            if(lineaParseada.length == 6){
                try{
                    EntradaLog entrada = new EntradaLog(
                        LocalDateTime.parse(lineaParseada[0], FORMATO_FECHA),
                        lineaParseada[1], lineaParseada[2], lineaParseada[3],
                        lineaParseada[4], lineaParseada[5]);
                    return entrada;
                }catch(DateTimeParseException e){
                    return null;
                }
            }
            else{
                return null;
            }
        }else{
            return null;
        }
    }
}
