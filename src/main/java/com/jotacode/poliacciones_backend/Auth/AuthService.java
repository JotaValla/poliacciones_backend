package com.jotacode.poliacciones_backend.Auth;

import com.jotacode.poliacciones_backend.jwt.JwtService;
import com.jotacode.poliacciones_backend.model.Usuario;
import com.jotacode.poliacciones_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        Usuario usuario = (Usuario) usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.getToken(usuario);

        return AuthResponse.builder()
                .token(token)
                .cedula(usuario.getCedula())  // Agregar la cédula a la respuesta
                .build();
    }

    public AuthResponse register(RegisterRequest request){
        Usuario usuario = Usuario.builder()
                .cedula(request.getCedula())
                .nombre(request.getNombre())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        usuarioRepository.save(usuario);

        return AuthResponse.builder()
                .token(jwtService.getToken(usuario))
                .build();
    }

}
