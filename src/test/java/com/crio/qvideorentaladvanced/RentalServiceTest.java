package com.crio.qvideorentaladvanced;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import com.crio.qvideorentaladvanced.Services.RentalService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private RentalService rentalService;

    private User user;
    private Video video;
    private Rental rental;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("password");
        user.setRole(User.Role.CUSTOMER);

        video = new Video();
        video.setId(1L);
        video.setTitle("The Matrix");
        video.setDirector("Wachowski Sisters");
        video.setGenre("Sci-Fi");
        video.setAvailable(true);

        rental = new Rental();
        rental.setId(1L);
        rental.setUser(user);
        rental.setVideo(video);
        rental.setRentDate(LocalDateTime.now());
        rental.setActive(true);
    }

    @Test
    void rentVideo_ShouldCreateRental_WhenVideoIsAvailableAndUserHasLessThanTwoRentals() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(videoRepository.findById(anyLong())).thenReturn(Optional.of(video));
        when(rentalRepository.countByUserIdAndActiveTrue(anyLong())).thenReturn(0);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);

        // Act
        RentalResponse result = rentalService.rentVideo(1L, "john@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getVideoId());
        assertEquals("The Matrix", result.getVideoTitle());
        assertTrue(result.isActive());
        
        verify(userRepository).findByEmail("john@example.com");
        verify(videoRepository).findById(1L);
        verify(rentalRepository).countByUserIdAndActiveTrue(1L);
        verify(videoRepository).save(video);
        verify(rentalRepository).save(any(Rental.class));
    }

    @Test
    void rentVideo_ShouldThrowException_WhenVideoIsNotAvailable() {
        // Arrange
        video.setAvailable(false);
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(videoRepository.findById(anyLong())).thenReturn(Optional.of(video));

        // Act & Assert
        assertThrows(VideoNotAvailableException.class, () -> rentalService.rentVideo(1L, "john@example.com"));
        
        verify(userRepository).findByEmail("john@example.com");
        verify(videoRepository).findById(1L);
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void rentVideo_ShouldThrowException_WhenUserAlreadyHasTwoActiveRentals() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(rentalRepository.countByUserIdAndActiveTrue(anyLong())).thenReturn(2);

        // Act & Assert
        assertThrows(MaxRentalsReachedException.class, () -> rentalService.rentVideo(1L, "john@example.com"));
        
        verify(userRepository).findByEmail("john@example.com");
        verify(rentalRepository).countByUserIdAndActiveTrue(1L);
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void returnVideo_ShouldMarkRentalAsReturned_WhenRentalIsActive() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(rentalRepository.findByVideoIdAndUserIdAndActiveTrue(anyLong(), anyLong())).thenReturn(Optional.of(rental));
        
        Rental returnedRental = new Rental();
        returnedRental.setId(1L);
        returnedRental.setUser(user);
        returnedRental.setVideo(video);
        returnedRental.setRentDate(rental.getRentDate());
        returnedRental.setReturnDate(LocalDateTime.now());
        returnedRental.setActive(false);
        
        when(rentalRepository.save(any(Rental.class))).thenReturn(returnedRental);

        // Act
        RentalResponse result = rentalService.returnVideo(1L, "john@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getVideoId());
        assertNotNull(result.getReturnDate());
        assertFalse(result.isActive());
        
        verify(userRepository).findByEmail("john@example.com");
        verify(rentalRepository).findByVideoIdAndUserIdAndActiveTrue(1L, 1L);
        verify(videoRepository).save(video);
        verify(rentalRepository).save(any(Rental.class));
    }

    @Test
    void returnVideo_ShouldThrowException_WhenRentalNotFound() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(rentalRepository.findByVideoIdAndUserIdAndActiveTrue(anyLong(), anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> rentalService.returnVideo(1L, "john@example.com"));
        
        verify(userRepository).findByEmail("john@example.com");
        verify(rentalRepository).findByVideoIdAndUserIdAndActiveTrue(1L, 1L);
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void getUserActiveRentals_ShouldReturnListOfActiveRentals() {
        // Arrange
        Rental rental2 = new Rental();
        rental2.setId(2L);
        rental2.setUser(user);
        
        Video video2 = new Video();
        video2.setId(2L);
        video2.setTitle("Inception");
        
        rental2.setVideo(video2);
        rental2.setRentDate(LocalDateTime.now());
        rental2.setActive(true);
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(rentalRepository.findByUserAndActiveTrue(any(User.class))).thenReturn(Arrays.asList(rental, rental2));

        // Act
        List<RentalResponse> results = rentalService.getUserActiveRentals("john@example.com");

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals("The Matrix", results.get(0).getVideoTitle());
        assertEquals(2L, results.get(1).getId());
        
        verify(userRepository).findByEmail("john@example.com");
        verify(rentalRepository).findByUserAndActiveTrue(user);
    }
}