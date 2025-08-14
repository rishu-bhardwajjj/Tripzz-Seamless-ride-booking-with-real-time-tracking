package com.example.ridebooking.controllers;
import java.util.Optional;
import com.example.ridebooking.entities.Driver;
import com.example.ridebooking.entities.Ride;
import com.example.ridebooking.entities.RideStatus;
import com.example.ridebooking.repository.DriverRepository;
import com.example.ridebooking.repository.RideRepository;
import com.example.ridebooking.services.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
@CrossOrigin
public class RideController {

    private final RideService service;
    @Autowired
    private DriverRepository driverRepo;
    @Autowired
    private RideRepository rideRepo;

    public RideController(RideService service) {
        this.service = service;
    }

    // 1. Request ride using JSON body (frontend use)
    @PostMapping("/request/json")
    public Ride requestRideJson(@RequestBody Ride ride) {
        return service.requestRide(ride);
    }

    // 2. Request ride using query params (Postman/manual testing)
    @PostMapping("/request")
    public Ride requestRideParams(@RequestParam double pickupLat,
                                  @RequestParam double pickupLng,
                                  @RequestParam double destLat,
                                  @RequestParam double destLng) {
        Ride ride = new Ride();
        ride.setPickupLat(pickupLat);
        ride.setPickupLng(pickupLng);
        ride.setDestLat(destLat);
        ride.setDestLng(destLng);
        ride.setStatus(RideStatus.REQUESTED);
        return service.requestRide(ride);
    }

    // 3. Assign driver to ride
    @PostMapping("/{rideId}/assign")
    public Ride assignRide(@PathVariable Long rideId, @RequestParam Long driverId) {
        return service.assignRide(rideId, driverId);
    }

    // 4. Get ride by ID
    @GetMapping("/{id}")
    public Ride getRide(@PathVariable Long id) {
        return service.getRide(id);
    }
    @PostMapping("/{rideId}/start")
    public Ride startRide(@PathVariable Long rideId) {
        return service.startRide(rideId);
    }
    @PostMapping("/{rideId}/complete")
    public Ride completeRide(@PathVariable Long rideId) {
        return service.completeRide(rideId);
    }
    @PostMapping("/{rideId}/cancel")
    public Ride cancelRide(@PathVariable Long rideId) {
        return service.cancelRide(rideId);
    }
    @PostMapping("/rides")
    public ResponseEntity<Ride> createRide(@RequestBody Ride rideRequest) {
        // 1. Find the first available driver
        Optional<Driver> availableDriver = driverRepo.findByAvailableTrue().stream().findFirst();

        if (availableDriver.isEmpty()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(null); // Or return a custom message
        }

        Driver driver = availableDriver.get();

        // 2. Assign the driver to the ride
        rideRequest.setDriver(driver);
        rideRequest.setStatus(RideStatus.ASSIGNED); // assuming you use enums
        rideRequest.setPickupLat(rideRequest.getPickupLat());
        rideRequest.setPickupLng(rideRequest.getPickupLng());

        // 3. Mark driver as unavailable
        driver.setAvailable(false);
        driverRepo.save(driver);

        // 4. Save ride
        Ride savedRide = rideRepo.save(rideRequest);

        return ResponseEntity.ok(savedRide);
    }




}
