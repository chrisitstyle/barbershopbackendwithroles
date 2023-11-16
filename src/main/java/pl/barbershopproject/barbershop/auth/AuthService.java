package pl.barbershopproject.barbershop.auth;


import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.barbershopproject.barbershop.config.JwtService;
import pl.barbershopproject.barbershop.model.Role;
import pl.barbershopproject.barbershop.model.User;
import pl.barbershopproject.barbershop.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthResponse register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        var token = jwtService.generateToken(user);
        return AuthResponse.builder().token(token).build();
    }
    public AuthResponse authenticate(AuthRequest request) {
        return authenticate(request.getEmail(), request.getPassword());
    }
    public AuthResponse authenticate(@NotNull String email, @NotNull String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
        var user = userRepository.findByEmail(email).orElseThrow();
        var token = jwtService.generateToken(user);
        return AuthResponse.builder().token(token).build();
    }
}