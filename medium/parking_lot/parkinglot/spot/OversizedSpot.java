package parkinglot.spot;

import parkinglot.vehicle.Vehicle;
import parkinglot.vehicle.VehicleSize;

public class OversizedSpot implements ParkingSpot {
    private int spotNumber;
    private Vehicle vehicle;

    public OversizedSpot(int spotNumber) {
        this.spotNumber = spotNumber;
        this.vehicle = null;
    }

    @Override
    public int getSpotNumber() {
        return spotNumber;
    }

    @Override
    public boolean isAvailable() {
        return vehicle == null;
    }

    @Override
    public void occupy(Vehicle vehicle) {
        if (isAvailable()) {
            this.vehicle = vehicle;
        } else {
            // Spot is already occupied
        }
    }

    @Override
    public void vacate() {
        this.vehicle = null;
    }

    @Override
    public VehicleSize getSize() {
        return VehicleSize.LARGE;
    }
} 
