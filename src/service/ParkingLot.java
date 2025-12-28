package service;

import model.facility.ParkingLevel;
import model.facility.ParkingSpot;
import model.ticket.ParkingTicket;
import model.vehicle.Vehicle;
import strategy.ParkingAssignmentStrategy;
import strategy.impl.NaturalOrderParkingStrategy;
import strategy.impl.OptimizedParkingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ParkingLot {
    private static volatile ParkingLot instance;
    private final List<ParkingLevel> levels;
    private final ParkingAssignmentStrategy parkingStrategy;

    // Track active tickets
    private final Map<String, ParkingTicket> activeTickets;

    private ParkingLot() {
        levels = new ArrayList<>();
        activeTickets = new ConcurrentHashMap<>();
        parkingStrategy = new OptimizedParkingStrategy();
    }

    public static synchronized ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }

    public void addLevel(ParkingLevel level) {
        levels.add(level);
        // Tell the strategy to index the newly added spots
        parkingStrategy.indexSpots(levels);
    }

    public ParkingTicket parkVehicle(Vehicle vehicle) {
        Optional<ParkingSpot> spotOpt = parkingStrategy.findSpot(levels, vehicle);

        if (spotOpt.isPresent()) {
            ParkingSpot spot = spotOpt.get();
            spot.assignVehicle(vehicle);

            ParkingTicket ticket = new ParkingTicket(
                    vehicle.getLicensePlate(),
                    spot.getId(),
                    findFloorForSpot(spot)
            );

            activeTickets.put(ticket.getTicketId(), ticket);
            System.out.println("Vehicle " + vehicle.getLicensePlate() + " parked. Ticket: " + ticket.getTicketId());

            return ticket;
        }

        throw new RuntimeException("No spots available for type: " + vehicle.getType());
    }

    // Helper to find which floor a spot belongs to
    private int findFloorForSpot(ParkingSpot spot) {
        for (ParkingLevel lvl : levels) {
            if (lvl.getSpots().contains(spot)) return lvl.getFloorNumber();
        }
        throw new IllegalStateException("Spot does not belong to any known level!");
    }

    public void exitVehicle(String ticketId) {
        if (!activeTickets.containsKey(ticketId)) {
            throw new IllegalStateException("Ticket " + ticketId + " not found or already processed!");
        }

        ParkingTicket ticket = activeTickets.get(ticketId);

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
        // Return the spot to queue so others can use it
        parkingStrategy.returnSpot(spot);

        activeTickets.remove(ticketId);

        System.out.println("Vehicle " + ticket.getLicensePlate() + " exited. Spot " + spot.getId() + " is now FREE.");
    }
}
