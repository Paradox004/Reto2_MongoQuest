package tonguely.Excepciones;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Centraliza los cuadros de diálogo ({@link Alert}) que muestran los controladores.
 *
 * En lugar de repetir en cada controlador las cinco líneas de crear, configurar
 * y mostrar un Alert, se llama a uno de estos métodos con los textos como
 * parámetros. Así todos los avisos de la aplicación tienen el mismo aspecto y,
 * si hay que cambiar algo (un estilo, un icono...), se cambia en un solo sitio.
 *
 * Está en el paquete de los controladores porque pertenece a la capa de
 * interfaz: los DAO nunca deben mostrar alertas.
 */
public final class Alertas {

    /** Clase de utilidades: no se crean objetos de ella. */
    private Alertas() {
    }

    /**
     * Muestra un Alert y espera a que el usuario lo cierre.
     * Si el título o la cabecera son null, se deja el valor por defecto de JavaFX.
     *
     * @param tipo tipo de aviso (ERROR, WARNING, INFORMATION, CONFIRMATION)
     * @param titulo texto de la barra de la ventana
     * @param cabecera texto destacado encima del mensaje
     * @param mensaje texto principal del aviso
     */
    public static void mostrar(AlertType tipo, String titulo, String cabecera, String mensaje) {
        Alert alerta = new Alert(tipo);
        if (titulo != null) {
            alerta.setTitle(titulo);
        }
        if (cabecera != null) {
            alerta.setHeaderText(cabecera);
        }
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Muestra un aviso de error con el título "Error".
     *
     * @param cabecera qué operación ha fallado
     * @param mensaje explicación del error (normalmente {@code e.getMessage()})
     */
    public static void error(String cabecera, String mensaje) {
        mostrar(AlertType.ERROR, "Error", cabecera, mensaje);
    }

    /**
     * Muestra un aviso informativo.
     *
     * @param titulo texto de la barra de la ventana
     * @param cabecera texto destacado encima del mensaje
     * @param mensaje texto principal del aviso
     */
    public static void informacion(String titulo, String cabecera, String mensaje) {
        mostrar(AlertType.INFORMATION, titulo, cabecera, mensaje);
    }

    /**
     * Muestra una advertencia, por ejemplo cuando falta un dato por rellenar.
     *
     * @param mensaje texto del aviso
     */
    public static void advertencia(String mensaje) {
        mostrar(AlertType.WARNING, null, null, mensaje);
    }
}
