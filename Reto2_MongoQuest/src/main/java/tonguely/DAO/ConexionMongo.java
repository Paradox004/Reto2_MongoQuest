
package tonguely.DAO;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import java.util.concurrent.TimeUnit;

/**
 * Clase encargada de gestionar la conexión con MongoDB.
 *
 * @author anartz
 */
public class ConexionMongo {

    /** Cliente de conexión con MongoDB. */
    private static MongoClient cliente;

    /**
     * Establece la conexión con MongoDB y configura los codecs
     * para trabajar con objetos Java.
     *
     * @return base de datos TonguelyDB.
     */
    public static MongoDatabase conectar() {

        if (cliente == null) {
            // Si MongoDB no está arrancado, el driver espera 30 s por defecto antes
            // de lanzar el error. Se baja a 5 s para que el usuario vea el aviso antes.
            MongoClientSettings ajustes = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                    .applyToClusterSettings(b -> b.serverSelectionTimeout(5, TimeUnit.SECONDS))
                    .build();
            cliente = MongoClients.create(ajustes);
        }

        CodecProvider pojoCodecProvider = PojoCodecProvider.builder()
                .automatic(true)
                .build();

        CodecRegistry codecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(pojoCodecProvider)
        );

        return cliente.getDatabase("TonguelyDB")
                .withCodecRegistry(codecRegistry);
    }
}
