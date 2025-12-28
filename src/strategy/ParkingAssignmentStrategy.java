package strategy;

import model.facility.ParkingLevel;
import model.facility.ParkingSpot;
import model.vehicle.Vehicle;

import java.util.List;
import java.util.Optional;

public interface ParkingAssignmentStrategy {

    /**
     * Find a spot according to the specific algorithm.
     */
    Optional<ParkingSpot> findSpot(List<ParkingLevel> levels, Vehicle vehicle);

    /**
     * Lifecycle Hook: Called when the system starts or new levels are added.
     * Strategies that cache spots (like Queue) use this to build their cache.
     */
    void indexSpots(List<ParkingLevel> levels);

    /**
     * Lifecycle Hook: Called when a vehicle leaves.
     * Strategies that maintain a pool of free spots use this to reclaim the spot.
     */
    void returnSpot(ParkingSpot spot);
}
