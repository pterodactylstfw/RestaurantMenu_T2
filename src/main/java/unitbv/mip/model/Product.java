package unitbv.mip.model;

import javafx.beans.property.*;

public sealed abstract class Product permits Food, Drink {
    private StringProperty name;
    private DoubleProperty price;
    private ObjectProperty<Category> category;

    public Product(String name, double price, Category category) {
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        this.category = new SimpleObjectProperty<>(category);
    }

    public String getName() { return name.get(); }
    public void setName(String n) { this.name.set(n); }
    public StringProperty nameProperty() { return name; }

    public double getPrice() { return price.get(); }
    public void setPrice(double p) { this.price.set(p); }
    public DoubleProperty priceProperty() { return price; }

    public Category getCategory() { return category.get(); }
    public ObjectProperty<Category> categoryProperty() { return category; }


    @Override
    public String toString() {
        return getName();
    }
}
