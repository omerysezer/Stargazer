package com.omerygouw.stargazer.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class PointToObjectWrapper {
    private String objectName;
    private boolean isInsideSolarSystem;
}
