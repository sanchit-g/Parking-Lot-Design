package model.facility;

import enums.VehicleType;
import model.vehicle.Vehicle;

import java.util.concurrent.locks.ReentrantLock;

public class ParkingSpot {
    private final int id;
    private final VehicleType type;
    private boolean isFree;
    private Vehicle currentVehicle;

    // Lock for this specific spot to handle concurrent assignment
    private final ReentrantLock lock = new ReentrantLock(true);

    public ParkingSpot(int id, VehicleType type) {
        this.id = id;
        this.type = type;
        this.isFree = true;
    }

    public int getId() {
        return id;
    }

    public boolean isFree() {
        return isFree;
    }

    public VehicleType getType() {
        return type;
    }

    public boolean assignVehicle(Vehicle vehicle) {
        lock.lock();
        try {
            if (!isFree) {
                return false;
            }

            this.currentVehicle = vehicle;
            this.isFree = false;
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void removeVehicle() {
        lock.lock();
        try {
            this.currentVehicle = null;
            this.isFree = true;
        } finally {
            lock.unlock();
        }
    }
}
