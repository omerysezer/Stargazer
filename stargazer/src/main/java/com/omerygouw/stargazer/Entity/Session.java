package com.omerygouw.stargazer.Entity;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("Session")
@Data
@ToString
public class Session implements Serializable {
    @Id
    private String clientSessionId;
    private String websocketSessionId;
    private String piSessionId;
    private LocationCoordinates coordinates;
    private double magneticDeclination;
    private long timeLastCheckedPlane;
    private boolean lastPlaneCheckResult;
}
