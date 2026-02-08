package restaurant.command;

import restaurant.reservation.OrderItem;

// Command that handles delivery of order items (food orders) to customers
public class DeliverCommand implements OrderCommand {
    private final OrderItem orderItem;

    // Constructor that takes the order item to be delivered
    public DeliverCommand(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    @Override
    public void execute() {
        orderItem.deliverToCustomer();
    }
}
