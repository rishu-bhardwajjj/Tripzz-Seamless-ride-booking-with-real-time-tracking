package com.example.ridebooking.controllers;

import com.example.ridebooking.entities.Driver;
import com.example.ridebooking.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin
public class DriverController {

    @Autowired
    private DriverRepository driverRepository;

    @PostMapping("/add")
    public Driver addDriver(@RequestParam String name,
                            @RequestParam double lat,
                            @RequestParam double lng) {
        Driver driver = new Driver();
        driver.setName(name);
        driver.setLatitude(lat);
        driver.setLongitude(lng);
        driver.setAvailable(true);
        return driverRepository.save(driver);
    }
    @GetMapping("/available")
    public List<Driver> getAvailableDrivers() {
        return driverRepository.findByAvailableTrue();
    }
    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        if (!driverRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        driverRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        return ResponseEntity.ok(driver);
    }

    @PostMapping("/{id}/update-location")
    public Driver updateDriverLocation(@PathVariable Long id,
                                       @RequestParam double lat,
                                       @RequestParam double lng) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        driver.setLatitude(lat);
        driver.setLongitude(lng);
        return driverRepository.save(driver);
    }


}
