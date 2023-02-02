package com.omerygouw.stargazer.DTO;

import lombok.Builder;

@Builder
public record ObjectCoordinates(Double azimuth, Double altitude) {
}
