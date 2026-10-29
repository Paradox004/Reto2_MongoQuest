package tonguely.reto2_mongoquest.DAO;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import java.util.ArrayList;
import modelo.Usuario;


public class UsuarioDAO {

     private MongoCollection<Usuario> coleccion;

    public UsuarioDAO() {

        MongoDatabase bd = ConexionMongo.conectar();

        coleccion = bd.getCollection("Usuario", Usuario.class);
    }

    public ArrayList<Usuario> obtenerUsuarios() {

        ArrayList<Usuario> usuarios = new ArrayList<>();

        for (Usuario usuario : coleccion.find()) {
            usuarios.add(usuario);
        }

        return usuarios;
    }
    
    public boolean comprobarLogin(String email, String password) {

        // Buscar al usuario por email
        Usuario usuario = coleccion.find(eq("email", email)).first();

        // Comprobar si existe y si la contraseña coincide
        if (usuario != null
                && usuario.getContraseña().equals(password)) {
            return true;
        }

        return false;
    }
}