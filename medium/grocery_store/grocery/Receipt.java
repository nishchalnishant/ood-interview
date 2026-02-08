package grocery;

import java.util.Date;

public class Receipt {
    private final String receiptId;
    private final Order order;
    private final Date issueDate;

    public Receipt(Order order) {
        this.receiptId = order.getOrderId();
        this.order = order;
        this.issueDate = new Date();
    }

    public String printReceipt() {
        StringBuilder receipt = new StringBuilder("grocery.Receipt ID: " + receiptId + "\n");
        receipt.append("Date: ").append(issueDate).append("\n");
        receipt.append("Items:\n");

        for (OrderItem item : order.getItems()) {
            receipt.append("- ").append(item.getItem().getName())
                    .append(" x ").append(item.getQuantity())
                    .append(" @ ").append(item.getItem().getPrice());
                    if (order.getAppliedDiscounts().get(item) == null) {
                        receipt.append(" = ")
                                .append(item.calculatePrice()).append("\n");
                    } else {
                        receipt.append(" (")
                                .append(order.getAppliedDiscounts().get(item).getName())
                                .append(")");
                        receipt.append(" = ")
                                .append(item.calculatePriceWithDiscount(order.getAppliedDiscounts().get(item)))
                                .append("\n");
                    }

        }

        receipt.append("Subtotal: ").append(order.calculateSubtotal()).append("\n");
        receipt.append("Total: ").append(order.calculateTotal()).append("\n");

        return receipt.toString();
    }
}
