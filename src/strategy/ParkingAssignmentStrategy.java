package strategy;

import model.facility.ParkingLevel;
import model.facility.ParkingSpot;
import model.vehicle.Vehicle;

import java.util.List;
import java.util.Optional;

public interface ParkingAssignmentStrategy {
    Optional<ParkingSpot> findSpot(List<ParkingLevel> levels, Vehicle vehicle);
}
