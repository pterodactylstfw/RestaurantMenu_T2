package unitbv.mip.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import unitbv.mip.model.Order;
import unitbv.mip.strategy.DiscountStrategies;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.json";
    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static void loadConfig() {
        try {
            File file = new File(CONFIG_FILE);
            if (!file.exists()) {
                saveConfig(new AppConfig("La Andrei ~Default settings~", 0.09));
            }

            AppConfig config = mapper.readValue(file, AppConfig.class);

            Order.TVA = config.getTVA();

            DiscountStrategies.HAPPY_HOUR_ACTIVE = config.isHappyHourActive();
            DiscountStrategies.MEAL_DEAL_ACTIVE = config.isMealDealActive();
            DiscountStrategies.PARTY_PACK_ACTIVE = config.isPartyPackActive();

            System.out.println("Configurație încărcată. Happy Hour este: " + (config.isHappyHourActive() ? "ON" : "OFF"));

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Nu s-a putut încărca config.json!");
        }
    }

    public static void saveCurrentState() {
        AppConfig config = new AppConfig();

        config.setRestaurantName("Restaurant 'La Andrei'");
        config.setTVA(Order.TVA);

        config.setHappyHourActive(DiscountStrategies.HAPPY_HOUR_ACTIVE);
        config.setMealDealActive(DiscountStrategies.MEAL_DEAL_ACTIVE);
        config.setPartyPackActive(DiscountStrategies.PARTY_PACK_ACTIVE);

        saveConfig(config);
    }

    private static void saveConfig(AppConfig config) {
        try {
            mapper.writeValue(new File(CONFIG_FILE), config);
            System.out.println("Configurație salvată cu succes!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}