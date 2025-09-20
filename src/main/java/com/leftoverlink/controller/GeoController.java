package com.leftoverlink.controller;



import com.leftoverlink.service.DistanceService;
import com.leftoverlink.service.GeoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/geo")
public class GeoController {

    @Autowired
    private GeoService geoService;

    @Autowired
    private DistanceService distanceService;

    @GetMapping("/coordinates")
    public String[] getCoordinates(@RequestParam String location) {
        return geoService.getCoordinates(location);
    }

    @GetMapping("/distance")
    public String getDistance(@RequestParam String from, @RequestParam String to) {
        String[] fromCoord = geoService.getCoordinates(from);
        String[] toCoord = geoService.getCoordinates(to);

        if (fromCoord == null || toCoord == null) {
            return "Invalid location(s)";
        }

        double distanceMeters = distanceService.calculateDistance(fromCoord[0], fromCoord[1], toCoord[0], toCoord[1]);
        double distanceKm = distanceMeters / 1000.0;
        return String.format("Distance from '%s' to '%s' is %.2f km", from, to, distanceKm);
    }
}
