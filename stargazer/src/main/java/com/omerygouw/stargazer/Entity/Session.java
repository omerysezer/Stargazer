package com.omerygouw.stargazer.Entity;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash("Session")
@Data
@ToString
public class Session implements Serializable {
    @Id
    private String clientSessionId;
    private String piSessionId;
    private LocationCoordinates coordinates;
    private double magneticDeclination;
    private LocalDateTime timeLastCheckedPlane;
    private boolean lastPlaneCheckResult;
}
