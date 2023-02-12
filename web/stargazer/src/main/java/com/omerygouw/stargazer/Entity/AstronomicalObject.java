package com.omerygouw.stargazer.Entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class AstronomicalObject {
    private final String name;
    private final double rightAscension;
    private final double declination;

    // azimuth/altitude are integer values instead of doubles because
    // raspberry pi servos can only turn to the nearest degree
    // and raspberry pi is instructed to point to azimuth/altitude values
    private final Integer azimuth;
    private final Integer altitude;
}
