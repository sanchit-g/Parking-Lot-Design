package model.facility;

import enums.VehicleType;

import java.util.ArrayList;
import java.util.List;

public class ParkingLevel {
    private final int floorNumber;
    private final List<ParkingSpot> spots;

    public ParkingLevel(int floorNumber, int numSpots) {
        this.floorNumber = floorNumber;
        this.spots = new ArrayList<>(numSpots);

        // 50% Cars, 25% Bikes, 25% Trucks
        double spotRatio = numSpots;
        for (int i = 0; i < numSpots; i++) {
            VehicleType type = VehicleType.CAR;
            if (i < spotRatio * 0.25) type = VehicleType.MOTORCYCLE;
            else if (i > spotRatio * 0.75) type = VehicleType.TRUCK;
            spots.add(new ParkingSpot(i, type));
        }
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public List<ParkingSpot> getSpots() {
        return spots;
    }
}
