/**
 * Java-Moduldefinition für die JavaFX-Praxisanwendung.
 *
 * <p>Enthält die benötigten Module (JavaFX, JDBC) sowie die Öffnungen/Exports
 * für FXML-Loading und Property-Binding.</p>
 */
module my.app {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires java.sql;
    requires java.logging;
    requires javafx.swing;
    requires java.desktop;

    opens ch.hftm to javafx.fxml;
    opens ch.hftm.controller to javafx.fxml;
    opens ch.hftm.model to javafx.base;
    
    exports ch.hftm;
    exports ch.hftm.controller;
    exports ch.hftm.model;
    exports ch.hftm.util;
    exports ch.hftm.service;
}
