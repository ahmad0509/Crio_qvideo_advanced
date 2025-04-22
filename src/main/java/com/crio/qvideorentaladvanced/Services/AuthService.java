package com.crio.qvideorentaladvanced.Services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crio.qvideorentaladvanced.Config.JwtService;
import com.crio.qvideorentaladvanced.Dto.AuthDto.AuthResponse;
import com.crio.qvideorentaladvanced.Dto.AuthDto.LoginRequest;
import com.crio.qvideorentaladvanced.Dto.AuthDto.RegisterRequest;
import com.crio.qvideorentaladvanced.Entity.User;
import com.crio.qvideorentaladvanced.Exception.CustomException.UserAlreadyExistsException;
import com.crio.qvideorentaladvanced.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        User.Role role = request.getRole() != null ? request.getRole() : User.Role.CUSTOMER;
        
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        
        userRepository.save(user);
        
        String jwtToken = jwtService.generateToken(
            new org.springframework.security.core.userdetails.User(
                user.getEmail(), 
                user.getPassword(), 
                java.util.Collections.singletonList(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                )
            )
        );
        
        return new AuthResponse(jwtToken, user.getEmail(), user.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        String jwtToken = jwtService.generateToken(userDetails);
        
        return new AuthResponse(jwtToken, user.getEmail(), user.getRole());
    }
}