package tonguely.Controller;

import java.util.ArrayList;
import tonguely.DAO.ProfesorDAO;
import tonguely.DAO.UsuarioDAO;
import tonguely.Excepciones.DatoDuplicadoException;
import tonguely.Excepciones.TonguelyException;
import tonguely.Main.Sesion;
import tonguely.modelo.Profesor;
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

    /** Email del profesor de prueba (lo crea {@code bd/datos_prueba.js}). */
    static final String EMAIL_PROFESOR = "laura@tonguely.com";

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

    /**
     * Inicia la sesión del estudiante de prueba igual que la pantalla de login:
     * {@code UsuarioDAO.comprobarLogin} rellena {@link Sesion}.
     *
     * @return el estudiante conectado
     * @throws TonguelyException si MongoDB no está arrancado
     */
    static Usuario iniciarEstudiante() throws TonguelyException {
        asegurarUsuarioPrueba();
        Usuario usuario = new UsuarioDAO().comprobarLogin(EMAIL, CONTRASENA);
        if (usuario == null) {
            throw new IllegalStateException("No se puede iniciar sesión con " + EMAIL);
        }
        return usuario;
    }

    /**
     * Inicia la sesión del profesor de prueba (administrador).
     *
     * @return el profesor conectado
     * @throws TonguelyException si MongoDB no está arrancado
     */
    static Profesor iniciarProfesor() throws TonguelyException {
        Profesor profesor = new ProfesorDAO().comprobarLogin(EMAIL_PROFESOR, CONTRASENA);
        if (profesor == null) {
            throw new IllegalStateException("No existe el profesor " + EMAIL_PROFESOR
                    + ": carga los datos con  mongosh TonguelyDB bd/datos_prueba.js");
        }
        return profesor;
    }

    /**
     * Vacía la sesión sin escribir un LOGOUT en el log
     * ({@code Sesion.cerrar()} sí lo escribiría).
     */
    static void vaciarSesion() {
        Sesion.usuarioActual = null;
        Sesion.profesorActual = null;
    }
}
