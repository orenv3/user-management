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
@Table(name = "users")
public final class Users implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private Boolean isAdmin;

    @Column
    private Boolean active;

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
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    public Users(CreateUserRequest createUserRequest){
    this.active = createUserRequest.active();
    this.email =createUserRequest.email();
    this.isAdmin = createUserRequest.isAdmin();
    this.name = createUserRequest.name();
    this.password = createUserRequest.password();

}

    public Boolean isAdmin() {
   return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        this.isAdmin = admin;
    }
}
