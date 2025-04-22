package com.crio.qvideorentaladvanced.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * CustomException acts as the base class for all custom exceptions.
 * Other specific exceptions are nested as inner classes.
 */
public class CustomException {

    /**
     * ResourceNotFoundException returns 404 Not Found status.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * UserAlreadyExistsException returns 400 Bad Request status.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }

    /**
     * VideoNotAvailableException returns 400 Bad Request status.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class VideoNotAvailableException extends RuntimeException {
        public VideoNotAvailableException(String message) {
            super(message);
        }
    }

    /**
     * MaxRentalsReachedException returns 400 Bad Request status.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class MaxRentalsReachedException extends RuntimeException {
        public MaxRentalsReachedException(String message) {
            super(message);
        }
    }
}
