package parkinglot.spot;

import parkinglot.vehicle.Vehicle;
import parkinglot.vehicle.VehicleSize;

public interface ParkingSpot {
    boolean isAvailable();
    void occupy(Vehicle vehicle);
    void vacate();
    int getSpotNumber();
    VehicleSize getSize();
}
