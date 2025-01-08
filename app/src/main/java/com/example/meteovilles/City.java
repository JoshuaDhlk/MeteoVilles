package com.example.meteovilles;

public class City {
    private String name;
    private String temperature;
    private String weatherDescription;  // Champ ajouté pour la description du temps

    public City(String name, String temperature) {
        this.name = name;
        this.temperature = temperature;
        this.weatherDescription = "Inconnu";  // Valeur par défaut
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    @Override
    public String toString() {
        return name + " - " + temperature + " - " + weatherDescription;
    }
}
