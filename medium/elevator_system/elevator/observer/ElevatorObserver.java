package elevator.observer;

import elevator.components.Direction;

// Observer Interface
public interface ElevatorObserver {
    void update(int floor, Direction direction);
}
