package tonguely.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

import org.bson.types.ObjectId;

import tonguely.Main.App;
import tonguely.modelo.Curso;
import tonguely.modelo.Inscripciones;
import tonguely.DAO.CursoDAO;
import tonguely.DAO.InscripcionesDAO;

public class SeleccionCursoController {

    @FXML
    private GridPane gridCursos;

    private final InscripcionesDAO inscripcionesDAO
            = new InscripcionesDAO();

    private final CursoDAO cursoDAO
            = new CursoDAO();

    private ObjectId idEstudiante1;

    public void cargarCursos(ObjectId idEstudiante) {

        this.idEstudiante1 = idEstudiante;

        gridCursos.getChildren().clear();

        List<Inscripciones> inscripciones
                = inscripcionesDAO.buscarPorEstudiante(idEstudiante);

        int columna = 0;
        int fila = 0;

        for (Inscripciones inscripcion : inscripciones) {

            Curso curso = cursoDAO.buscarPorId(
                    inscripcion.getIdCurso()
            );

            if (curso == null) {
                continue;
            }

            // TARJETA PRINCIPAL
            HBox tarjeta = new HBox(20);
            tarjeta.getStyleClass().add("curso-card");

            tarjeta.setPrefSize(520, 210);
            tarjeta.setMinSize(520, 210);

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
            descripcion.setMaxWidth(300);

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

            HBox.setHgrow(informacion, Priority.ALWAYS);

            // IMAGEN DEL CURSO
            ImageView imagen = new ImageView();

            imagen.setFitWidth(170);
            imagen.setFitHeight(145);
            imagen.setPreserveRatio(true);
            imagen.setSmooth(true);

            // Reservar siempre el espacio de la imagen
            StackPane contenedorImagen = new StackPane();

            contenedorImagen.setMinSize(170, 145);
            contenedorImagen.setPrefSize(170, 145);
            contenedorImagen.setMaxSize(170, 145);

            // Cargar fotografía
            String rutaFoto = curso.getFoto();

            System.out.println("Ruta de imagen MongoDB: " + rutaFoto);

            Image foto = cargarImagen(rutaFoto);

            imagen.setImage(foto);

            contenedorImagen.getChildren().add(imagen);

            // Añadir imagen a la tarjeta
            HBox.setHgrow(contenedorImagen, Priority.NEVER);

            // Colocar textos e imagen
            tarjeta.getChildren().clear();
            tarjeta.getChildren().addAll(informacion, contenedorImagen);

            // Añadir al GridPane
            gridCursos.add(tarjeta, columna, fila);

            columna++;

            if (columna == 2) {
                columna = 0;
                fila++;
            }
        }
    }

    // MÉTODO PARA CARGAR LAS IMÁGENES
    private Image cargarImagen(String ruta) {

        // Si no hay ruta, utilizar imagen predeterminada
        if (ruta == null || ruta.isBlank()) {
            ruta = "/img/curso-default.png";
        }

        // Normalizar ruta
        if (!ruta.startsWith("/")) {
            ruta = "/" + ruta;
        }

        System.out.println("Buscando imagen: " + ruta);

        URL recurso = getClass().getResource(ruta);

        // Si no encuentra la imagen, utilizar la predeterminada
        if (recurso == null) {

            System.out.println("No se encuentra: " + ruta);

            recurso = getClass().getResource(
                    "/img/curso-default.png"
            );
        }

        // Comprobar que existe la imagen predeterminada
        if (recurso == null) {
            System.out.println("Tampoco se encuentra la imagen predeterminada");
            return null;
        }

        Image imagen = new Image(
                recurso.toExternalForm(),
                false
        );

        if (imagen.isError()) {
            System.out.println(
                    "Error cargando imagen: "
                    + imagen.getException()
            );

            return null;
        }

        System.out.println("Imagen cargada: " + recurso);

        return imagen;
    }

}
