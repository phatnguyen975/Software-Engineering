package com.example.myapplication;

import java.io.Serializable;

public class HocSinh implements Serializable {
    private String maHS;
    private String tenHS;
    private double dtb;
    private String maLop;

    public HocSinh(String maHS, String tenHS, double dtb, String maLop) {
        this.maHS = maHS;
        this.tenHS = tenHS;
        this.dtb = dtb;
        this.maLop = maLop;
    }

    public String getMaHS() {
        return maHS;
    }

    public void setMaHS(String maHS) {
        this.maHS = maHS;
    }

    public String getTenHS() {
        return tenHS;
    }

    public void setTenHS(String tenHS) {
        this.tenHS = tenHS;
    }

    public double getDtb() {
        return dtb;
    }

    public void setDtb(double dtb) {
        this.dtb = dtb;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }
}
