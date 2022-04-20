package com.example.tp7_asyntask.activities;

import org.jetbrains.annotations.Nullable;

public class User {
    public String firstname;
    public String lastname;
    public String username;
    public String image;

    public User()
    {

    }

    public User(String username, String firstname, String lastname, String image)
    {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.image = image;
    }
}
