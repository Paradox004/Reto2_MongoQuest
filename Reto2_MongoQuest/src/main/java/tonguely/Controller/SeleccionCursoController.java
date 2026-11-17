package tonguely.Controller;
import tonguely.Main.Sesion;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import org.bson.types.ObjectId;

import tonguely.Main.App;
import tonguely.modelo.Curso;
import tonguely.modelo.Inscripciones;
import tonguely.DAO.CursoDAO;
import tonguely.DAO.InscripcionesDAO;
import tonguely.Excepciones.TonguelyException;

/**
 * Controlador encargado de mostrar los cursos del estudiante.
 *
 * @author anartz
 */
public class SeleccionCursoController {

    /**
     * Panel donde se muestran los cursos.
     */
    @FXML
    private GridPane gridCursos;

    /**
     * DAO para gestionar las inscripciones.
     */
    private final InscripcionesDAO inscripcionesDAO
            = new InscripcionesDAO();

    /**
     * DAO para gestionar los cursos.
     */
    private final CursoDAO cursoDAO
            = new CursoDAO();

    /**
     * Identificador del estudiante.
     */
    private ObjectId idEstudiante1;

    /**
     * Carga los cursos en los que está inscrito el estudiante.
     *
     * @param idEstudiante identificador del estudiante.
     */

    public void cargarCursos() {

        this.idEstudiante1 = Sesion.getUsuarioActual().getId();

        gridCursos.getChildren().clear();
        gridCursos.getColumnConstraints().clear();
        gridCursos.setHgap(20);   // separación entre tarjetas
        gridCursos.setVgap(20);
        // Si MongoDB falla, el DAO ya lo ha registrado en el log: aquí solo se avisa
        List<Inscripciones> inscripciones;
        try {
            inscripciones = inscripcionesDAO.buscarPorEstudiante(idEstudiante1);
        } catch (TonguelyException e) {
            Alertas.error("No se han podido cargar los cursos", e.getMessage());
            return;
        }

        int columna = 0;
        int fila = 0;

        for (Inscripciones inscripcion : inscripciones) {

            // Se asigna una sola vez, así sigue siendo "efectivamente final"
            // y se puede usar dentro de la lambda del botón
            Curso curso;
            try {
                curso = cursoDAO.buscarPorId(inscripcion.getIdCurso());
            } catch (TonguelyException e) {
                Alertas.error("No se han podido cargar los cursos", e.getMessage());
                return;
            }

            if (curso == null) {
                continue;
            }
            // TARJETA PRINCIPAL
        HBox tarjeta = new HBox(15);
        tarjeta.getStyleClass().add("curso-card");
        tarjeta.setAlignment(Pos.CENTER_LEFT);

        // CONTENEDOR DE TEXTOS
        VBox informacion = new VBox(12);
        informacion.getStyleClass().add("curso-info");

        // Nombre
        Label nombre = new Label(curso.getNombre());
        nombre.getStyleClass().add("curso-nombre");

        // Estado
        Label estado = new Label("Inscrito");
        estado.getStyleClass().add("curso-estado");

        // Descripción desde MongoDB
        String textoDescripcion = curso.getDescripcion();

        if (textoDescripcion == null
                || textoDescripcion.trim().isEmpty()) {
            textoDescripcion = "Este curso todavía no tiene descripción.";
        }

        Label descripcion = new Label(textoDescripcion);
        descripcion.getStyleClass().add("curso-descripcion");
        descripcion.setMaxWidth(230);   // antes 300, deja más sitio a la imagen
        descripcion.setWrapText(true);

        // Botón
        Button entrar = new Button("Entrar al curso");

        entrar.getStyleClass().add("boton-entrar");

        entrar.setOnAction(e -> {

            try {

                FXMLLoader loader = new FXMLLoader(
                        App.class.getResource(
                                "/view/actividades.fxml"
                        )
                );

                Parent root = loader.load();

                ActividadesController controller
                        = loader.getController();

                controller.setCurso(curso, idEstudiante1);

                App.scene.setRoot(root);

            } catch (IOException ex) {

                ex.printStackTrace();

            }
        });

        // Añadimos los textos
        informacion.getChildren().addAll(
                nombre,
                estado,
                descripcion,
                entrar
        );
        informacion.setPrefWidth(240);
        informacion.setMinWidth(240);

        // IMAGEN DEL CURSO
        ImageView imagen = new ImageView(cargarImagen(curso.getFoto()));
        imagen.setFitWidth(210);
        imagen.setFitHeight(170);
        imagen.setPreserveRatio(true);
        imagen.setSmooth(true);

        // Esquinas redondeadas
        Rectangle clip = new Rectangle(210, 170);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        imagen.setClip(clip);

        tarjeta.getChildren().setAll(informacion, imagen);
        GridPane.setHgrow(tarjeta, Priority.NEVER);
        GridPane.setFillWidth(tarjeta, false);
        gridCursos.add(tarjeta, columna, fila);

        if (++columna == 2) {
            columna = 0;
            fila++;
        }

        }
    }

    /**
     * Carga la imagen del curso a partir de su ruta.
     *
     * @param ruta ruta de la imagen.
     * @return imagen cargada o null si no se encuentra.
     */
    private Image cargarImagen(String ruta) {
        String r = (ruta == null || ruta.isBlank()) ? "/img/curso-default.png"
                : (ruta.startsWith("/") ? ruta : "/" + ruta);

        URL url = getClass().getResource(r);
        if (url == null) {
            url = getClass().getResource("/img/curso-default.png");
        }
        if (url == null) {
            return null;
        }

        Image img = new Image(url.toExternalForm());
        return img.isError() ? null : img;
    }

}