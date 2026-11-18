package tonguely.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tonguely.DAO.CursoDAO;
import tonguely.Excepciones.TonguelyException;

/**
 * Pruebas de interfaz del catálogo de cursos que ve el invitado.
 */
public class TodosCursosControllerTest extends PruebaVista {

    /**
     * Carga el catálogo antes de cada test (sin sesión: es un invitado).
     *
     * @param stage ventana que crea TestFX
     * @throws Exception si falla el FXML
     */
    @Override
    public void start(Stage stage) throws Exception {
        mostrar(stage, "todosLosCursos");
    }

    /**
     * Hay una tarjeta por cada curso guardado en MongoDB.
     *
     * @throws TonguelyException si MongoDB no está arrancado
     */
    @Test
    public void muestraUnaTarjetaPorCurso() throws TonguelyException {
        int cursos = new CursoDAO().cargarCursos().size();
        assertEquals(cursos, lookup(".curso-card").queryAll().size());
    }

    /** Un invitado que pulsa "Entrar al curso" llega a la pantalla que le pide registrarse. */
    @Test
    public void entrarComoInvitadoPideRegistrarse() {
        clickOn(lookup(".boton-entrar").nth(0).queryButton());
        verifyThat("#registrarButton", hasText("Regístrate gratis"));
    }
}
