package elevator.dispatch;

import elevator.components.Direction;
import elevator.observer.ElevatorObserver;

// Observer Implementation: ElevatorDispatchController
public class ElevatorDispatchController implements ElevatorObserver {
    @Override
    public void update(int floor, Direction direction) {
        // Logic to handle the floor request
    }
}
