package com.example.myapplication;

public class Contact {
    private String name;
    private String phone;
    private int imageResource;

    public Contact(String name, String phone, int imageResource) {
        this.name = name;
        this.phone = phone;
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public int getImageResource() {
        return imageResource;
    }
}
