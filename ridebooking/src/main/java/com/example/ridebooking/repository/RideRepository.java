package com.example.ridebooking.repository;

import com.example.ridebooking.entities.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRepository extends JpaRepository<Ride, Long> {}
