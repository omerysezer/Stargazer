package com.omerygouw.stargazer.Entity;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class LocationCoordinates {
        private final double longitude;
        private final double latitude;
}
