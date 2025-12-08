package unitbv.mip.config;

public class AppConfig {
    private String restaurantName;
    private double TVA;

    public AppConfig() {}

    public AppConfig(String restaurantName, double TVA) {
        this.restaurantName = restaurantName;
        this.TVA = TVA;
    }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName; }

    public double getTVA() { return TVA; }
    public void setTVA(double TVA) { this.TVA = TVA; }
}