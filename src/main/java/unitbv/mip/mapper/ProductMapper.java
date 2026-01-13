package unitbv.mip.mapper;

import unitbv.mip.model.*;

public class ProductMapper {

    public static ProductViewModel toModel(Product product) {
        if (product == null) return null;

        String details = "-";
        String type = "Unknown";

        if (product instanceof Food) {
            details = ((Food) product).getWeight() + "g";
            type = "Food";
        } else if (product instanceof Drink) {
            details = ((Drink) product).getVolume() + "l";
            type = "Drink";
        }

        return new ProductViewModel(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory().toString(),
                details,
                type
        );
    }
}