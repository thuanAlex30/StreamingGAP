package com.fpt.StreamGAP.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name ="user")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer user_id;
    private String username;
    private String email;
    private String password;
    private String avatar_url;
    @Column(name = "login_provider")
    private String loginProvider;
    @Column(name = "provider_id")
    private String providerId;
    private String role;
    private String subscription_type;
    private Date created_at;
    private Date updated_at;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    private Date subscriptionExpirationDate;


    private boolean enabled;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return username;
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