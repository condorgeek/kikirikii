package com.kikirikii.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;

    @NotNull
    private String name;

    @NotNull
    private String password;

    private User(){}

    public static User create(String email, String name, String password) {
        User user = new User();
        user.id = email;
        user.name = name;
        user.password = password;
        return user;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
