package tonguely.reto2_mongoquest.DAO;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class ConexionMongo {

    private static MongoClient cliente;
    private static MongoDatabase bd;

    public static MongoDatabase conectar() {

        if (cliente == null) {
            cliente = MongoClients.create("mongodb://localhost:27017");
            bd = cliente.getDatabase("TonguelyDB");
        }

        return bd;
    }

    public static void cerrar() {
        if (cliente != null) {
            cliente.close();
            cliente = null;
            bd = null;
        }
    }
}
