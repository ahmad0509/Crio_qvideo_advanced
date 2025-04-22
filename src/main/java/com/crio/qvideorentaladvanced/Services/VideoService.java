package com.crio.qvideorentaladvanced.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.crio.qvideorentaladvanced.Dto.VideoDto.VideoRequest;
import com.crio.qvideorentaladvanced.Dto.VideoDto.VideoResponse;
import com.crio.qvideorentaladvanced.Entity.Video;
import com.crio.qvideorentaladvanced.Exception.CustomException.ResourceNotFoundException;
import com.crio.qvideorentaladvanced.Repository.VideoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;

    public List<VideoResponse> getAllVideos() {
        return videoRepository.findAll().stream()
                .map(this::mapToVideoResponse)
                .collect(Collectors.toList());
    }

    public List<VideoResponse> getAvailableVideos() {
        return videoRepository.findByAvailableTrue().stream()
                .map(this::mapToVideoResponse)
                .collect(Collectors.toList());
    }

    public VideoResponse getVideoById(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));
        return mapToVideoResponse(video);
    }

    public VideoResponse createVideo(VideoRequest videoRequest) {
        Video video = new Video();
        video.setTitle(videoRequest.getTitle());
        video.setDirector(videoRequest.getDirector());
        video.setGenre(videoRequest.getGenre());
        video.setAvailable(true);
        
        video = videoRepository.save(video);
        return mapToVideoResponse(video);
    }

    public VideoResponse updateVideo(Long id, VideoRequest videoRequest) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));
        
        video.setTitle(videoRequest.getTitle());
        video.setDirector(videoRequest.getDirector());
        video.setGenre(videoRequest.getGenre());
        
        video = videoRepository.save(video);
        return mapToVideoResponse(video);
    }

    public void deleteVideo(Long id) {
        if (!videoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Video not found with id: " + id);
        }
        videoRepository.deleteById(id);
    }

    private VideoResponse mapToVideoResponse(Video video) {
        return new VideoResponse(
                video.getId(),
                video.getTitle(),
                video.getDirector(),
                video.getGenre(),
                video.isAvailable()
        );
    }
}