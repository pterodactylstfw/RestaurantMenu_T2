module unitbv.mip {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    opens unitbv.mip to javafx.graphics, javafx.fxml;
    opens unitbv.mip.model to javafx.base;

    exports unitbv.mip;
}