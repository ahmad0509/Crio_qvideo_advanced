package com.crio.qvideorentaladvanced.Services;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.crio.qvideorentaladvanced.Dto.RentalDto.RentalResponse;
import com.crio.qvideorentaladvanced.Entity.Rental;
import com.crio.qvideorentaladvanced.Entity.User;
import com.crio.qvideorentaladvanced.Entity.Video;
import com.crio.qvideorentaladvanced.Exception.CustomException.MaxRentalsReachedException;
import com.crio.qvideorentaladvanced.Exception.CustomException.ResourceNotFoundException;
import com.crio.qvideorentaladvanced.Exception.CustomException.VideoNotAvailableException;
import com.crio.qvideorentaladvanced.Repository.RentalRepository;
import com.crio.qvideorentaladvanced.Repository.UserRepository;
import com.crio.qvideorentaladvanced.Repository.VideoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private static final int MAX_RENTALS = 2;

    @Transactional
    public RentalResponse rentVideo(Long videoId, String userEmail) {
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));


    int activeRentals = rentalRepository.countByUserIdAndActiveTrue(user.getId());
    if (activeRentals >= MAX_RENTALS) {
        throw new MaxRentalsReachedException("User has reached the maximum number of active rentals.");
    }

    Video video = videoRepository.findById(videoId)
        .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + videoId));

    if (!video.isAvailable()) {
        throw new VideoNotAvailableException("This video is not available for rent");
    }

    Rental rental = new Rental();
    rental.setUser(user);  
    rental.setVideo(video);  
    rental.setRentDate(LocalDateTime.now());  
    rental.setActive(true);  

    video.setAvailable(false);
    videoRepository.save(video);
    rental = rentalRepository.save(rental);

    return mapToRentalResponse(rental);
    }

    
        @Transactional
        public RentalResponse returnVideo(Long videoId, String userEmail) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));
            
            Rental rental = rentalRepository.findByVideoIdAndUserIdAndActiveTrue(videoId, user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Active rental not found for this video"));
            
            rental.setReturnDate(LocalDateTime.now());
            rental.setActive(false);
            
            Video video = rental.getVideo();
            video.setAvailable(true);
            videoRepository.save(video);
            
            rental = rentalRepository.save(rental);
            return mapToRentalResponse(rental);
        }
    
        public List<RentalResponse> getUserActiveRentals(String userEmail) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));
            
            return rentalRepository.findByUserAndActiveTrue(user).stream()
                    .map(this::mapToRentalResponse)
                    .collect(Collectors.toList());
        }
    
        private RentalResponse mapToRentalResponse(Rental rental) {
            return new RentalResponse(
                    rental.getId(),
                    rental.getVideo().getId(),
                    rental.getVideo().getTitle(),
                    rental.getRentDate(),
                    rental.getReturnDate(),
                    rental.isActive()
            );
        
    }
}