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
    private final double azimuth;
    private final double altitude;
}
