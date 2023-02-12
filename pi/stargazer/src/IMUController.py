import adafruit_bno055
import board


class IMUController:
    def __init__(self):
        self.i2c = board.I2C()
        self.sensor = adafruit_bno055.BNO055_I2C(self.i2c)
        self.sensor.mode = adafruit_bno055.COMPASS_MODE

    def get_gravity(self):
        return self.sensor.gravity

    def get_magnetic_heading(self):
        return self.sensor.euler[0]

    def accel_is_calibrated(self):
        return self.sensor.calibration_status[2] == 3

    def magnet_is_calibrated(self):
        return self.sensor.calibration_status[3] == 3
