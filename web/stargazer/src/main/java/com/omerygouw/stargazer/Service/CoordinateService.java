package com.omerygouw.stargazer.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.omerygouw.stargazer.DTO.LocationCoordinates;
import com.omerygouw.stargazer.DTO.ObjectToPointAt;
import com.omerygouw.stargazer.Entity.AstronomicalObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.*;

@Service
public class CoordinateService {
        private final Set<String> solarSystemObjectTypes;
        private final Set<String> extraSolarSystemObjectTypes;

        public CoordinateService(){
                this.solarSystemObjectTypes = new HashSet<>(Arrays.asList(
                        "PLANET",
                        "COMET",
                        "ASTEROID"
                ));

               this.extraSolarSystemObjectTypes = new HashSet<>(Arrays.asList(
                        "STAR",
                        "GALAXY"
                ));
        }

        private Map<String, Double> findCoordinatesOfExtraSolarObject(String name, String id) throws RuntimeException{
                Map<String, Double> coords = new HashMap<String, Double>();
                String requestUri = "http://simbad.u-strasbg.fr/simbad/sim-tap/sync?request=doQuery&lang=adql&format=csv&query=SELECT RA, DEC FROM basic JOIN ident ON oidref = oid WHERE id = '" + id + "'";

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

                String response = null;
                try{
                        response = responseSpec.bodyToMono(String.class).block();
                }catch (Exception e){
                        throw new RuntimeException("Received error response from SIMBAD API. This may be due to an apostrophe in the requested object name.\n" +
                                "Please try removing any apostrophes or using the objects catalogue ID instead.");
                }


                if(response == null){
                        throw new RuntimeException("Received no response from SIMBAD API.");
                }

                String[] responseSplitByLine = response.split("\n");

                if(responseSplitByLine.length == 1) {
                        throw new RuntimeException("Could not find an object named \"" + name + "\" with identifier \"" + id + "\".");
                }

                String[] coordinates = responseSplitByLine[1].split(",");
                double rightAscension = Double.parseDouble(coordinates[0]);
                double declination = Double.parseDouble(coordinates[1]);

                coords.put("Right Ascension", rightAscension);
                coords.put("Declination", declination);
                return coords;
        }

