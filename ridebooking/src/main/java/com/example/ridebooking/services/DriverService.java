package com.example.ridebooking.services;

import com.example.ridebooking.entities.Driver;
import com.example.ridebooking.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {
    private final DriverRepository repo;

    public DriverService(DriverRepository repo) {
        this.repo = repo;
    }

    public List<Driver> getAvailableDrivers() {
        return repo.findByAvailableTrue();
    }

    public Driver updateLocation(Long id, double lat, double lng) {
        Driver d = repo.findById(id).orElseThrow();
        d.setLatitude(lat);
        d.setLongitude(lng);
        return repo.save(d);
    }

    public Driver getById(Long id) {
        return repo.findById(id).orElseThrow();
    }
}
