package com.omerygouw.stargazer.Service;

import com.omerygouw.stargazer.Entity.AstronomicalObject;
import com.omerygouw.stargazer.Entity.LocationCoordinates;
import com.omerygouw.stargazer.Entity.ObjectToPointAt;
import com.omerygouw.stargazer.Entity.UserLocation;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.*;

@Service
public class CoordinateService {
        @Autowired
        UserLocation userLocation;


        private Map<String, Double> findCoordinatesOfExtraSolarObjectByName(String name) throws RuntimeException{
                Map<String, Double> coords = new HashMap<String, Double>();
                String requestUri = "http://simbad.u-strasbg.fr/simbad/sim-tap/sync?request=doQuery&lang=adql&format=csv&query=SELECT RA, DEC FROM basic JOIN ident ON oidref = oid WHERE id = '" + name + "'";

                WebClient client = WebClient.create();
                ResponseSpec responseSpec = client.get()
                        .uri(requestUri)
                        .retrieve();

                /*
                If the object name is valid response is a string in the following format:

                ra,dec\n101.28715533333335,-16.71611586111111\n

                If object name is invalid, response is expected to be:

                ra,dec\n
                 */

                String response = responseSpec.bodyToMono(String.class).block();

                if(response == null){
                        throw new RuntimeException("Received no response from SIMBAD API.");
                }

                String[] responseSplitByLine = response.split("\n");

                if(responseSplitByLine.length == 1) {
                        throw new RuntimeException("Could not find an extrasolar object with identifier \"" + name + "\".");
                }

                String[] coordinates = responseSplitByLine[1].split(",");
                double rightAscension = Double.parseDouble(coordinates[0]);
                double declination = Double.parseDouble(coordinates[1]);

                coords.put("Right Ascension", rightAscension);
                coords.put("Declination", declination);
                return coords;
        }

        private Map<String, Double> findCoordinatesOfSolarObjectByName(String name) throws RuntimeException{
                Map<String, Integer> planetToIdMap = new HashMap<>();
                planetToIdMap.put("Mercury", 199);
                planetToIdMap.put("Venus", 299);
                planetToIdMap.put("Moon", 301);
                planetToIdMap.put("Mars", 499);
                planetToIdMap.put("Jupiter", 599);
                planetToIdMap.put("Saturn", 699);
                planetToIdMap.put("Uranus", 799);
                planetToIdMap.put("Neptune", 899);
                planetToIdMap.put("Pluto", 999);

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss.SS");
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime nowPlusFiveMinutes = LocalDateTime.now().plusMinutes(5);

                String currentTime = dtf.format(now);
                String currentTimePlusFiveMinutes = dtf.format(nowPlusFiveMinutes);

                String requestUri = "https://ssd.jpl.nasa.gov/api/horizons.api?format=text&ANG_FORMAT='DEG'&COMMAND='" + planetToIdMap.get(name) + "'&QUANTITIES='1'&START_TIME='" + currentTime + "'&STOP_TIME='" + currentTimePlusFiveMinutes + "'";

                WebClient client = WebClient.create();
                ResponseSpec responseSpec = client.get()
                        .uri(requestUri)
                        .retrieve();
                String response = responseSpec.bodyToMono(String.class).block();

                if(response == null){
                        throw new RuntimeException("No response from JPL HORIZONS API.");
                }
                if(response.contains("API ERROR")){
                        throw new RuntimeException("Received error message from JPL HORIZONS API.");
                }

                Pattern pattern = Pattern.compile("\\$\\$SOE(.*?)\\$\\$EOE", Pattern.DOTALL);
                Matcher matcher = pattern.matcher(response);

                if(!matcher.find()){
                    throw new RuntimeException("Could not extract coordinates from JPL HORIZONS API response");
                }

                String coordinatesLine = matcher.group(1);
                coordinatesLine = coordinatesLine.replaceAll("\\s+", " ");      // replaces any groups of spaces with just one space
                String[] lineSplitBySpaces = coordinatesLine.split(" ");

                double declination = Double.parseDouble(lineSplitBySpaces[lineSplitBySpaces.length - 1]);
                double rightAscension = Double.parseDouble(lineSplitBySpaces[lineSplitBySpaces.length - 2]);

                Map<String, Double> coords = new HashMap<>();
                coords.put("Declination", declination);
                coords.put("Right Ascension", rightAscension);

                return coords;
        }

