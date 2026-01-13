module unitbv.mip {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.desktop;

    uses jakarta.persistence.spi.PersistenceProvider;

    opens unitbv.mip to javafx.graphics, javafx.fxml;
    opens unitbv.mip.model to javafx.base, org.hibernate.orm.core,
            com.fasterxml.jackson.databind;
    opens unitbv.mip.config to com.fasterxml.jackson.databind;

    exports unitbv.mip;
    opens unitbv.mip.mapper to com.fasterxml.jackson.databind, javafx.base, org.hibernate.orm.core;
}