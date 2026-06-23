package com.ryan.login;

public class ForexModel {
    private String code;
    private String name;
    private String rate;

    public ForexModel(String code, String name, String rate) {
        this.code = code;
        this.name = name;
        this.rate = rate;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getRate() { return rate; }
}