package unitbv.mip.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

public final class Drink extends Product {
    private DoubleProperty volume;
    private BooleanProperty isAlcoholic;

    public Drink(String name, double price, double volume, boolean isAlcoholic) {
        super(name, price, Category.BAUTURI);
        this.volume = new SimpleDoubleProperty(volume);
        this.isAlcoholic = new SimpleBooleanProperty(isAlcoholic);
    }





//    @Override
//    public String toString() {
//        return super.toString() + " - Volum: " + volume + "l - " + (isIsAlcoholic() ? "Contine alcool" : "Fara alcool");
//    }

    public double getVolume() {
        return volume.get();
    }

    public DoubleProperty volumeProperty() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume.set(volume);
    }

    public boolean isIsAlcoholic() {
        return isAlcoholic.get();
    }

    public BooleanProperty isAlcoholicProperty() {
        return isAlcoholic;
    }

    public void setIsAlcoholic(boolean isAlcoholic) {
        this.isAlcoholic.set(isAlcoholic);
    }
}
