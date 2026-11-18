package tonguely.Excepciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias (JUnit) de las excepciones propias: herencia, mensaje
 * para el usuario y encadenamiento de la causa original.
 */
public class ExcepcionesTest {

    /** Todas las excepciones propias se pueden capturar con un solo catch de TonguelyException. */
    @Test
    public void todasHeredanDeTonguelyException() {
        assertTrue(new ConexionException("x", null) instanceof TonguelyException);
        assertTrue(new DatoDuplicadoException("x", null) instanceof TonguelyException);
        assertTrue(new ErrorLectura("x", null) instanceof TonguelyException);
    }

    /** La causa original (la excepción de Mongo) se conserva con getCause(). */
    @Test
    public void conservanLaCausaOriginal() {
        RuntimeException deMongo = new RuntimeException("Timeout");
        ConexionException e = new ConexionException("No se ha podido conectar", deMongo);

        assertEquals("No se ha podido conectar", e.getMessage());
        assertSame(deMongo, e.getCause());
    }

    /** Si el error lo detecta nuestra lógica (no Mongo), la causa es null. */
    @Test
    public void datoDuplicadoSinCausa() {
        DatoDuplicadoException e = new DatoDuplicadoException("Ya existe", null);
        assertNull(e.getCause());
    }

    /**
     * Al lanzar una subclase, el catch de la superclase la recibe con su tipo real:
     * así el controlador puede tratar aparte el email repetido.
     */
    @Test
    public void seCapturanConLaSuperclase() {
        TonguelyException capturada = assertThrows(TonguelyException.class, () -> {
            throw new DatoDuplicadoException("Ya existe un usuario con el email ane@gmail.com", null);
        });
        assertTrue(capturada instanceof DatoDuplicadoException);
    }
}
