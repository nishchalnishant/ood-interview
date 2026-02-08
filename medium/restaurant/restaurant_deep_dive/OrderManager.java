package restaurant;

import restaurant.command.OrderCommand;

import java.util.ArrayList;
import java.util.List;

// Step 3: Implement the Invoker, which will replace direct method implementation.
public class OrderManager {
    private final List<OrderCommand> commandQueue = new ArrayList<>();

    // Adds a command to the queue for later execution
    public void addCommand(OrderCommand command) {
        commandQueue.add(command);
    }

    // Executes all commands in the queue and clears it
    public void executeCommands() {
        for (OrderCommand command : commandQueue) {
            command.execute();
        }
        commandQueue.clear();
    }
}
