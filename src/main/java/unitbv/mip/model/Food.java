package unitbv.mip.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

@Entity
@DiscriminatorValue("FOOD")
public non-sealed class Food extends Product {
    private DoubleProperty weight;
    private BooleanProperty isVegetarian;

    public Food(String name, double price, double weight, Category category,
                boolean isVegetarian) {
        super(name, price, category);
        this.weight = new SimpleDoubleProperty(weight);
        this.isVegetarian = new SimpleBooleanProperty(isVegetarian);
    }

    public Food() {
        super();
        this.weight = new SimpleDoubleProperty();
        this.isVegetarian = new SimpleBooleanProperty();
    }

//    @Override
//    public String toString() {
//        return super.toString() + " - Gramaj: " + getWeight() + "g" + (isIsVegetarian() ? " [VEG]" : "");
//    }

    @Column
    public double getWeight() {
        return weight.get();
    }

    public DoubleProperty weightProperty() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight.set(weight);
    }

    @Column
    public boolean isVegetarian() {
        return isVegetarian.get();
    }

    @Transient
    public BooleanProperty isVegetarianProperty() {
        return isVegetarian;
    }

    public void setVegetarian(boolean isVegetarian) {
        this.isVegetarian.set(isVegetarian);
    }
}
