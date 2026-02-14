package com.usermanagement.entities;

import com.usermanagement.requestObjects.CreateUserRequest;
import com.usermanagement.utils.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "user")
public final class User implements UserDetails {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private boolean isAdmin;

    @Column
    private boolean active;

    @Column
    private String password;

    @Transient
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(Role.chooseRole(this.isAdmin).name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }

    public User(CreateUserRequest createUserRequest){
    this.active = createUserRequest.active();
    this.email =createUserRequest.email();
    this.isAdmin = createUserRequest.isAdmin();
    this.name = createUserRequest.name();
    this.password = createUserRequest.password();

}
    // Explicit getters/setters to break compilation cycle (Lombok @Data will also generate these)
    // public Long getId() { return id; }
    // public void setId(Long id) { this.id = id; }
    // public String getName() { return name; }
    // public String getEmail() { return email; }
    // public boolean isAdmin() { return isAdmin; }
    // public boolean isActive() { return active; }
    // public void setName(String name) { this.name = name; }
    // public void setEmail(String email) { this.email = email; }
    // public void setIsAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }
    // public void setActive(boolean active) { this.active = active; }
    // public void setPassword(String password) { this.password = password; }
    // public void setRole(Role role) { this.role = role; }
    // public Role getRole() { return role; }

}
