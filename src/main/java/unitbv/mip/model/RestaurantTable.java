package unitbv.mip.model;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurant_tables")
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int tableNumber;
    private boolean isOccupied;

    public RestaurantTable() {}
    public RestaurantTable(int number) {
        this.tableNumber = number;
        this.isOccupied = false;
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public int getTableNumber() {return tableNumber;}

    public void setTableNumber(int tableNumber) {this.tableNumber = tableNumber;}

    public boolean isOccupied() {return isOccupied;}

    public void setOccupied(boolean occupied) {isOccupied = occupied;}
}
