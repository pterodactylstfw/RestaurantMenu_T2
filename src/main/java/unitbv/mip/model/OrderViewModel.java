package unitbv.mip.model;

import javafx.beans.property.*;

public class OrderViewModel {
    private final LongProperty id;
    private final StringProperty waiterName;
    private final StringProperty totalAmount;
    private final StringProperty status;

    public OrderViewModel(Long id, String waiterName, String totalAmount, String status) {
        this.id = new SimpleLongProperty(id);
        this.waiterName = new SimpleStringProperty(waiterName);
        this.totalAmount = new SimpleStringProperty(totalAmount);
        this.status = new SimpleStringProperty(status);
    }

    public LongProperty idProperty() { return id; }
    public StringProperty waiterNameProperty() { return waiterName; }
    public StringProperty totalAmountProperty() { return totalAmount; }
    public StringProperty statusProperty() { return status; }
}