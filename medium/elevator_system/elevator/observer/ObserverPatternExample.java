package elevator.observer;

import elevator.components.Direction;
import elevator.dispatch.ElevatorDispatchController;
import elevator.ElevatorSystem;
import elevator.components.HallwayButtonPanel;

// Example Usage
class ObserverPatternExample {
    private final ElevatorSystem elevatorSystem;

    public ObserverPatternExample(ElevatorSystem elevatorSystem) {
        this.elevatorSystem = elevatorSystem;
    }

    public static void main(String[] args) {
        HallwayButtonPanel hallwayButtonPanel = new HallwayButtonPanel(5);
        ElevatorDispatchController controller = new ElevatorDispatchController();

        hallwayButtonPanel.addObserver(controller);
        hallwayButtonPanel.pressButton(Direction.UP);
    }
}
