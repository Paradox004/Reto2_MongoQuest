package tonguely.Controller;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import tonguely.Main.App;

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
        aplicarEstilo(alerta);
        alerta.showAndWait();
    }

    /**
     * Un Alert se abre en su propia ventana, que no hereda la hoja de estilos
     * de la escena principal: hay que añadírsela a su DialogPane.
     */
    private static void aplicarEstilo(Alert alerta) {
        alerta.getDialogPane().getStylesheets().add(App.HOJA_ESTILOS);
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

    /**
     * Pide confirmación al usuario antes de una acción que no se puede deshacer,
     * como un borrado. Muestra los botones "Sí" y "No".
     *
     * @param mensaje pregunta que se le hace al usuario
     * @return true si el usuario ha pulsado "Sí"
     */
    public static boolean confirmar(String mensaje) {
        Alert alerta = new Alert(AlertType.CONFIRMATION, mensaje, ButtonType.YES, ButtonType.NO);
        alerta.setHeaderText(null);
        aplicarEstilo(alerta);
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.YES;
    }
}
