import model.facility.ParkingLevel;
import model.vehicle.Car;
import service.ParkingLot;

public class ParkingExecution {
    public static void main(String[] args) {
        ParkingLot lot = ParkingLot.getInstance();
        lot.addLevel(new ParkingLevel(1, 8));
        lot.addLevel(new ParkingLevel(2, 12));

        // Simulate concurrent entry
        Runnable parkTask = () -> {
            try {
                lot.parkVehicle(new Car("ABC-" + Thread.currentThread().getId()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        };

        Thread t1 = new Thread(parkTask);
        Thread t2 = new Thread(parkTask);
        Thread t3 = new Thread(parkTask);

        t1.start();
        t2.start();
        t3.start();
    }
}
