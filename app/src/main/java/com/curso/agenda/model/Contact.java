package com.curso.agenda.model;

import android.location.Location;

import java.io.Serializable;

public class Contact implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String name;
    private String phone;
    private String email;
    private String image;
    private String addres;
    private Double locX;
    private Double locY;

    public Contact(String name, String phone, String email, String image, String address, Double locX, Double locY) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.image = image;
        this.addres = address;
        this.locX = locX;
        this.locY = locY;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddres() {
        return addres;
    }

    public void setAddres(String addres) {
        this.addres = addres;
    }

    public Double getLocX() {
        return locX;
    }

    public void setLocX(Double locX) {
        this.locX = locX;
    }

    public Double getLocY() {
        return locY;
    }

    public void setLocY(Double locY) {
        this.locY = locY;
    }
}
