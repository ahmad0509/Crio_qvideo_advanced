package com.crio.qvideorentaladvanced.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.crio.qvideorentaladvanced.Dto.RentalDto.RentalResponse;
import com.crio.qvideorentaladvanced.Services.RentalService;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @GetMapping
    public ResponseEntity<List<RentalResponse>> getUserActiveRentals(Authentication authentication) {
        return ResponseEntity.ok(rentalService.getUserActiveRentals(authentication.getName()));
    }

    @PostMapping("/videos/{videoId}/rent")
    public ResponseEntity<RentalResponse> rentVideo(
            @PathVariable Long videoId,
            Authentication authentication) {
        return ResponseEntity.ok(rentalService.rentVideo(videoId, authentication.getName()));
    }

    @PostMapping("/videos/{videoId}/return")
    public ResponseEntity<RentalResponse> returnVideo(
            @PathVariable Long videoId,
            Authentication authentication) {
        return ResponseEntity.ok(rentalService.returnVideo(videoId, authentication.getName()));
    }
}