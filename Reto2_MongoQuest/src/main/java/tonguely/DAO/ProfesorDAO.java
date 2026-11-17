package tonguely.DAO;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import tonguely.modelo.Profesor;
import static com.mongodb.client.model.Filters.eq;

public class ProfesorDAO {

    private final MongoCollection<Profesor> coleccion;

    public ProfesorDAO() {
        MongoDatabase bd = ConexionMongo.conectar();
        coleccion = bd.getCollection("Profesores", Profesor.class);
    }
        public Profesor comprobarLogin(String email, String password) {
        Profesor profesor = coleccion.find(eq("email", email)).first();

        if (profesor != null
                && profesor.getContraseña() != null
                && profesor.getContraseña().equals(password)) {
            return profesor;
        }
        return null;
    }
    
        public Profesor buscarPorEmail(String email) {
        return coleccion.find(Filters.eq("email", email)).first();
    }
    public List<Profesor> buscarTodos() {
        return coleccion.find().into(new ArrayList<>());
    }

    // Quita el curso (por nombre) de todos los profesores
    public void quitarCurso(String nombreCurso) {
        if (nombreCurso == null) {
            return;
        }
        coleccion.updateMany(
                Filters.exists("cursos"),
                Updates.pull("cursos", new Document("nombre", nombreCurso)));
    }

    // Añade el curso (solo con nombre, como ya está en la BD) a un profesor
    public void agregarCurso(ObjectId idProfesor, String nombreCurso) {
        coleccion.updateOne(
                Filters.eq("_id", idProfesor),
                Updates.push("cursos", new Document("nombre", nombreCurso)));
    }

    // Deja los profesores indicados como únicos asignados al curso
    public void sincronizarCurso(String nombreAnterior, String nombreNuevo, List<ObjectId> idsProfesores) {
        quitarCurso(nombreAnterior);
        if (nombreAnterior == null || !nombreAnterior.equals(nombreNuevo)) {
            quitarCurso(nombreNuevo); // evita duplicados
        }
        for (ObjectId id : idsProfesores) {
            agregarCurso(id, nombreNuevo);
        }
    }
}   