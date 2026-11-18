package tonguely.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tonguely.Log.EntradaLog;
import tonguely.Log.GestorLog;
import tonguely.Log.TipoOperacion;

/**
 * Pruebas de interfaz del visor del log (pestaña "Log" del administrador):
 * orden, filtros y botón "Actualizar".
 */
public class ViewLogControllerTest extends PruebaVista {

    /** Texto que llevan las entradas que escribe este test, para buscarlas. */
    private static final String MARCA = "viewlogtest";

    /** Tabla del log. */
    private TableView<EntradaLog> tabla;

    /** Se asegura de que el log tiene al menos una entrada correcta y otra de error. */
    @BeforeAll
    public static void escribirEntradas() {
        GestorLog.registrar(TipoOperacion.CONSULTAR, "Pruebas", "Entrada correcta de " + MARCA);
        GestorLog.registrarError(TipoOperacion.BORRAR, "Pruebas", "Entrada con error de " + MARCA);
    }

    /**
     * Carga el visor antes de cada test.
     *
     * @param stage ventana que crea TestFX
     * @throws Exception si falla el FXML
     */
    @Override
    public void start(Stage stage) throws Exception {
        mostrar(stage, "viewLog");
        tabla = lookup("#tVLog").queryTableView();
    }

    /** Se cargan las entradas con la más reciente arriba. */
    @Test
    public void cargaElLogConLaMasRecienteArriba() {
        assertFalse(tabla.getItems().isEmpty());
        for (int i = 0; i + 1 < tabla.getItems().size(); i++) {
            EntradaLog actual = tabla.getItems().get(i);
            EntradaLog siguiente = tabla.getItems().get(i + 1);
            assertFalse(actual.getFecha().isBefore(siguiente.getFecha()), "Fila " + i + " desordenada");
        }
    }

    /** El desplegable tiene "Todos" más un valor por cada tipo de operación. */
    @Test
    public void desplegableTieneTodosLosTipos() {
        ComboBox<String> tipos = lookup("#cbTipo").queryComboBox();
        assertEquals("Todos", tipos.getValue());
        assertEquals(TipoOperacion.values().length + 1, tipos.getItems().size());
    }

    /** "Solo errores" deja solo las filas con resultado ERROR. */
    @Test
    public void soloErroresFiltraLosErrores() {
        clickOn("#chBSoloError");
        assertFalse(tabla.getItems().isEmpty());
        for (EntradaLog e : tabla.getItems()) {
            assertEquals("ERROR", e.getResultado());
        }
    }

    /** Elegir un tipo en el desplegable deja solo las filas de ese tipo. */
    @Test
    public void filtroPorTipo() {
        ComboBox<String> tipos = lookup("#cbTipo").queryComboBox();
        interact(() -> tipos.setValue(TipoOperacion.BORRAR.name()));
        assertFalse(tabla.getItems().isEmpty());
        for (EntradaLog e : tabla.getItems()) {
            assertEquals("BORRAR", e.getTipo());
        }
    }

    /** El buscador filtra por el usuario o el detalle, sin distinguir mayúsculas, y se combina con "Solo errores". */
    @Test
    public void buscadorYSoloErroresSeCombinan() {
        clickOn("#tFBuscar").write(MARCA.toUpperCase());
        assertTrue(tabla.getItems().size() >= 2);
        for (EntradaLog e : tabla.getItems()) {
            assertTrue(e.getDetalle().toLowerCase().contains(MARCA)
                    || e.getUsuario().toLowerCase().contains(MARCA));
        }

        clickOn("#chBSoloError");
        assertFalse(tabla.getItems().isEmpty());
        for (EntradaLog e : tabla.getItems()) {
            assertEquals("ERROR", e.getResultado());
            assertTrue(e.getDetalle().toLowerCase().contains(MARCA));
        }
    }

    /** "Actualizar" vuelve a leer el fichero: aparece una entrada escrita después de abrir el visor. */
    @Test
    public void actualizarMuestraEntradasNuevas() {
        String detalle = "Entrada nueva de " + MARCA + " " + System.nanoTime();
        GestorLog.registrar(TipoOperacion.CONSULTAR, "Pruebas", detalle);
        assertFalse(contieneDetalle(detalle), "Todavía no se ha pulsado Actualizar");

        clickOn("#bActualizar");
        assertTrue(contieneDetalle(detalle));
    }

    /**
     * @param detalle detalle buscado
     * @return true si alguna fila de la tabla tiene ese detalle
     */
    private boolean contieneDetalle(String detalle) {
        for (EntradaLog e : tabla.getItems()) {
            if (detalle.equals(e.getDetalle())) {
                return true;
            }
        }
        return false;
    }
}
