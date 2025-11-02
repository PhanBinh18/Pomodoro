module com.btl.taskmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires javafx.media;
    requires java.desktop;

    opens taskmanagement to javafx.fxml;
    opens taskmanagement.Controllers to javafx.fxml;
    exports taskmanagement;
    exports taskmanagement.models;
    exports taskmanagement.controllers;
}