package model.ticket;

import java.time.LocalDateTime;
import java.util.UUID;

public class ParkingTicket {
    private final String ticketId;
    private final String licensePlate;
    private final LocalDateTime entryTime;
    private final int spotId;
    private final int floorNumber;

    public ParkingTicket(String licensePlate, int spotId, int floorNumber) {
        this.licensePlate = licensePlate;
        this.spotId = spotId;
        this.floorNumber = floorNumber;
        this.entryTime = LocalDateTime.now();
        this.ticketId = String.valueOf(UUID.randomUUID());
    }

    public int getSpotId() {
        return spotId;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public String getTicketId() {
        return ticketId;
    }

    @Override
    public String toString() { return "Ticket ID: " + ticketId + " | Spot: " + floorNumber + "-" + spotId; }
}
