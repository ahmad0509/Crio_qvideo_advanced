package com.crio.qvideorentaladvanced.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class VideoDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VideoRequest {
        @NotBlank(message = "Title is required")
        private String title;
        
        @NotBlank(message = "Director is required")
        private String director;
        
        @NotBlank(message = "Genre is required")
        private String genre;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VideoResponse {
        private Long id;
        private String title;
        private String director;
        private String genre;
        private boolean available;
    }
}
