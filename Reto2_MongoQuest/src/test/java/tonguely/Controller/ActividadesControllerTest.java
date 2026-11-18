package tonguely.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tonguely.DAO.ActividadesDAO;
import tonguely.DAO.CursoDAO;
import tonguely.Excepciones.TonguelyException;
import tonguely.modelo.Actividades;
import tonguely.modelo.Curso;
import tonguely.modelo.Usuario;

/**
 * Pruebas de interfaz del caso de uso "Hacer las actividades de un curso":
 * preguntas tipo test, preguntas escritas, aciertos, fallos y final del curso.
 */
public class ActividadesControllerTest extends PruebaVista {

    /** Curso de prueba: su primera actividad es tipo test y la segunda es escrita. */
    private static Curso curso;

    /** Actividades del curso, en el mismo orden en que las muestra el controlador. */
    private static List<Actividades> actividades;

    /**
     * Busca en MongoDB un curso cuya 1ª actividad sea tipo test y la 2ª escrita
     * (en los datos de prueba, "Inglés A1").
     *
     * @throws TonguelyException si MongoDB no está arrancado
     */
    @BeforeAll
    public static void buscarCurso() throws TonguelyException {
        ActividadesDAO dao = new ActividadesDAO();
        for (Curso c : new CursoDAO().cargarCursos()) {
            List<Actividades> lista = dao.actividadesCurso(c.getId());
            if (lista.size() >= 2 && "test".equalsIgnoreCase(lista.get(0).getTipo())
                    && "pregunta".equalsIgnoreCase(lista.get(1).getTipo())) {
                curso = c;
                actividades = lista;
                return;
            }
        }
        assertNotNull(curso, "No hay un curso con una actividad test y otra escrita: carga bd/datos_prueba.js");
    }

    /**
     * Inicia la sesión y abre las actividades del curso igual que el botón "Entrar al curso".
     *
     * @param stage ventana que crea TestFX
     * @throws Exception si MongoDB no está arrancado o falla el FXML
     */
    @Override
    public void start(Stage stage) throws Exception {
        Usuario estudiante = DatosPrueba.iniciarEstudiante();
        ActividadesController controlador = mostrar(stage, "actividades").getController();
        controlador.setCurso(curso, estudiante.getId());
    }

    /** Se ve la primera pregunta y el progreso "Pregunta 1 de N". */
    @Test
    public void muestraLaPrimeraPregunta() {
        assertEquals("Pregunta 1 de " + actividades.size(), progreso());
        assertEquals(actividades.get(0).getPregunta(),
                lookup(".question-label").queryAs(Label.class).getText());
    }

    /** Error del usuario: pulsar "Comprobar" sin elegir ninguna opción avisa y no avanza. */
    @Test
    public void comprobarSinElegirOpcionAvisa() {
        clickOn(boton("Comprobar"));
        assertEquals("Selecciona una respuesta.", cerrarDialogo());
        assertEquals("Pregunta 1 de " + actividades.size(), progreso());
    }

    /** Una opción incorrecta muestra "Respuesta incorrecta" y no avanza. */
    @Test
    public void opcionIncorrectaNoAvanza() {
        Actividades primera = actividades.get(0);
        for (String opcion : primera.getOpciones()) {
            if (!opcion.equalsIgnoreCase(primera.getRespuesta_correcta())) {
                clickOn(opcion(opcion));
                break;
            }
        }
        clickOn(boton("Comprobar"));
        assertEquals("Respuesta incorrecta", cerrarDialogo());
        assertEquals("Pregunta 1 de " + actividades.size(), progreso());
    }

    /** La opción correcta felicita al estudiante y pasa a la pregunta 2. */
    @Test
    public void opcionCorrectaPasaALaSiguiente() {
        responderBien(actividades.get(0));
        assertEquals("Pregunta 2 de " + actividades.size(), progreso());
    }

    /**
     * Pregunta escrita: una respuesta incorrecta no avanza y la correcta sí,
     * aunque esté escrita en mayúsculas.
     */
    @Test
    public void preguntaEscritaCompruebaLaRespuesta() {
        responderBien(actividades.get(0));
        Actividades escrita = actividades.get(1);

        clickOn(".answer-field").write("respuesta mal");
        clickOn(boton("validar"));
        assertEquals("Respuesta incorrecta", cerrarDialogo());
        assertEquals("Pregunta 2 de " + actividades.size(), progreso());

        clickOn(".answer-field").eraseText("respuesta mal".length())
                .write(escrita.getRespuesta_correcta().toUpperCase());
        clickOn(boton("validar"));
        assertEquals("¡Respuesta correcta!", cerrarDialogo());
    }

    /** Al acertar todas se ve el mensaje final y "Volver a los cursos" lleva a "Mis cursos". */
    @Test
    public void terminarYVolverALosCursos() {
        for (Actividades a : actividades) {
            responderBien(a);
        }
        verifyThat(".finish-label", isVisible());
        clickOn(boton("Volver a los cursos"));
        verifyThat("#gridCursos", isVisible());
    }

    /**
     * Responde bien a una actividad según su tipo y cierra el aviso de acierto.
     *
     * @param a actividad que se está mostrando
     */
    private void responderBien(Actividades a) {
        if ("test".equalsIgnoreCase(a.getTipo())) {
            clickOn(opcion(a.getRespuesta_correcta()));
            clickOn(boton("Comprobar"));
        } else {
            clickOn(".answer-field").write(a.getRespuesta_correcta());
            clickOn(boton("validar"));
        }
        assertEquals("¡Respuesta correcta!", cerrarDialogo());
    }

    /**
     * Busca la opción de la pregunta tipo test que tiene ese texto.
     *
     * @param texto texto de la opción
     * @return el RadioButton de la opción
     */
    private RadioButton opcion(String texto) {
        return lookup((Node n) -> n instanceof RadioButton && texto.equals(((RadioButton) n).getText()))
                .queryAs(RadioButton.class);
    }

    /** @return el texto de la etiqueta de progreso */
    private String progreso() {
        return lookup("#labelProgreso").queryAs(Label.class).getText();
    }
}
