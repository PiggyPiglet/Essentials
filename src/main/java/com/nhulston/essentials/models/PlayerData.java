package com.nhulston.essentials.models;

import java.util.HashMap;
import java.util.Map;

public class PlayerData {
    private final Map<String, Home> homes;

    public PlayerData() {
        this.homes = new HashMap<>();
    }

    public Map<String, Home> getHomes() {
        return homes;
    }

    public Home getHome(String name) {
        return homes.get(name.toLowerCase());
    }

    public void setHome(String name, Home home) {
        homes.put(name.toLowerCase(), home);
    }

    public void deleteHome(String name) {
        homes.remove(name.toLowerCase());
    }

    public int getHomeCount() {
        return homes.size();
    }
}
