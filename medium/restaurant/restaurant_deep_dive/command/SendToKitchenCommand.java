package restaurant.command;

import restaurant.reservation.OrderItem;

// Command that handles sending order items (food orders) to the kitchen
public class SendToKitchenCommand implements OrderCommand {
    private final OrderItem orderItem;

    // Constructor that takes the order item to be sent to the kitchen
    public SendToKitchenCommand(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    @Override
    public void execute() {
        orderItem.sendToKitchen();
    }
}
