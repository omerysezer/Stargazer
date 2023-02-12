package com.omerygouw.stargazer.DTO;

public enum Status {
    SUCCESS,
    POINT_TO_FAILURE,
    LASER_ON_FAILURE,
    LASER_OFF_FAILURE,
    LOCATION_SAVE_FAILURE,
    PAIRING_FAILURE,
    GET_SESSION_ID_FAILURE,
    CALIBRATION_WARNING,
    LEVEL_WARNING,
    ORIENTATION_WARNING,
    CALIBRATION_OK,
    LEVEL_OK,
    ORIENTATION_OK,
    PI_DISCONNECT
}