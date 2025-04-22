package com.crio.qvideorentaladvanced;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crio.qvideorentaladvanced.Dto.VideoDto.VideoRequest;
import com.crio.qvideorentaladvanced.Dto.VideoDto.VideoResponse;
import com.crio.qvideorentaladvanced.Entity.Video;
import com.crio.qvideorentaladvanced.Exception.CustomException.ResourceNotFoundException;
import com.crio.qvideorentaladvanced.Repository.VideoRepository;
import com.crio.qvideorentaladvanced.Services.VideoService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private VideoService videoService;

    private Video video1;
    private Video video2;
    private VideoRequest videoRequest;

    @BeforeEach
    void setUp() {
        video1 = new Video();
        video1.setId(1L);
        video1.setTitle("The Matrix");
        video1.setDirector("Wachowski Sisters");
        video1.setGenre("Sci-Fi");
        video1.setAvailable(true);

        video2 = new Video();
        video2.setId(2L);
        video2.setTitle("Inception");
        video2.setDirector("Christopher Nolan");
        video2.setGenre("Sci-Fi");
        video2.setAvailable(false);

        videoRequest = new VideoRequest("The Matrix Reloaded", "Wachowski Sisters", "Sci-Fi/Action");
    }

    @Test
    void getAllVideos_ShouldReturnAllVideos() {
        // Arrange
        when(videoRepository.findAll()).thenReturn(Arrays.asList(video1, video2));

        // Act
        List<VideoResponse> result = videoService.getAllVideos();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("The Matrix", result.get(0).getTitle());
        assertEquals("Inception", result.get(1).getTitle());
        verify(videoRepository).findAll();
    }

    @Test
    void getAvailableVideos_ShouldReturnOnlyAvailableVideos() {
        // Arrange
        when(videoRepository.findByAvailableTrue()).thenReturn(List.of(video1));

        // Act
        List<VideoResponse> result = videoService.getAvailableVideos();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("The Matrix", result.get(0).getTitle());
        assertTrue(result.get(0).isAvailable());
        verify(videoRepository).findByAvailableTrue();
    }

    @Test
    void getVideoById_ShouldReturnVideo_WhenVideoExists() {
        // Arrange
        when(videoRepository.findById(anyLong())).thenReturn(Optional.of(video1));

        // Act
        VideoResponse result = videoService.getVideoById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("The Matrix", result.getTitle());
        assertEquals("Wachowski Sisters", result.getDirector());
        assertEquals("Sci-Fi", result.getGenre());
        assertTrue(result.isAvailable());
        verify(videoRepository).findById(1L);
    }

    @Test
    void getVideoById_ShouldThrowException_WhenVideoDoesNotExist() {
        // Arrange
        when(videoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> videoService.getVideoById(1L));
        verify(videoRepository).findById(1L);
    }

    @Test
    void createVideo_ShouldReturnCreatedVideo() {
        // Arrange
        Video newVideo = new Video();
        newVideo.setId(3L);
        newVideo.setTitle(videoRequest.getTitle());
        newVideo.setDirector(videoRequest.getDirector());
        newVideo.setGenre(videoRequest.getGenre());
        newVideo.setAvailable(true);
        
        when(videoRepository.save(any(Video.class))).thenReturn(newVideo);

        // Act
        VideoResponse result = videoService.createVideo(videoRequest);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("The Matrix Reloaded", result.getTitle());
        assertEquals("Wachowski Sisters", result.getDirector());
        assertEquals("Sci-Fi/Action", result.getGenre());
        assertTrue(result.isAvailable());
        verify(videoRepository).save(any(Video.class));
    }

    @Test
    void updateVideo_ShouldReturnUpdatedVideo_WhenVideoExists() {
        // Arrange
        when(videoRepository.findById(anyLong())).thenReturn(Optional.of(video1));
        
        Video updatedVideo = new Video();
        updatedVideo.setId(1L);
        updatedVideo.setTitle(videoRequest.getTitle());
        updatedVideo.setDirector(videoRequest.getDirector());
        updatedVideo.setGenre(videoRequest.getGenre());
        updatedVideo.setAvailable(true);
        
        when(videoRepository.save(any(Video.class))).thenReturn(updatedVideo);

        // Act
        VideoResponse result = videoService.updateVideo(1L, videoRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("The Matrix Reloaded", result.getTitle());
        assertEquals("Wachowski Sisters", result.getDirector());
        assertEquals("Sci-Fi/Action", result.getGenre());
        verify(videoRepository).findById(1L);
        verify(videoRepository).save(any(Video.class));
    }

    @Test
    void updateVideo_ShouldThrowException_WhenVideoDoesNotExist() {
        // Arrange
        when(videoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> videoService.updateVideo(1L, videoRequest));
        verify(videoRepository).findById(1L);
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    void deleteVideo_ShouldDeleteVideo_WhenVideoExists() {
        // Arrange
        when(videoRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(videoRepository).deleteById(anyLong());

        // Act
        videoService.deleteVideo(1L);

        // Assert
        verify(videoRepository).existsById(1L);
        verify(videoRepository).deleteById(1L);
    }

    @Test
    void deleteVideo_ShouldThrowException_WhenVideoDoesNotExist() {
        // Arrange
        when(videoRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> videoService.deleteVideo(1L));
        verify(videoRepository).existsById(1L);
        verify(videoRepository, never()).deleteById(anyLong());
    }
}