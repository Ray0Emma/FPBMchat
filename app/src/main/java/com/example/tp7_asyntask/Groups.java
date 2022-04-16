package com.example.tp7_asyntask;

public class Groups {

    private String name;
    private String icon;
    private String last_msg;

    public Groups(String name,String icon) {
        this.name = name;
        this.icon = icon;
    }
    public Groups(String name,String icon,String last_msg) {
        this.name = name;
        this.icon = icon;
        this.last_msg = last_msg;
    }
    public Groups(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public String getLast_msg() {
        return last_msg;
    }

    public void setLast_msg(String last_msg) {
        this.last_msg = last_msg;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
