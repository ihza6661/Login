package com.ryan.login;

public class MahasiswaModel {
    private String nim;
    private String nama;
    private String jenisKelamin;
    private String jurusan;

    public MahasiswaModel(String nim, String nama, String jenisKelamin, String jurusan) {
        this.nim = nim;
        this.nama = nama;
        this.jenisKelamin = jenisKelamin;
        this.jurusan = jurusan;
    }

    public String getNim() { return nim; }
    public String getNama() { return nama; }
    public String getJenisKelamin() { return jenisKelamin; }
    public String getJurusan() { return jurusan; }
}