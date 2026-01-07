package unitbv.mip.config;

public class AppConfig {
    private String restaurantName;
    private double TVA;

    private boolean happyHourActive;
    private boolean mealDealActive;
    private boolean partyPackActive;

    public AppConfig() {}

    public AppConfig(String restaurantName, double TVA) {
        this.restaurantName = restaurantName;
        this.TVA = TVA;
    }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public double getTVA() { return TVA; }
    public void setTVA(double TVA) { this.TVA = TVA; }

    public boolean isHappyHourActive() { return happyHourActive; }
    public void setHappyHourActive(boolean happyHourActive) { this.happyHourActive = happyHourActive; }

    public boolean isMealDealActive() { return mealDealActive; }
    public void setMealDealActive(boolean mealDealActive) { this.mealDealActive = mealDealActive; }

    public boolean isPartyPackActive() { return partyPackActive; }
    public void setPartyPackActive(boolean partyPackActive) { this.partyPackActive = partyPackActive; }
}