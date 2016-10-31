package com.infinityworks.webapp.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken extends BaseEntity {
    private String token;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private User user;

    @Convert(converter = PersistentLocalDateTime.class)
    private LocalDateTime expires;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpires() {
        return expires;
    }

    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
    }
}
