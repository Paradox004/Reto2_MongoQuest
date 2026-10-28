module tonguely.reto2_mongoquest {
    requires javafx.controls;
    requires javafx.fxml;

    opens tonguely.reto2_mongoquest to javafx.fxml;
    exports tonguely.reto2_mongoquest;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
}
