package com.example.myapplication;

import java.io.Serializable;

public class Student implements Serializable {
    private String id;
    private String name;
    private String className;
    private double gpa;
    private int imageResource;

    public Student(String id, String name, String className, double gpa, int imageResource) {
        this.id = id;
        this.name = name;
        this.className = className;
        this.gpa = gpa;
        this.imageResource = imageResource;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getClassName() { return className; }
    public double getGpa() { return gpa; }
    public int getImageResource() { return imageResource; }
}
