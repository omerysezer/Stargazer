package com.omerygouw.stargazer;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.DoubleBuffer;

@Getter
@Setter
@Builder
@ToString
public class AstronomicalObject {
    private final String name;
    private final double rightAscension;
    private final double declination;

    private final double azimuth;
    private final double altitude;
}
