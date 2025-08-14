package com.example.ridebooking.controllers;
import com.example.ridebooking.entities.RideStatus;
import com.example.ridebooking.dto.LocationDTO;
import com.example.ridebooking.entities.Driver;
import com.example.ridebooking.entities.Ride;
import com.example.ridebooking.repository.DriverRepository;
import com.example.ridebooking.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@CrossOrigin(origins = "http://localhost:3000")

@RestController
@RequestMapping("/api")
public class DriverLocationController {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideRepository rideRepository;

    // ✅ 1. Update driver location
    @PostMapping("/drivers/{id}/location")
    public ResponseEntity<String> updateLocation(@PathVariable Long id,
                                                 @RequestBody LocationDTO location) {
        Optional<Driver> driverOpt = driverRepository.findById(id);
        if (driverOpt.isPresent()) {
            Driver driver = driverOpt.get();
            driver.setLatitude(location.getLat());
            driver.setLongitude(location.getLng());
            driverRepository.save(driver);
            return ResponseEntity.ok("Location updated");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver not found");
    }

    // ✅ 2. Get driver's current location by ride ID
    @GetMapping("/rides/{rideId}/driver-location")
    public ResponseEntity<LocationDTO> getDriverLocation(@PathVariable Long rideId) {
        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        if (rideOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Ride ride = rideOpt.get();
        Driver driver = ride.getDriver();
        if (driver == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        LocationDTO location = new LocationDTO();
        location.setLat(driver.getLatitude());
        location.setLng(driver.getLongitude());

        return ResponseEntity.ok(location);
    }
    @GetMapping("/rides/{rideId}/eta")
    public ResponseEntity<String> getETA(@PathVariable Long rideId) {
        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        if (rideOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride not found");
        }

        Ride ride = rideOpt.get();
        Driver driver = ride.getDriver();
        if (driver == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver not assigned");
        }

        double lat1 = driver.getLatitude();
        double lon1 = driver.getLongitude();
        double lat2 = ride.getPickupLat();    // assuming you have pickup coordinates
        double lon2 = ride.getPickupLng();

        double distanceKm = haversine(lat1, lon1, lat2, lon2);
        double avgSpeedKmph = 40.0;
        double etaMinutes = (distanceKm / avgSpeedKmph) * 60;

        return ResponseEntity.ok("ETA: " + (int) etaMinutes + " minutes");
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    @PostMapping("/rides/{rideId}/accept")
    public ResponseEntity<String> acceptRide(@PathVariable Long rideId, @RequestParam Long driverId) {
        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        Optional<Driver> driverOpt = driverRepository.findById(driverId);

        if (rideOpt.isEmpty() || driverOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride or Driver not found");
        }

        Ride ride = rideOpt.get();
        ride.setDriver(driverOpt.get());
        ride.setStatus(RideStatus.ASSIGNED);
        rideRepository.save(ride);

        return ResponseEntity.ok("Ride accepted by driver");
    }


}
