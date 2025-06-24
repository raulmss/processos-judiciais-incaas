package com.incaas.tjrn.security.auth;

import com.incaas.tjrn.exception.exceptions.UserAlreadyExistsException;
import com.incaas.tjrn.security.config.JwtService;
import com.incaas.tjrn.security.exceptions.CustomAuthenticationException;
import com.incaas.tjrn.security.exceptions.UserNotFoundException;
import com.incaas.tjrn.security.user.Role;
import com.incaas.tjrn.security.user.User;
import com.incaas.tjrn.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request, Role role) {

        //Check if the user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists.");
        }
        //Build the new user
        var user = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getFirstName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();
        //Save the new user
        userRepository.save(user);

        //Generate the user's Token
        var jwtToken = jwtService.generateToken(user);

        //Return the authentication response containing the JWT Token.
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try{
            //This member will do all the authentication work for me. If email or password are wrong, then an Exception will be thrown.
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        }catch (AuthenticationException e){
            throw new CustomAuthenticationException("Authentication failed for user with email: " + request.getEmail(), e);
        }

        //if it gets to this point, user is authenticated, credentials are alright.
        //Then we fetch the User object.
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("No user found with the email: " + request.getEmail()));

        //Generate the user's Token
        var jwtToken = jwtService.generateToken(user);

        //Return the authentication response containing the JWT Token.
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
