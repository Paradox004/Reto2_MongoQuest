module tonguely.reto2_mongoquest {
    requires javafx.controls;
    requires javafx.fxml;

    exports tonguely.reto2_mongoquest;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires java.base;
    opens modelo to org.mongodb.bson;
    opens tonguely.reto2_mongoquest.Controller to javafx.fxml;
}

