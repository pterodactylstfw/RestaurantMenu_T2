package unitbv.mip.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

public non-sealed class Food extends Product {
    private DoubleProperty weight;
    private BooleanProperty isVegetarian;

    public Food(String name, double price, double weight, Category category,
                boolean isVegetarian) {
        super(name, price, category);
        this.weight = new SimpleDoubleProperty(weight);
        this.isVegetarian = new SimpleBooleanProperty(isVegetarian);
    }


//    @Override
//    public String toString() {
//        return super.toString() + " - Gramaj: " + getWeight() + "g" + (isIsVegetarian() ? " [VEG]" : "");
//    }

    public double getWeight() {
        return weight.get();
    }

    public DoubleProperty weightProperty() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight.set(weight);
    }

    public boolean isIsVegetarian() {
        return isVegetarian.get();
    }

    public BooleanProperty isVegetarianProperty() {
        return isVegetarian;
    }

    public void setIsVegetarian(boolean isVegetarian) {
        this.isVegetarian.set(isVegetarian);
    }
}
