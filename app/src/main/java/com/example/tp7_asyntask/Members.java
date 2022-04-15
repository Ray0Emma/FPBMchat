package com.example.tp7_asyntask;

import java.io.Serializable;

public class Members {

    private String name;
    private String email;
    private String profile;



    public Members(String name)  {
        this.name = name;

    }

    public Members(String name,String profile) {
        this.name= name;
        this.profile = profile;
    }

    public Members(String name,String email,String profile) {
        this.name= name;
        this.email = email;
        this.profile = profile;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
