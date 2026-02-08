package elevator.components;

import java.util.LinkedList;
import java.util.Queue;

public class ElevatorCar {
    private ElevatorStatus status;
    private final Queue<Integer> targetFloors;

    public ElevatorCar(int startingFloor) {
        this.status = new ElevatorStatus(startingFloor, Direction.IDLE);
        this.targetFloors = new LinkedList<>();
    }

    public ElevatorStatus getStatus() {
        return status;
    }

    public void addFloorRequest(int floor) {
        if (!targetFloors.contains(floor)) {
            targetFloors.offer(floor);
            updateDirection(floor);
        }
    }

    public int getCurrentFloor() {
        return status.getCurrentFloor();
    }

    public Direction getCurrentDirection() {
        return status.getCurrentDirection();
    }

    public boolean isIdle() {
        return targetFloors.isEmpty();
    }

    private void updateDirection(int targetFloor) {
        if (status.getCurrentFloor() < targetFloor) {
            status = new ElevatorStatus(status.getCurrentFloor(), Direction.UP);
        } else if (status.getCurrentFloor() > targetFloor) {
            status = new ElevatorStatus(status.getCurrentFloor(), Direction.DOWN);
        }
    }

    public void moveOneStep() {
        if (targetFloors.isEmpty()) return;

        int nextFloor = targetFloors.peek();
        if (status.getCurrentFloor() < nextFloor) {
            status = new ElevatorStatus(status.getCurrentFloor() + 1, Direction.UP);
        } else if (status.getCurrentFloor() > nextFloor) {
            status = new ElevatorStatus(status.getCurrentFloor() - 1, Direction.DOWN);
        } else {
            targetFloors.poll();
            if (targetFloors.isEmpty()) {
                status = new ElevatorStatus(status.getCurrentFloor(), Direction.IDLE);
            }
        }
    }

    public boolean isAtDestination() {
        return !targetFloors.isEmpty() && status.getCurrentFloor() == targetFloors.peek();
    }

    public void nextDestination() {
        if (isAtDestination()) {
            targetFloors.poll();
        }
    }

    public void moveUntilNextFloor() {
        if (!targetFloors.isEmpty()) {
            int nextFloor = targetFloors.peek();
            while (status.getCurrentFloor() != nextFloor) {
                moveOneStep();
            }
        }
    }
}
