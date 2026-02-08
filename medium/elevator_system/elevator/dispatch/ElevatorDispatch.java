package elevator.dispatch;

import elevator.components.Direction;
import elevator.components.ElevatorCar;

import java.util.List;

public class ElevatorDispatch {

    private final DispatchingStrategy strategy;

    public ElevatorDispatch(DispatchingStrategy strategy) {
        this.strategy = strategy;
    }

    public void dispatchElevatorCar(int floor, Direction direction, List<ElevatorCar> elevators) {
        ElevatorCar selectedElevator = strategy.selectElevator(elevators, floor, direction);
        if (selectedElevator != null) {
            selectedElevator.addFloorRequest(floor);
        }
    }
}
