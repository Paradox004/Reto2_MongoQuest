package tonguely.Log;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tonguely.Main.Sesion;
import tonguely.modelo.Usuario;

/**
 * Pruebas unitarias (JUnit) de {@link GestorLog}: lo que se escribe con
 * {@code registrar} debe poder leerse con {@code leer} separado en sus campos.
 *
 * Escriben en el fichero real {@code logs/tonguely.log}. Cada test usa un
 * texto único para encontrar su propia línea entre las que ya hubiera.
 * No necesitan MongoDB.
 */
public class GestorLogTest {

    /**
     * Deja la sesión vacía antes y después de cada test. Todos los tests corren en
     * la misma JVM y otro test (por ejemplo el de login) puede haberla dejado abierta.
     */
    @BeforeEach
    @AfterEach
    public void limpiar() {
        Sesion.cerrar();
    }

    /** Texto distinto en cada ejecución para localizar la línea escrita por el test. */
    private static String marcaUnica() {
        return "test-" + System.nanoTime();
    }

    /**
     * Busca en el log la entrada cuyo detalle contiene la marca.
     * Falla el test si no la encuentra.
     */
    private static EntradaLog buscar(String marca) throws Exception {
        List<EntradaLog> entradas = GestorLog.leer();
        for (EntradaLog e : entradas) {
            if (e.getDetalle().contains(marca)) {
                return e;
            }
        }
        throw new AssertionError("No se ha encontrado en el log la línea con " + marca);
    }

    /** Una operación correcta se lee con todos sus campos y resultado OK. */
    @Test
    public void registrarYLeerOperacionCorrecta() throws Exception {
        String marca = marcaUnica();
        GestorLog.registrar(TipoOperacion.INSERTAR, "Pruebas", marca);

        EntradaLog e = buscar(marca);
        assertNotNull(e.getFecha());
        assertEquals("anonimo", e.getUsuario());
        assertEquals("INSERTAR", e.getTipo());
        assertEquals("Pruebas", e.getColeccion());
        assertEquals("OK", e.getResultado());
        assertEquals(marca, e.getDetalle());
    }

    /** La línea lleva el email de quien tiene la sesión iniciada. */
    @Test
    public void laLineaLlevaElUsuarioDeLaSesion() throws Exception {
        Usuario u = new Usuario();
        u.setEmail("ane@gmail.com");
        Sesion.iniciar(u);

        String marca = marcaUnica();
        GestorLog.registrar(TipoOperacion.CONSULTAR, "Pruebas", marca);

        assertEquals("ane@gmail.com", buscar(marca).getUsuario());
    }

    /** Un error con excepción guarda ERROR, el nombre de la clase y su mensaje. */
    @Test
    public void registrarErrorConExcepcion() throws Exception {
        String marca = marcaUnica();
        GestorLog.registrarError(TipoOperacion.BORRAR, "Pruebas", new IllegalStateException(marca));

        EntradaLog e = buscar(marca);
        assertEquals("ERROR", e.getResultado());
        assertEquals("BORRAR", e.getTipo());
        assertEquals("IllegalStateException: " + marca, e.getDetalle());
    }

    /**
     * Un detalle con el separador "|" o con saltos de línea no debe romper
     * el formato: se sigue leyendo como una sola entrada de 6 campos.
     */
    @Test
    public void elSeparadorEnElDetalleNoRompeLaLinea() throws Exception {
        String marca = marcaUnica();
        GestorLog.registrar(TipoOperacion.CONSULTAR, "Pruebas", marca + " | con barra\ny salto");

        EntradaLog e = buscar(marca);
        assertFalse(e.getDetalle().contains("|"), "El detalle no debe contener el separador");
        assertFalse(e.getDetalle().contains("\n"), "El detalle no debe contener saltos de línea");
        assertTrue(e.getDetalle().contains("con barra"));
        assertEquals("OK", e.getResultado());
    }
}