        private Map<String, Double> convertRightAscensionAndDeclinationToAzimuthAndAltitude(double rightAscension, double declination) throws RuntimeException{
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss:SS");
                LocalDateTime datetime = LocalDateTime.now(ZoneOffset.UTC);
                LocalDateTime jan1st2000 = LocalDateTime.parse("2000-01-01 00:00:00:00", dtf);

                double daysSinceJan1st2000 = (double) Duration.between(jan1st2000, datetime).toHours() / 24;
                double currentTimeInHours = (double) datetime.toLocalTime().toSecondOfDay() / 3600;

                LocationCoordinates location;
                try{
                        location = userLocation.getUserLocation();
                }
                catch(Exception e){
                        throw new RuntimeException("Could not convert absolute coordinates to relative coordinates because user location is unknown.");
                }
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();

                double localSiderealTime = (100.46 + 0.985647 * daysSinceJan1st2000 + longitude + 15*currentTimeInHours) % 360;
                double hourAngle = localSiderealTime - rightAscension < 0 ? localSiderealTime - rightAscension + 360 : localSiderealTime - rightAscension;

                double sinOfDeclination = sin(toRadians(declination));
                double sinOfLatitude = sin(toRadians(latitude));
                double sinOfHourAngle = sin(toRadians(hourAngle));
                double cosOfDeclination = cos(toRadians(declination));
                double cosOfLatitude = cos(toRadians(latitude));
                double cosOfHourAngle = cos(toRadians(hourAngle));

                double sinOfAltitude = (sinOfDeclination * sinOfLatitude) + (cosOfDeclination * cosOfLatitude * cosOfHourAngle);
                double altitude = toDegrees(asin(sinOfAltitude));

                double cosOfAltitude = cos(toRadians(altitude));

                double cosOfA = (sinOfDeclination - (sinOfAltitude * sinOfLatitude)) / (cosOfAltitude * cosOfLatitude);
                double A = toDegrees(acos(cosOfA));

                double azimuth = A;
                if(sinOfHourAngle >= 0){
                        azimuth = 360 - A;
                }

                Map<String, Double> convertedCoords = new HashMap<>();
                convertedCoords.put("Azimuth", azimuth);
                convertedCoords.put("Altitude", altitude);

                return convertedCoords;
        }

        public AstronomicalObject findObjectCoordinates(ObjectToPointAt object) throws RuntimeException{
                Map<String, Double> absoluteCoords;

                try {
                        if (object.isInsideSolarSystem()) {
                                absoluteCoords = findCoordinatesOfSolarObjectByName(object.getObjectName());
                        } else {
                                absoluteCoords = findCoordinatesOfExtraSolarObjectByName(object.getObjectName());
                        }
                }
                catch (Exception e){
                        throw new RuntimeException("Could not find coordinates of object with identifier \"" + object.getObjectName() + "\"");
                }

                Map<String, Double> relativeCoords = convertRightAscensionAndDeclinationToAzimuthAndAltitude(absoluteCoords.get("Right Ascension"), absoluteCoords.get("Declination"));

                return AstronomicalObject.builder()
                        .name(object.getObjectName())
                        .rightAscension(absoluteCoords.get("Right Ascension"))
                        .declination(absoluteCoords.get("Declination"))
                        .azimuth(relativeCoords.get("Azimuth"))
                        .altitude(relativeCoords.get("Altitude"))
                        .build();
        }
}