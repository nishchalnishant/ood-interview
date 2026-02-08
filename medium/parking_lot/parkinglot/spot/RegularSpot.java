package parkinglot.spot;

import parkinglot.vehicle.Vehicle;
import parkinglot.vehicle.VehicleSize;

public class RegularSpot implements ParkingSpot {
    private int spotNumber;
    private Vehicle vehicle;  // The vehicle currently occupying this spot

    public RegularSpot(int spotNumber) {
        this.spotNumber = spotNumber;
        this.vehicle = null;  // No vehicle occupying initially
    }

    @Override
    public int getSpotNumber() {
        return spotNumber;
    }

    @Override
    public boolean isAvailable() {
        return vehicle == null;  // Available if no vehicle is occupying
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
        this.vehicle = null;  // Make the spot available
    }

    @Override
    public VehicleSize getSize() {
        return VehicleSize.MEDIUM;  // Regular spots fit medium vehicles
    }
} 
