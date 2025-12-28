import model.facility.ParkingLevel;
import model.vehicle.Car;
import service.ParkingLot;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParkingExecution {
    // 10 threads trying to park at the exact same moment
    private static final int THREAD_COUNT = 10;

    public static void main(String[] args) {
        // Single level, 5 spots. 10 threads will compete.
        ParkingLot lot = ParkingLot.getInstance();
        lot.addLevel(new ParkingLevel(1, 5));

        CountDownLatch startLatch = new CountDownLatch(1);
        // To wait all threads to finish before the main exits
        CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    String threadName = Thread.currentThread().getName();
                    System.out.println(threadName + " is READY and waiting at the barrier.");

                    // BLOCK here until the main thread fires the gun
                    startLatch.await();

                    // RACE: All threads hit this line simultaneously
                    System.out.println(threadName + " attempting to park at: " + System.nanoTime());
                    lot.parkVehicle(new Car("CAR-" + threadName));

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (RuntimeException e) {
                    System.err.println(Thread.currentThread().getName() + " FAILED: " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // Give the executor a moment to initialize all threads
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("\n--- MAIN THREAD RELEASING THE LATCH ---\n");

        // Release all threads instantly
        startLatch.countDown();

        // Wait for all to finish
        try {
            endLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executor.shutdown();
    }
}
