package com.jotacode.poliacciones_backend.Auth;

import com.jotacode.poliacciones_backend.error.UsuarioExistenteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @ExceptionHandler(UsuarioExistenteException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleUsuarioExistenteException(UsuarioExistenteException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) throws UsuarioExistenteException {
        return ResponseEntity.ok(authService.register(request));
    }

}
