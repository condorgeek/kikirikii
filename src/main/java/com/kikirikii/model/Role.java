package com.kikirikii.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "roles")
public class Role {

    public enum Type {
        USER, SUPERUSER, ADMIN;
        public String authority() {
            return "ROLE_" + this.name();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;

    private Role() {
    }

    public static Role of(Type type) {
        Role role = new Role();
        role.type = type;
        return role;
    }

    public static Role of(User user, Type type) {
        Role role = new Role();
        role.type = type;
        role.user = user;
        return role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getAuthority() {
        return type.authority();
    }
}
