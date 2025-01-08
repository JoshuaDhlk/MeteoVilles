package com.example.meteovilles;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText cityInput;
    private Button addCityButton, refreshButton, fetchWeatherButton;
    private ListView cityListView;
    private TextView weatherResult;

    private List<City> cities;
    private CityAdapter cityAdapter;
    private CitiesManager citiesManager;
    private OkHttpClient client;

    private static final String API_KEY = "1e37fda098c9630ae6e99e502850a573";  // Remplacez par votre clé API OpenWeatherMap
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityInput = findViewById(R.id.cityInput);
        addCityButton = findViewById(R.id.addCityButton);
        refreshButton = findViewById(R.id.refreshButton);
        fetchWeatherButton = findViewById(R.id.fetchWeatherButton);
        weatherResult = findViewById(R.id.weatherResult);
        cityListView = findViewById(R.id.cityListView);

        citiesManager = new CitiesManager(this);
        cities = new ArrayList<>();
        cityAdapter = new CityAdapter(this, cities);
        cityListView.setAdapter(cityAdapter);

        client = new OkHttpClient();  // Initialisation d'OkHttpClient

        // Charger les villes depuis le fichier
        try {
            loadCities();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Ajouter une ville
        addCityButton.setOnClickListener(v -> {
            String cityName = cityInput.getText().toString().trim();
            if (!cityName.isEmpty()) {
                try {
                    citiesManager.saveCity(cityName);  // Sauvegarder la ville dans le fichier
                    loadCities();  // Recharger les villes
                    cityInput.setText("");  // Réinitialiser l'EditText
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Veuillez entrer un nom de ville", Toast.LENGTH_SHORT).show();
            }
        });

        // Rafraîchir la météo de toutes les villes
        refreshButton.setOnClickListener(v -> {
            // Rafraîchir la météo de toutes les villes
            for (City city : cities) {
                fetchWeatherForCity(city);  // Récupérer la météo pour chaque ville
            }
            cityAdapter.notifyDataSetChanged();  // Rafraîchir l'affichage de la liste
        });

        // Récupérer la météo pour toutes les villes
        fetchWeatherButton.setOnClickListener(v -> {
            for (City city : cities) {
                fetchWeatherForCity(city);  // Récupérer la météo pour chaque ville
            }
        });

        // Détection du clic long pour supprimer une ville
        cityListView.setOnItemLongClickListener((parent, view, position, id) -> {
            // Supprimer la ville après un clic long
            City cityToRemove = cities.get(position);
            try {
                // Supprimer la ville du fichier
                citiesManager.removeCity(cityToRemove.getName());
                // Supprimer la ville de la liste
                cities.remove(position);
                // Rafraîchir l'affichage de la liste
                cityAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Ville supprimée", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Erreur lors de la suppression de la ville", Toast.LENGTH_SHORT).show();
            }
            return true;  // Indique que l'événement est consommé
        });
    }

    private void loadCities() throws IOException {
        List<String> cityNames = citiesManager.getCities();
        cities.clear();
        for (String cityName : cityNames) {
            cities.add(new City(cityName, "N/A"));
        }
        cityAdapter.updateCities(cities);  // Mettre à jour l'adaptateur avec la nouvelle liste de villes
    }

    private void fetchWeatherForCity(City city) {
        String url = BASE_URL + city.getName() + "&appid=" + API_KEY + "&units=metric&lang=fr"; // URL avec la clé API et la ville
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();

                    // Analyser la réponse JSON
                    JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                    // Extraire la température (en °C)
                    String temperature = jsonObject.getAsJsonObject("main").get("temp").getAsString() + "°C";

                    // Extraire la description du temps
                    JsonArray weatherArray = jsonObject.getAsJsonArray("weather");
                    String description = weatherArray.get(0).getAsJsonObject().get("description").getAsString();

                    // Mettre à jour les données de la ville
                    city.setTemperature(temperature);
                    city.setWeatherDescription(description);

                    // Rafraîchir l'affichage
                    runOnUiThread(() -> cityAdapter.notifyDataSetChanged());
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Erreur lors de la récupération de la météo", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
