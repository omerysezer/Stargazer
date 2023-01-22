import adafruit_bno055
import board


class IMUController:
    def __init__(self):
        self.magnet_declination = None
        self.magnet_declination_is_set = False
        self.i2c = board.I2C()
        self.sensor = adafruit_bno055.BNO055_I2C(self.i2c)
        self.sensor.mode = adafruit_bno055.COMPASS_MODE

    def is_facing_north(self):
        if not self.magnet_declination_is_set:
            raise RuntimeError("Magnetic declination has not been set. True north cannot be found.")

        return 359 <= (self.sensor.euler[0] + self.magnet_declination) % 360 <= 1

    def is_level(self):
        def value_is_in_margin(acceptable_error, expected_value, real_value):
            return (expected_value - acceptable_error) <= real_value <= (expected_value + acceptable_error)

        gravity_vector = self.sensor.gravity
        error = .3
        return value_is_in_margin(error, 0, gravity_vector[0]) and value_is_in_margin(error, 0, gravity_vector[1]) and value_is_in_margin(error, 9.8, gravity_vector[2])

    def accel_is_calibrated(self):
        return self.sensor.calibration_status[2] == 3

    def magnet_is_calibrated(self):
        return self.sensor.calibration_status[3] == 3

    def set_magnetic_declination(self, declination):
        self.magnet_declination = declination
        self.magnet_declination_is_set = True


