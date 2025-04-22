package com.crio.qvideorentaladvanced.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crio.qvideorentaladvanced.Entity.Rental;
import com.crio.qvideorentaladvanced.Entity.User;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByUserAndActiveTrue(User user);
    Optional<Rental> findByVideoIdAndUserIdAndActiveTrue(Long videoId, Long userId);
    int countByUserIdAndActiveTrue(Long userId);
}