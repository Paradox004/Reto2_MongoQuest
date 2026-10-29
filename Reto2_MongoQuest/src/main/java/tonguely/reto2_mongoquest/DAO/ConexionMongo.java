package tonguely.reto2_mongoquest.DAO;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoClientSettings;

public class ConexionMongo {

    private static MongoClient cliente;

    public static MongoDatabase conectar() {

        if (cliente == null) {
            cliente = MongoClients.create("mongodb://localhost:27017");
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