        public Map<String, Double> findCoordinatesOfSolarObject(String id) throws RuntimeException, URISyntaxException {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss.SS");
                LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
                LocalDateTime nowPlusFiveMinutes = now.plusMinutes(5);

                String currentTime = dtf.format(now);
                String currentTimePlusFiveMinutes = dtf.format(nowPlusFiveMinutes);

                String requestUri = "https://ssd.jpl.nasa.gov/api/horizons.api?format=text&ANG_FORMAT='DEG'&COMMAND=" + id + "&QUANTITIES='1'&START_TIME='" + currentTime + "'&STOP_TIME='" + currentTimePlusFiveMinutes + "'";
                requestUri = requestUri.replaceAll(" ", "%20")
                        .replaceAll(";", "%3B")
                        .replaceAll("'", "%27");

                URI uri = new URI(requestUri);
                WebClient client = WebClient.create();
                ResponseSpec responseSpec = client.get()
                        .uri(uri)
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

        private Map<String, Integer> convertRightAscensionAndDeclinationToAzimuthAndAltitude(double rightAscension, double declination, double longitude, double latitude) throws RuntimeException{
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss:SS");
                LocalDateTime datetime = LocalDateTime.now(ZoneOffset.UTC);
                LocalDateTime jan1st2000 = LocalDateTime.parse("2000-01-01 00:00:00:00", dtf);

                double daysSinceJan1st2000 = (double) Duration.between(jan1st2000, datetime).toHours() / 24;
                double currentTimeInHours = (double) datetime.toLocalTime().toSecondOfDay() / 3600;

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

                // rounding azimuth and altitude to the nearest integer because
                // raspberry pi servos can only turn to the nearest whole number degree
                Map<String, Integer> convertedCoords = new HashMap<>();
                convertedCoords.put("Azimuth", (int) Math.round(azimuth));
                convertedCoords.put("Altitude", (int) Math.round(altitude));

                return convertedCoords;
        }

        public String getIdOfSolarObjectType(String objectName, String objectType) throws URISyntaxException {
                String requestUri = "https://ssd-api.jpl.nasa.gov/sbdb.api?sstr=" + objectName + "&no-orbit=1";

                 requestUri = requestUri.replaceAll(" ", "%20")
                        .replaceAll(";", "%3B")
                        .replaceAll("'", "%27");

                URI uri = new URI(requestUri);
                WebClient client = WebClient.create();
                ResponseSpec responseSpec = client.get()
                        .uri(uri)
                        .retrieve();

                String response = responseSpec.bodyToMono(String.class).block();
                if(response == null){
                        throw new RuntimeException("Did not get response from JPL Horizons Small Body Lookup API.");
                }

                JsonObject objectDetails = JsonParser.parseString(response).getAsJsonObject();
                if(objectDetails.has("list")){
                        StringBuilder firstThreeMatches = new StringBuilder();
                        JsonArray matchList = objectDetails.get("list").getAsJsonArray();
                        for(int i = 0; i < matchList.size() && i < 3; i++){
                                firstThreeMatches.append(matchList.get(i).getAsJsonObject().get("name").getAsString()).append("\n");
                        }
                        throw new RuntimeException("Could not find an unambiguous match for small-body object with name: " + objectName +
                                ".\nFirst few matches:\n" + firstThreeMatches.toString());
                }

                if(objectDetails.has("code")){
                        throw new RuntimeException("Could not find any matches for a small-body object with name: " + objectName);
                }

                String objectDesignation = objectDetails.get("object").getAsJsonObject().get("des").getAsString();

                if (objectType.equals("ASTEROID")) {
                        return "'" + objectDesignation + ";'";
                }
                else if(objectType.equals("COMET")){
                        return "'DES=" + objectDesignation + ";NOFRAG;CAP;'";
                }

                throw new RuntimeException("Cannot search for an object of type: \"" + objectType + ".\"");
        }

        public AstronomicalObject findObjectCoordinates(ObjectToPointAt object, LocationCoordinates userCoordinates) throws RuntimeException{
                Map<String, Double> absoluteCoords;

                String name = object.objectName();
                String id = object.objectId();
                try{
                        if(solarSystemObjectTypes.contains(object.objectType())){
                                if(object.objectId().equals("UNKNOWN")){
                                        id = getIdOfSolarObjectType(object.objectName(), object.objectType());
                                }

                                absoluteCoords = findCoordinatesOfSolarObject(id);
                        }
                        else if(extraSolarSystemObjectTypes.contains(object.objectType())){
                                if(object.objectId().equals("UNKNOWN")){
                                        id = name;
                                }
                                absoluteCoords = findCoordinatesOfExtraSolarObject(name, id);
                        }
                        else {
                                throw new RuntimeException("Cannot point to object type: " + object.objectType());
                        }
                } catch (Exception e){
                        throw new RuntimeException(e.getMessage());
                }

                Map<String, Integer> relativeCoords = convertRightAscensionAndDeclinationToAzimuthAndAltitude(absoluteCoords.get("Right Ascension"),
                        absoluteCoords.get("Declination"), userCoordinates.longitude(), userCoordinates.latitude());

                AstronomicalObject returnObject = AstronomicalObject.builder()
                        .name(object.objectName())
                        .rightAscension(absoluteCoords.get("Right Ascension"))
                        .declination(absoluteCoords.get("Declination"))
                        .azimuth(relativeCoords.get("Azimuth"))
                        .altitude(relativeCoords.get("Altitude"))
                        .build();

                if(0 <= returnObject.getAltitude() && returnObject.getAltitude() <= 10){
                        throw new RuntimeException("Object with identifier \"" + returnObject.getName() + "\"" +
                                " is too close to the horizon for safe usage of the laser. Please try a different object.");
                }

                if(returnObject.getAltitude() < 0){
                        throw new RuntimeException("Object with identifier \"" + returnObject.getName() + "\"" +
                                " is currently not in the sky. Please try a different object.");
                }

                return returnObject;
        }
}
