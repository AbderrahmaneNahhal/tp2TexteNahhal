package ma.emsi.nahhal.tp2texte.outil.meteo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;

public class MeteoTool {

    @Tool("""
            Retourne true si la probabilité de pluie dépasse 25 % au moins
            un jour de la période, sinon false.
            La période commence nbJoursDebut jours après aujourd'hui et
            dure nbJours jours, dans la limite des 7 jours de prévision.
            Par exemple, nbJoursDebut = 0 et nbJours = 3 signifie
            aujourd'hui et les deux jours suivants.
            """)
    public boolean isPluiePrevue(
            @P(value = "Latitude", name = "latitude") double latitude,
            @P(value = "Longitude", name = "longitude") double longitude,
            @P(value = "Nombre de jours après aujourd'hui avant le début de la période",
                    name = "nbJoursDebut") int nbJoursDebut,
            @P(value = "Nombre de jours de la période", name = "nbJours") int nbJours) {

        double seuilPluie = 25.0;

        try {
            String baseUrl = "https://api.open-meteo.com/v1/forecast";
            int finPeriode = Math.min(nbJoursDebut + nbJours, 7);
            String apiUri = String.format(Locale.US,
                    "%s?latitude=%f&longitude=%f&daily=precipitation_probability_max&forecast_days=%d",
                    baseUrl, latitude, longitude, finPeriode);

            HttpURLConnection connection =
                    (HttpURLConnection) new URI(apiUri).toURL().openConnection();
            connection.setRequestMethod("GET");

            String response;
            try (Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8)) {
                response = scanner.useDelimiter("\\A").next();
            }

            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            JsonObject daily = jsonResponse.getAsJsonObject("daily");
            int[] precipitationProbabilities =
                    gson.fromJson(daily.get("precipitation_probability_max"), int[].class);

            for (int i = nbJoursDebut; i < finPeriode; i++) {
                if (precipitationProbabilities[i] > seuilPluie) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException(
                    "Erreur lors de la récupération de la météo pour ("
                            + latitude + ", " + longitude + ")", e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Tool("""
            Recherche les coordonnées d'une ville à partir de son nom.
            Retourne un tableau contenant la latitude, puis la longitude.
            Utilise cet outil quand le nom de la ville est connu mais que
            ses coordonnées ne sont pas fournies.
            """)
    public double[] getCoordonnees(
            @P(value = "Nom de la ville à localiser, par exemple Paris", name = "ville")
            String ville) {

        try {
            String baseUrl = "https://geocoding-api.open-meteo.com/v1/search?name=";
            String apiUri = baseUrl + URLEncoder.encode(ville, StandardCharsets.UTF_8);

            HttpURLConnection connection =
                    (HttpURLConnection) new URI(apiUri).toURL().openConnection();
            connection.setRequestMethod("GET");

            String response;
            try (Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8)) {
                response = scanner.useDelimiter("\\A").next();
            }

            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

            if (jsonResponse.has("results")) {
                JsonArray results = jsonResponse.getAsJsonArray("results");

                if (!results.isEmpty()) {
                    JsonObject premierResultat = results.get(0).getAsJsonObject();
                    double latitude = premierResultat.get("latitude").getAsDouble();
                    double longitude = premierResultat.get("longitude").getAsDouble();
                    return new double[]{latitude, longitude};
                }
            }

            throw new RuntimeException("Aucune coordonnée trouvée pour la ville : " + ville);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Erreur lors de la récupération des coordonnées pour la ville : " + ville, e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}