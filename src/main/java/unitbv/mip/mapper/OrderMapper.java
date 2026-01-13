package unitbv.mip.mapper;

import unitbv.mip.model.Order;
import unitbv.mip.model.OrderViewModel;

public class OrderMapper {

    // transforma din db model in view model
    public static OrderViewModel toModel(Order order) {
        if (order == null) {
            return null;
        }

        // extrag nume ospatar
        String waiter = (order.getWaiter() != null) ? order.getWaiter().getUsername() : "N/A (Șters)";

        String priceFormatted = String.format("%.2f RON", order.getTotalAmount());

        String status = (order.getTotalAmount() > 0) ? "Finalizată" : "Anulată";

        return new OrderViewModel(
                order.getId(),
                waiter,
                priceFormatted,
                status
        );
    }
}