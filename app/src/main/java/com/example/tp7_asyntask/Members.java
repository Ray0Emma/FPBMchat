package com.example.tp7_asyntask;

import java.io.Serializable;

public class Members {

    private String name;
    private String email;



    public Members(String name, String email)  {
        this.name= name;
        this.email = email;

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
