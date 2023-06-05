// 
module com.rootgame {
    requires org.json;
    requires java.xml;
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires org.slf4j;
    //requires ch.qos.logback;
    requires transitive javafx.graphics;

    opens com.rootgame to javafx.fxml;
    exports com.rootgame;
    exports com.rootgame.controller;
    exports com.rootgame.model;
    exports com.rootgame.model.World.MyObservable;    
    exports com.rootgame.model.World;
    exports com.rootgame.model.World.WorldObjects;
    exports com.rootgame.model.NPC;
    exports com.rootgame.view;
}
