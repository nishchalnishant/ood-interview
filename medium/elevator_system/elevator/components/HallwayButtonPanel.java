package elevator.components;

import elevator.observer.ElevatorObserver;

import java.util.ArrayList;
import java.util.List;

// Observable Subject: HallwayButtonPanel
public class HallwayButtonPanel {
    private final int floor;
    private final List<ElevatorObserver> observers;

    public HallwayButtonPanel(int floor) {
        this.floor = floor;
        this.observers = new ArrayList<>();
    }

    public void pressButton(Direction direction) {
        notifyObservers(direction);
    }

    public void addObserver(ElevatorObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(Direction direction) {
        for (ElevatorObserver observer : observers) {
            observer.update(floor, direction);
        }
    }
}
