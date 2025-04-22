package com.crio.qvideorentaladvanced.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class RentalDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RentalResponse {
        private Long id;
        private Long videoId;
        private String videoTitle;
        private LocalDateTime rentDate;
        private LocalDateTime returnDate;
        private boolean active;
    }
}