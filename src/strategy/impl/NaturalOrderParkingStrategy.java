package strategy.impl;

import model.facility.ParkingLevel;
import model.facility.ParkingSpot;
import model.vehicle.Vehicle;
import strategy.ParkingAssignmentStrategy;

import java.util.List;
import java.util.Optional;

public class NaturalOrderParkingStrategy implements ParkingAssignmentStrategy {
    @Override
    public Optional<ParkingSpot> findSpot(List<ParkingLevel> levels, Vehicle vehicle) {
        for (ParkingLevel level : levels) {
            for (ParkingSpot spot : level.getSpots()) {
                if (spot.isFree() && spot.getType() == vehicle.getType()) {
                    return Optional.of(spot);
                }
            }
        }
        return Optional.empty();
    }
}
