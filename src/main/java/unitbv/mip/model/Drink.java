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
@DiscriminatorValue("DRINK")
public final class Drink extends Product {
    private DoubleProperty volume;
    private BooleanProperty isAlcoholic;

    public Drink(String name, double price, double volume, boolean isAlcoholic) {
        super(name, price, Category.BAUTURI);
        this.volume = new SimpleDoubleProperty(volume);
        this.isAlcoholic = new SimpleBooleanProperty(isAlcoholic);
    }

    public Drink() {
        super();
        this.volume = new SimpleDoubleProperty();
        this.isAlcoholic = new SimpleBooleanProperty();
    }



//    @Override
//    public String toString() {
//        return super.toString() + " - Volum: " + volume + "l - " + (isIsAlcoholic() ? "Contine alcool" : "Fara alcool");
//    }

    @Column
    public double getVolume() {
        return volume.get();
    }

    public DoubleProperty volumeProperty() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume.set(volume);
    }

    @Column
    public boolean isAlcoholic() {
        return isAlcoholic.get();
    }

    @Transient
    public BooleanProperty isAlcoholicProperty() {
        return isAlcoholic;
    }

    public void setAlcoholic(boolean isAlcoholic) {
        this.isAlcoholic.set(isAlcoholic);
    }
}
