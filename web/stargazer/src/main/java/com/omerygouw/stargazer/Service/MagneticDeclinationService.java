package com.omerygouw.stargazer.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.omerygouw.stargazer.DTO.LocationCoordinates;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;

@Service
public class MagneticDeclinationService {

    // magnetic declination refers to the difference between Magnetic North and True (geometric) North
    public double getMagneticDeclinationAtLocation(LocationCoordinates location){
        LocalDate now = LocalDate.now();

        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        int currentDay = now.getDayOfMonth();


        String requestUri = "https://www.ngdc.noaa.gov/geomag-web/calculators/calculateDeclination?"
                + "lat1=" + location.latitude()
                + "&lon1=" + location.longitude()
                + "&startYear=" + currentYear
                + "&startMonth=" + currentMonth
                + "&startDay=" + currentDay
                + "&key=zNEw7"
                + "&resultFormat=json";

        WebClient client = WebClient.create();
        WebClient.ResponseSpec responseSpec = client.get()
                .uri(requestUri)
                .retrieve();



        String response = responseSpec.bodyToMono(String.class).block();

        if(response == null){
            throw new RuntimeException("Did not receive response from NOAA Magnetic Declination API.");
        }

        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
        double magneticDeclination = jsonResponse.get("result").getAsJsonArray().get(0).getAsJsonObject().get("declination").getAsDouble();

        return magneticDeclination;
    }
}
