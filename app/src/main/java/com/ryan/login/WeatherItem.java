package com.ryan.login;

public class WeatherItem {
    public String waktu;
    public String suhu;
    public String main;      // Status utama (contoh: Clouds)
    public String deskripsi; // Detail (contoh: scattered clouds)
    public String icon;

    public WeatherItem(String waktu, String suhu, String main, String deskripsi, String icon) {
        this.waktu = waktu;
        this.suhu = suhu;
        this.main = main;
        this.deskripsi = deskripsi;
        this.icon = icon;
    }
}