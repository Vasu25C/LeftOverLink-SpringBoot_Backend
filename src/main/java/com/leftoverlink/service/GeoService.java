package com.leftoverlink.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeoService {

    private final RestTemplate restTemplate = new RestTemplate();
/*
    public String[] getCoordinates(String location) {
        String url = "https://nominatim.openstreetmap.org/search?q=" + location + "&format=json";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "SpringBootApp");  // Required by OSM

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        JSONArray jsonArray = new JSONArray(response.getBody());

        if (!jsonArray.isEmpty()) {
            JSONObject first = jsonArray.getJSONObject(0);
            return new String[]{ first.getString("lat"), first.getString("lon") };
        }

        return null;
    }*/
    public String[] getCoordinates(String location) {
        try {
            String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
            String url = "https://nominatim.openstreetmap.org/search?q=" + encodedLocation + "&format=json";

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "SpringBootApp");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JSONArray jsonArray = new JSONArray(response.getBody());

            if (!jsonArray.isEmpty()) {
                JSONObject first = jsonArray.getJSONObject(0);
                return new String[] {
                    first.getString("lat"),
                    first.getString("lon")
                };
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching coordinates: " + e.getMessage());
        }

        return null;
    }

    
    public double calculateDistance(String lat1, String lon1, String lat2, String lon2) {
        double lat1d = Double.parseDouble(lat1);
        double lon1d = Double.parseDouble(lon1);
        double lat2d = Double.parseDouble(lat2);
        double lon2d = Double.parseDouble(lon2);

        final int EARTH_RADIUS = 6371; // km

        double dLat = Math.toRadians(lat2d - lat1d);
        double dLon = Math.toRadians(lon2d - lon1d);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1d)) * Math.cos(Math.toRadians(lat2d))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

}

