package tonguely.Controller;

import java.util.ArrayList;
import tonguely.DAO.UsuarioDAO;
import tonguely.Excepciones.DatoDuplicadoException;
import tonguely.Excepciones.TonguelyException;
import tonguely.modelo.Usuario;

/**
 * Prepara en MongoDB los datos que necesitan los tests de interfaz.
 *
 * Así los tests funcionan en el ordenador de cualquiera del equipo,
 * aunque su base de datos local esté vacía.
 */
final class DatosPrueba {

    /** Email del usuario de prueba. */
    static final String EMAIL = "ane@gmail.com";

    /** Contraseña del usuario de prueba. */
    static final String CONTRASENA = "1234";

    /** Clase de utilidades: no se crean objetos de ella. */
    private DatosPrueba() {
    }

    /**
     * Crea el usuario de prueba si todavía no existe. Si ya existe,
     * {@code crearUsuario} lanza {@link DatoDuplicadoException} y no se hace nada.
     *
     * @throws TonguelyException si MongoDB no está arrancado
     */
    static void asegurarUsuarioPrueba() throws TonguelyException {
        Usuario ane = new Usuario();
        ane.setNombre("Ane");
        ane.setEmail(EMAIL);
        ane.setContraseña(CONTRASENA);
        ane.setRacha(0);
        ane.setCursos(new ArrayList<>());
        try {
            new UsuarioDAO().crearUsuario(ane);
        } catch (DatoDuplicadoException yaExiste) {
            // Ya estaba creado de una ejecución anterior: es lo que queremos
        }
    }
}
