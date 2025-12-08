module unitbv.mip {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;

    uses jakarta.persistence.spi.PersistenceProvider;

    opens unitbv.mip to javafx.graphics, javafx.fxml;
    opens unitbv.mip.model to javafx.base, org.hibernate.orm.core,
            com.fasterxml.jackson.databind;


    exports unitbv.mip;
}