package com.jotacode.poliacciones_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @Column(name = "usuario_id")
    @Size(min = 10, max = 10, message = "La cédula debe tener 10 caracteres")
    private String cedula;

    @NotNull(message = "El nombre es requerido")
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String nombre;

    @NotNull(message = "El email es requerido")
    @NotBlank(message = "El email es requerido")
    private String email;

    @NotNull(message = "El username es requerido")
    @NotBlank(message = "El username es requerido")
    private String username;

    @NotNull(message = "La contraseña es requerida")
    @NotBlank(message = "La contraseña es requerida")
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Accion> acciones = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
