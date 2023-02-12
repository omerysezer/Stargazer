package com.omerygouw.stargazer.DTO;

import lombok.Builder;

@Builder
public record ObjectCoordinates(Integer azimuth, Integer altitude) {
}
