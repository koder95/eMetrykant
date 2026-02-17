module eme {
    requires java.xml;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.kohsuke.github.api;
    requires org.controlsfx.controls;
    requires java.logging;

    exports pl.koder95.eme;
    opens pl.koder95.eme;
    opens pl.koder95.eme.fx;
}