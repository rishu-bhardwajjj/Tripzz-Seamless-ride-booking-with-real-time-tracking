package com.example.ridebooking.entities;

import jakarta.persistence.*;

@Entity
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double pickupLat;
    private double pickupLng;

    @Column(name = "dest_lat")
    private double destLat;

    @Column(name = "dest_lng")
    private double destLng;

    @Enumerated(EnumType.STRING)
    private RideStatus status;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    public void setPickupLat(double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public void setPickupLng(double pickupLng) {
        this.pickupLng = pickupLng;
    }

    public void setDestLat(double destLat) {
        this.destLat = destLat;
    }

    public void setDestLng(double destLng) {
        this.destLng = destLng;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Long getId() {
        return id;
    }

    public double getPickupLat() {
        return pickupLat;
    }

    public double getPickupLng() {
        return pickupLng;
    }

    public double getDestLat() {
        return destLat;
    }

    public double getDestLng() {
        return destLng;
    }

    public RideStatus getStatus() {
        return status;
    }

    public Driver getDriver() {
        return driver;
    }
}
