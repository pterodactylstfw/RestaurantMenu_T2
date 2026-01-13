package unitbv.mip.model;

import javafx.beans.property.*;

public class ProductViewModel {
    private final LongProperty id;
    private final StringProperty name;
    private final DoubleProperty price;
    private final StringProperty category;
    private final StringProperty details; // Ex: "350g" sau "0.5l"
    private final String type; // "Food" sau "Drink"

    public ProductViewModel(Long id, String name, double price, String category, String details, String type) {
        this.id = new SimpleLongProperty(id);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        this.category = new SimpleStringProperty(category);
        this.details = new SimpleStringProperty(details);
        this.type = type;
    }

    public LongProperty idProperty() { return id; }
    public long getId() { return id.get(); }

    public StringProperty nameProperty() { return name; }
    public String getName() { return name.get(); }

    public DoubleProperty priceProperty() { return price; }
    public double getPrice() { return price.get(); }

    public StringProperty categoryProperty() { return category; }
    public StringProperty detailsProperty() { return details; }
    public String getType() { return type; }
}