package com.example.meteovilles;

import android.content.Context;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class CitiesManager {

    private Context context;
    private static final String FILE_NAME = "cities.txt";  // Fichier texte pour stocker les villes

    public CitiesManager(Context context) {
        this.context = context;
    }

    // Méthode pour ajouter une ville dans le fichier
    public void saveCity(String cityName) throws IOException {
        List<String> cities = getCities();  // Récupérer les villes existantes
        cities.add(cityName);  // Ajouter la nouvelle ville
        saveCities(cities);  // Sauvegarder la liste mise à jour
    }

    // Méthode pour supprimer une ville dans le fichier
    public void removeCity(String cityName) throws IOException {
        List<String> cities = getCities();
        if (cities.contains(cityName)) {
            cities.remove(cityName);  // Supprimer la ville
            saveCities(cities);  // Sauvegarder la liste mise à jour
        }
    }

    // Méthode pour récupérer la liste des villes depuis le fichier
    public List<String> getCities() throws IOException {
        List<String> cities = new ArrayList<>();
        try (FileInputStream fis = context.openFileInput(FILE_NAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader reader = new BufferedReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                cities.add(line.trim());  // Ajouter chaque ville à la liste
            }
        }
        return cities;
    }

    // Méthode pour sauvegarder la liste des villes dans le fichier
    public void saveCities(List<String> cities) throws IOException {
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
             OutputStreamWriter osw = new OutputStreamWriter(fos);
             BufferedWriter writer = new BufferedWriter(osw)) {
            for (String city : cities) {
                writer.write(city);
                writer.newLine();
            }
        }
    }
}
