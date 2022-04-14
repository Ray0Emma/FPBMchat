package com.example.tp7_asyntask;

import java.io.Serializable;

public class Members implements Serializable {

    private String userName;
    private String userEmail;



    public Members(String userName, String userEmail)  {
        this.userName= userName;
        this.userEmail = userEmail;

    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
