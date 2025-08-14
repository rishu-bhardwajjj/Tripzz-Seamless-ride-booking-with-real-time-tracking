package com.example.ridebooking.services;

import com.example.ridebooking.entities.Driver;
import com.example.ridebooking.entities.Ride;
import com.example.ridebooking.entities.RideStatus;
import com.example.ridebooking.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideService {
    private final RideRepository rideRepo;
    private final DriverRepository driverRepo;

    public RideService(RideRepository rideRepo, DriverRepository driverRepo) {
        this.rideRepo = rideRepo;
        this.driverRepo = driverRepo;
    }

    public Ride requestRide(Ride ride) {
        ride.setStatus(RideStatus.REQUESTED);
        return rideRepo.save(ride);
    }

    public Ride assignRide(Long rideId, Long driverId) {
        Ride ride = rideRepo.findById(rideId).orElseThrow();
        Driver driver = driverRepo.findById(driverId).orElseThrow();

        if (!driver.isAvailable()) throw new RuntimeException("Driver not available");

        ride.setDriver(driver);
        ride.setStatus(RideStatus.ASSIGNED);
        driver.setAvailable(false);

        driverRepo.save(driver);
        return rideRepo.save(ride);
    }

    public Ride getRide(Long id) {
        return rideRepo.findById(id).orElseThrow();
    }
    public Ride startRide(Long rideId) {
        Ride ride = getRide(rideId);
        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new IllegalStateException("Ride must be ASSIGNED to start");
        }
        ride.setStatus(RideStatus.IN_PROGRESS);
        return rideRepo.save(ride);
    }

    public Ride completeRide(Long rideId) {
        Ride ride = getRide(rideId);
        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new IllegalStateException("Ride must be IN_PROGRESS to complete");
        }
        ride.setStatus(RideStatus.COMPLETED);
        return rideRepo.save(ride);
    }

    public Ride cancelRide(Long rideId) {
        Ride ride = getRide(rideId);
        if (ride.getStatus() == RideStatus.COMPLETED) {
            throw new IllegalStateException("Completed ride cannot be cancelled");
        }
        ride.setStatus(RideStatus.CANCELLED);
        return rideRepo.save(ride);
    }

}
