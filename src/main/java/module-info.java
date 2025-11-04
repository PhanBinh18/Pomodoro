// [MỚI] Đổi tên module để khớp với package gốc
module taskmanagement {
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

    // [QUAN TRỌNG] Mở cả hai package 'controllers' (thường) và 'Controllers' (hoa)
    opens taskmanagement to javafx.fxml;
    opens taskmanagement.controllers to javafx.fxml;

    exports taskmanagement;

    // [QUAN TRỌNG] Xuất cả hai package 'models' (thường) và 'Models' (hoa)
    exports taskmanagement.models;

    // [QUAN TRỌNG] Xuất cả hai package 'controllers' (thường) và 'Controllers' (hoa)
    exports taskmanagement.controllers;
}

