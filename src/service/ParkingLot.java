package service;

import model.facility.ParkingLevel;
import model.facility.ParkingSpot;
import model.ticket.ParkingTicket;
import model.vehicle.Vehicle;
import strategy.ParkingAssignmentStrategy;
import strategy.impl.NaturalOrderParkingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ParkingLot {
    private static ParkingLot instance;
    private final List<ParkingLevel> levels;
    private final ParkingAssignmentStrategy parkingStrategy;

    // Track active tickets
    private final Map<String, ParkingTicket> activeTickets;

    // How many times to retry before giving up
    private final int MAX_RETRIES = 3;

    private ParkingLot() {
        levels = new ArrayList<>();
        activeTickets = new ConcurrentHashMap<>();
        parkingStrategy = new NaturalOrderParkingStrategy();
    }

    public static synchronized ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }

    public void addLevel(ParkingLevel level) {
        levels.add(level);
    }

    public ParkingTicket parkVehicle(Vehicle vehicle) {
        int attempts = 0;

        while (attempts < MAX_RETRIES) {
            // Find a candidate spot using Strategy
            Optional<ParkingSpot> spotOpt = parkingStrategy.findSpot(levels, vehicle);

            if (spotOpt.isPresent()) {
                ParkingSpot spot = spotOpt.get();

                // Try to lock and park
                // If this returns true, we own the spot.
                if (spot.assignVehicle(vehicle)) {
                    // Generate ticket
                    ParkingTicket ticket = new ParkingTicket(
                            vehicle.getLicensePlate(),
                            spot.getId(),
                            findFloorForSpot(spot)
                    );
                    activeTickets.put(ticket.getTicketId(), ticket);
                    System.out.println("Vehicle: " + vehicle.getLicensePlate() + " parked successfully, ticket ID: " + ticket.getTicketId());
                    return ticket;
                }

                // If assignVehicle returned false, it means another thread stole the spot
                // We increment attempts and LOOP again to find the NEXT available spot.
                System.out.println("Contention detected for " + vehicle.getLicensePlate() + ". Retrying...");
            } else {
                // Strategy returned empty. Parking lot is genuinely full.
                break;
            }
            attempts++;
        }

        throw new RuntimeException("Parking full or No suitable spot found!");
    }

    // Helper to find which floor a spot belongs to
    private int findFloorForSpot(ParkingSpot spot) {
        for (ParkingLevel lvl : levels) {
            if (lvl.getSpots().contains(spot)) return lvl.getFloorNumber();
        }
        return -1;
    }

    public void exitVehicle(ParkingTicket ticket) {
        int floorNumber = ticket.getFloorNumber();
        int spotId = ticket.getSpotId();

        ParkingLevel level = levels.stream()
                .filter(l -> l.getFloorNumber() == floorNumber)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Floor"));

        ParkingSpot spot = level.getSpots().stream()
                .filter(s -> s.getId() == spotId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Spot"));

        spot.removeVehicle();
        activeTickets.remove(ticket.getTicketId());

        System.out.println("Vehicle left successfully, ticket ID: " + ticket.getTicketId() + "Spot ID: " + spotId + " is now free.");
    }
}
