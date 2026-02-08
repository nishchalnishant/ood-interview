package parkinglot.spot;

import parkinglot.vehicle.Vehicle;
import parkinglot.vehicle.VehicleSize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingManager {
    private final Map<VehicleSize, List<ParkingSpot>> availableSpots;
    private final Map<Vehicle, ParkingSpot> vehicleToSpotMap;

    // Create Parking Manager based on a given map of available spots
    public ParkingManager(Map<VehicleSize, List<ParkingSpot>> availableSpots) {
        this.availableSpots = availableSpots;
        this.vehicleToSpotMap = new HashMap<>();
    }

    public ParkingSpot findSpotForVehicle(Vehicle vehicle) {
        VehicleSize vehicleSize = vehicle.getSize();

        // Start looking from the smallest spot that can fit the vehicle
        for (VehicleSize size : VehicleSize.values()) {
            if (size.ordinal() >= vehicleSize.ordinal()) {
                List<ParkingSpot> spots = availableSpots.get(size);
                for (ParkingSpot spot : spots) {
                    if (spot.isAvailable()) {
                        return spot;  // Return the first available spot
                    }
                }
            }
        }
        return null;  // No suitable spot found
    }

    public ParkingSpot parkVehicle(Vehicle vehicle) {
        ParkingSpot spot = findSpotForVehicle(vehicle);
        if (spot != null) {
            spot.occupy(vehicle);
            // Record the parking spot for the vehicle
            vehicleToSpotMap.put(vehicle, spot);  
            // Remove the spot from the available list
            availableSpots.get(spot.getSize()).remove(spot);
            return spot;  // Parking successful
        }
        return null;  // No spot found for this vehicle
    }

    public void unparkVehicle(Vehicle vehicle) {
        ParkingSpot spot = vehicleToSpotMap.remove(vehicle);
        if (spot != null) {
            spot.vacate();
            availableSpots.get(spot.getSize()).add(spot);
        }
    }

    // used for testing 
    public ParkingSpot findVehicleSpot(Vehicle vehicle) {
        return vehicleToSpotMap.get(vehicle);
    }

}
