from IMUController import IMUController


class CalibOrientWarning:
    def __init__(self):
        self.imu_controller = IMUController()
        self.magnetic_declination = None

    def is_calibrated(self):
        return self.imu_controller.accel_is_calibrated() and self.imu_controller.magnet_is_calibrated()

    def is_correctly_orientated(self):
        return self.is_facing_north() and self.is_level()

    def is_facing_north(self):
        heading = None

        while not heading:
            heading = self.imu_controller.get_magnetic_heading()

        heading += self.magnetic_declination
        return 359 <= (heading % 360) <= 1

    def is_level(self):
        def value_is_in_margin(acceptable_error, expected_value, real_value):
            return (expected_value - acceptable_error) <= real_value <= (expected_value + acceptable_error)

        gravity_vector = self.imu_controller.get_gravity()
        error = .3
        return value_is_in_margin(error, 0, gravity_vector[0]) and value_is_in_margin(error, 0, gravity_vector[1]) and value_is_in_margin(error, 9.8, gravity_vector[2])

    def set_magnetic_declination(self, magnetic_declination):
        self.magnetic_declination = magnetic_declination
