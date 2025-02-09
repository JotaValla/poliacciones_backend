package com.jotacode.poliacciones_backend.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    String cedula;
    String username;
    String password;
    String nombre;
    String email;
}
