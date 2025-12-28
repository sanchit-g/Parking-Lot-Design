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

    @Override
    public void indexSpots(List<ParkingLevel> levels) {
        // This strategy is stateless. It reads the "live" state of the list
        // every time findSpot() is called. It does not need to build an index.
        // Hence, nothing needs to be done here
    }

    @Override
    public void returnSpot(ParkingSpot spot) {
        // Nothing needs to be done here
    }
}
