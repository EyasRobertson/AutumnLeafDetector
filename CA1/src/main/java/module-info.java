module wit.ca1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires jdk.jfr;
    requires jmh.core;
    requires java.desktop;

    opens wit.ca1 to javafx.fxml;
    exports wit.ca1;
    opens benchmark to javafx.fxml;
    opens benchmark.jmh_generated to jmh.core;
    exports benchmark;
}