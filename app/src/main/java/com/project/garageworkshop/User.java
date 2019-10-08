package com.project.garageworkshop;

public class User {
    private String name;
    private String surname;
    private  String ID;
    private int role;

    public User() {}

    public User(String name, String surname, String ID, int role) {
        this.name = name;
        this.surname = surname;
        this.ID = ID;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getID() {
        return ID;
    }

    public int getRole() {
        return role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
