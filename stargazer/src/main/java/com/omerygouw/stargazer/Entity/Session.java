package com.omerygouw.stargazer.Entity;

import com.omerygouw.stargazer.DTO.LocationCoordinates;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash("Session")
@Data
@ToString
public class Session implements Serializable {
    @Id
    @Indexed
    private String sessionId;
    private LocationCoordinates coordinates;
    private double magneticDeclination;
    private LocalDateTime timeLastCheckedPlane;
    private boolean lastPlaneCheckResult;
}
