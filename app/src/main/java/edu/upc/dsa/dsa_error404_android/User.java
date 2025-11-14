package edu.upc.dsa.dsa_error404_android;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    private String id;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("vidaInicial")
    private int vidaInicial;

    @SerializedName("monedas")
    private int monedas;

    public User(String name, String password) {
        this.username = name;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return username;
    }

    public int getVidaInicial() {
        return vidaInicial;
    }

    public int getMonedas() {
        return monedas;
    }
}