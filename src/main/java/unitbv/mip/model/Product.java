package unitbv.mip.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import javafx.beans.property.*;
import org.hibernate.annotations.Proxy;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "product_type", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Food.class, name = "food"),
        @JsonSubTypes.Type(value = Drink.class, name = "drink"),
        @JsonSubTypes.Type(value = Pizza.class, name = "pizza")
})
@Proxy(lazy = false)
public sealed abstract class Product permits Food, Drink {

    Long id;

    private StringProperty name;
    private DoubleProperty price;
    private ObjectProperty<Category> category;


    public Product(String name, double price, Category category) {
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        this.category = new SimpleObjectProperty<>(category);
    }

    public Product() {
        this.name = new SimpleStringProperty();
        this.price = new SimpleDoubleProperty();
        this.category = new SimpleObjectProperty<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    @Column
    public String getName() { return name.get(); }
    public void setName(String n) { this.name.set(n); }
    public StringProperty nameProperty() { return name; }

    @Column
    public double getPrice() { return price.get(); }
    public void setPrice(double p) { this.price.set(p); }
    public DoubleProperty priceProperty() { return price; }

    @Enumerated(EnumType.STRING)
    public Category getCategory() { return category.get(); }
    public ObjectProperty<Category> categoryProperty() { return category; }
    public void setCategory(Category category) { this.category.set(category); }

    @Override
    public String toString() {
        return getName();
    }
}
