package edu.studyarena.training.service;

import edu.studyarena.training.dto.AuthResponse;
import edu.studyarena.training.dto.LoginRequest;
import edu.studyarena.training.dto.RegisterRequest;
import edu.studyarena.training.dto.UserResponse;
import edu.studyarena.training.entity.User;
import edu.studyarena.training.repository.UserRepository;
import edu.studyarena.training.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final String INVALID_CREDENTIALS = "Email o contraseña incorrectos";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("Ya existe un usuario con ese email");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        User saved = userRepository.save(user);

        return new UserResponse(saved.getId(), saved.getName(), saved.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException(INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException(INVALID_CREDENTIALS);
        }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(token, new UserResponse(user.getId(), user.getName(), user.getEmail()));
    }
}
