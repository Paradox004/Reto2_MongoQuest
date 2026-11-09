module tonguely.reto2_mongoquest {
    requires javafx.controls;
    requires javafx.fxml;

    exports tonguely.Main;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires java.base;
    opens tonguely.modelo to org.mongodb.bson;
    opens tonguely.Controller to javafx.fxml;
}

