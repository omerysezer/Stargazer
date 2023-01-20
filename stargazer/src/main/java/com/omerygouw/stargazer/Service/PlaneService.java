package com.omerygouw.stargazer.Service;

import com.omerygouw.stargazer.Entity.UserLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlaneService {
    @Autowired
    UserLocation userLocation;
}
