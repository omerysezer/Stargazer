# <center> Stargazer: Your Guide to The Sky </center>

<p align = "center">
<img src="readme-resources/stargazer_main_page.png" title="Galaxies Page of Website" width="90%" height="90%"/>
</p>

## Overview
Stargazer is a project done in collaboration between Omer Sezer and Georges Ouweijan during the winter break between the Fall 2022 and Spring 2023 semesters.
It allows users to go to a website, pair with a Raspberry Pi controlling two servo motors and a laser, and instruct it to point at any object in the night sky from stars to planets to asteroids to galaxies.

The server was written using the Java Spring framework, while the website was created with vanilla JavaScript, HTML, and CSS, and the Raspberry Pi ran Python.

When a user searches or clicks on an astronomical body they wish to see, the website sends a message containing the name and id (if it is known) of the object to the server via a WebSocket.
The server then searches for the coordinates of that object using the JPL Horizons API for bodies within our solar system, or SIMBAD API for extrasolar objects, and converts those absolute coordinates into the coordinates in the sky for the user's location.
Then the server instructs the Raspberry Pi to point to those coordinates and the magic happens!

The device is able to point to an object within a few degrees, enabling users to understand where everything is in the sky!

The server rejects requests to point to objects if it has seen any aircraft within the past five minutes, improving safety.

## Use Images
<table>
<tr>
<td><img alt="Stargazer pointing to Merak in Ursa Major" src="readme-resources/big_dipper.jpg" title="Stargazer pointing to Merak in Ursa Major" width="100%"/></td>
<td><img alt="Stargazer pointing to Alnilam in Orion" src="readme-resources/orion.jpg" title="Stargazer pointing to Alnilam in Orion" width="100%"/></td>
<td><img alt="Stargazer being setup outside for a demo" src="readme-resources/stargazer_in_field.jpg" title="Stargazer being setup outside for a demo" width="100%"/></td>
<td><img alt="Stargazer pointing to a star which we forgot to write down, and we can't tell from the picture" src="readme-resources/unkown_star.jpg" title="Stargazer pointing to a star which we forgot to write down, and we can't tell from the picture" width="100%"/></td>
</tr>
</table>

## Website GIFs

### Site Tour
![](/readme-resources/tour.gif)
<br>
### Site Usage
![](/readme-resources/planets.gif)

## Code Tree
```
README.md
pi
   |-- .gitignore
   |-- Jenkinsfile
   |-- stargazer
   |   |-- requirements.txt
   |   |-- src
   |   |   |-- CalibOrientWarning.py
   |   |   |-- IMUController.py
   |   |   |-- NetworkHandler.py
   |   |   |-- ServerCommunication.py
   |   |   |-- ServoLaserController.py
   |   |   |-- main.py
readme-resources
   |-- asteroids.gif
   |-- big_dipper.jpg
   |-- comets.gif
   |-- galaxies.gif
   |-- orion.jpg
   |-- planets.gif
   |-- stargazer_in_field.jpg
   |-- stargazer_main_page.png
   |-- stars.gif
   |-- tour.gif
   |-- unkown_star.jpg
web
   |-- JenkinsfileCertificates
   |-- JenkinsfileStargazer
   |-- docker-compose-certificates.yml
   |-- docker-compose-stargazer.yml
   |-- docker-files
   |   |-- certificates
   |   |   |-- certificates.conf
   |   |   |-- dockerfile_nginx
   |   |-- stargazer
   |   |   |-- dockerfile_nginx
   |   |   |-- dockerfile_server
   |   |   |-- stargazer.conf
   |-- stargazer
   |   |-- .gitignore
   |   |-- .mvn
   |   |   |-- wrapper
   |   |   |   |-- maven-wrapper.jar
   |   |   |   |-- maven-wrapper.properties
   |   |-- mvnw
   |   |-- mvnw.cmd
   |   |-- pom.xml
   |   |-- src
   |   |   |-- main
   |   |   |   |-- java
   |   |   |   |   |-- com
   |   |   |   |   |   |-- omerygouw
   |   |   |   |   |   |   |-- stargazer
   |   |   |   |   |   |   |   |-- Config
   |   |   |   |   |   |   |   |   |-- RedisConfig.java
   |   |   |   |   |   |   |   |   |-- WebSocketConfiguration.java
   |   |   |   |   |   |   |   |-- Controller
   |   |   |   |   |   |   |   |   |-- RPiCommunication.java
   |   |   |   |   |   |   |   |   |-- WebSocketController.java
   |   |   |   |   |   |   |   |-- DTO
   |   |   |   |   |   |   |   |   |-- FromPiToServerMessage.java
   |   |   |   |   |   |   |   |   |-- FromServerToPiMessage.java
   |   |   |   |   |   |   |   |   |-- LocationCoordinates.java
   |   |   |   |   |   |   |   |   |-- Message.java
   |   |   |   |   |   |   |   |   |-- ObjectCoordinates.java
   |   |   |   |   |   |   |   |   |-- ObjectToPointAt.java
   |   |   |   |   |   |   |   |   |-- SessionIdWrapper.java
   |   |   |   |   |   |   |   |   |-- Status.java
   |   |   |   |   |   |   |   |-- Entity
   |   |   |   |   |   |   |   |   |-- AstronomicalObject.java
   |   |   |   |   |   |   |   |   |-- RPiConnection.java
   |   |   |   |   |   |   |   |   |-- Session.java
   |   |   |   |   |   |   |   |-- Repository
   |   |   |   |   |   |   |   |   |-- SessionRepository.java
   |   |   |   |   |   |   |   |-- Service
   |   |   |   |   |   |   |   |   |-- CoordinateService.java
   |   |   |   |   |   |   |   |   |-- MagneticDeclinationService.java
   |   |   |   |   |   |   |   |   |-- PiToWebBridgeService.java
   |   |   |   |   |   |   |   |   |-- PlaneService.java
   |   |   |   |   |   |   |   |   |-- SessionManagerService.java
   |   |   |   |   |   |   |   |   |-- WebToPiBridgeService.java
   |   |   |   |   |   |   |   |-- StargazerApplication.java
   |   |   |   |-- resources
   |   |   |   |   |-- application.properties
   |   |   |   |   |-- static
   |   |   |   |   |   |-- asteroid.html
   |   |   |   |   |   |-- comet.html
   |   |   |   |   |   |-- css
   |   |   |   |   |   |   |-- mainPages.css
   |   |   |   |   |   |   |-- search.css
   |   |   |   |   |   |   |-- welcomePages.css
   |   |   |   |   |   |-- galaxy.html
   |   |   |   |   |   |-- images
   |   |   |   |   |   |   |-- asteroid.jpg
   |   |   |   |   |   |   |-- background.jpg
   |   |   |   |   |   |   |-- comet.jpg
   |   |   |   |   |   |   |-- galaxy.jpg
   |   |   |   |   |   |   |-- planet.jpg
   |   |   |   |   |   |   |-- solarSystem.jpg
   |   |   |   |   |   |-- stargazer.html
   |   |   |-- test
   |   |   |   |-- java
   |   |   |   |   |-- com
   |   |   |   |   |   |-- omerygouw
   |   |   |   |   |   |   |-- stargazer
   |   |   |   |   |   |   |   |-- StargazerApplicationTests.java

```