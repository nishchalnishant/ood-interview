package elevator.dispatch;

import elevator.components.Direction;
import elevator.components.ElevatorCar;

import java.util.List;

public interface DispatchingStrategy {
    ElevatorCar selectElevator(List<ElevatorCar> elevators, int floor, Direction direction);
}
