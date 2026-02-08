package restaurant.command;

import restaurant.reservation.OrderItem;

// Command that handles cancellation of order items (food orders)
public class CancelCommand implements OrderCommand {
    private final OrderItem orderItem;

    // Constructor that takes the order item to be canceled
    public CancelCommand(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    @Override
    public void execute() {
        orderItem.cancel();
    }
}
