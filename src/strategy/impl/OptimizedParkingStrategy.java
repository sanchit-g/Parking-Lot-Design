package strategy.impl;

import enums.VehicleType;
import model.facility.ParkingLevel;
import model.facility.ParkingSpot;
import model.vehicle.Vehicle;
import strategy.ParkingAssignmentStrategy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OptimizedParkingStrategy implements ParkingAssignmentStrategy {

    private final Map<VehicleType, ConcurrentLinkedQueue<ParkingSpot>> freeSpotsPool;

    public OptimizedParkingStrategy() {
        this.freeSpotsPool = new ConcurrentHashMap<>();
        for (VehicleType vehicleType : VehicleType.values()) {
            freeSpotsPool.put(vehicleType, new ConcurrentLinkedQueue<>());
        }
    }

    public void indexSpots(List<ParkingLevel> levels) {
        // Rebuilding the complete cache for simplicity
        for (VehicleType type : VehicleType.values()) {
            freeSpotsPool.get(type).clear();
        }
        for (ParkingLevel level : levels) {
            for (ParkingSpot spot : level.getSpots()) {
                if (spot.isFree()) {
                    freeSpotsPool.get(spot.getType()).add(spot);
                }
            }
        }
    }

    public void returnSpot(ParkingSpot spot) {
        freeSpotsPool.get(spot.getType()).offer(spot);
    }

    @Override
    public Optional<ParkingSpot> findSpot(List<ParkingLevel> levels, Vehicle vehicle) {
        // O(1) operation: poll retrieves and removes the head of the queue atomically
        ParkingSpot spot = freeSpotsPool.get(vehicle.getType()).poll();
        return Optional.ofNullable(spot);
    }
}
