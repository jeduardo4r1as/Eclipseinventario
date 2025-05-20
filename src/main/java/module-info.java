module com.inventory.appinventario {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires postgresql;
    requires com.jfoenix;
    requires AnimateFX;
    requires org.controlsfx.controls;
    requires java.desktop;
    requires javafx.swing;
    requires static org.slf4j.simple;
    requires webcam.capture;
    requires org.apache.poi.ooxml;
    requires jasperreports;
    requires java.mail;


    opens com.inventory.appinventario to javafx.fxml;
    opens com.inventory.appinventario.model to javafx.base;
    exports com.inventory.appinventario;
    exports com.inventory.appinventario.model;
    exports com.inventory.appinventario.controller;
    exports com.inventory.appinventario.util;
    opens com.inventory.appinventario.controller to javafx.fxml;
}