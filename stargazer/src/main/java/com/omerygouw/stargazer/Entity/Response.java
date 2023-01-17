package com.omerygouw.stargazer.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Response {
    private String response;
    private Boolean InsideSolarSystem;
}
