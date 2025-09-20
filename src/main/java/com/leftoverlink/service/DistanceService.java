package com.leftoverlink.service;



import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DistanceService {

    @Value("${openrouteservice.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public double calculateDistance(String fromLat, String fromLon, String toLat, String toLon) {
        String url = "https://api.openrouteservice.org/v2/matrix/driving-car";

        String body = String.format("""
        {
          "locations": [
            [%s, %s],
            [%s, %s]
          ],
          "metrics": ["distance"]
        }
        """, fromLon, fromLat, toLon, toLat);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        JSONObject json = new JSONObject(response.getBody());
        return json.getJSONArray("distances").getJSONArray(0).getDouble(1);  // in meters
    }
}
