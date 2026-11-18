package tonguely.Main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tonguely.modelo.Profesor;
import tonguely.modelo.Usuario;

/**
 * Pruebas unitarias (JUnit) de {@link Sesion}: quién aparece en el log
 * según quién haya iniciado sesión. No necesitan MongoDB ni interfaz.
 */
public class SesionTest {

    /**
     * Deja la sesión vacía antes y después de cada test para que no se afecten
     * entre sí (ni con otros tests de la misma ejecución).
     */
    @BeforeEach
    @AfterEach
    public void limpiar() {
        Sesion.cerrar();
    }

    /** Crea un usuario solo con email, que es lo que usa Sesion. */
    private static Usuario usuario(String email) {
        Usuario u = new Usuario();
        u.setEmail(email);
        return u;
    }

    /** Crea un profesor solo con email. */
    private static Profesor profesor(String email) {
        Profesor p = new Profesor();
        p.setEmail(email);
        return p;
    }

    /** Sin sesión, el log debe poner "anonimo". */
    @Test
    public void sinSesionEsAnonimo() {
        assertEquals("anonimo", Sesion.getNombreUsuario());
    }

    /** Con un usuario conectado, el log lleva su email. */
    @Test
    public void conUsuarioDevuelveSuEmail() {
        Sesion.iniciar(usuario("ane@gmail.com"));
        assertEquals("ane@gmail.com", Sesion.getNombreUsuario());
    }

    /** Con un profesor conectado, el log lleva su email (antes salía "anonimo"). */
    @Test
    public void conProfesorDevuelveSuEmail() {
        Sesion.iniciarProfesor(profesor("profe@tonguely.com"));
        assertEquals("profe@tonguely.com", Sesion.getNombreUsuario());
    }

    /** Iniciar sesión de profesor cierra la de usuario: nunca hay dos a la vez. */
    @Test
    public void profesorYUsuarioSeExcluyen() {
        Sesion.iniciar(usuario("ane@gmail.com"));
        Sesion.iniciarProfesor(profesor("profe@tonguely.com"));
        assertNull(Sesion.getUsuarioActual());
        assertEquals("profe@tonguely.com", Sesion.getNombreUsuario());
    }

    /** Cerrar sesión deja todo vacío y el log vuelve a "anonimo". */
    @Test
    public void cerrarVaciaLaSesion() {
        Sesion.iniciar(usuario("ane@gmail.com"));
        Sesion.cerrar();
        assertNull(Sesion.getUsuarioActual());
        assertNull(Sesion.getProfesorActual());
        assertEquals("anonimo", Sesion.getNombreUsuario());
    }
}
