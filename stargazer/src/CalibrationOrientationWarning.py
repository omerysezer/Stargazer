from IMUController import IMUController


class CalibOrientWarning:
    def __init__(self):
        self.imu_controller = IMUController()
        self.magnetic_declination = None
        self.magnet_declination_is_set = False

    def is_calibrated(self):
        return self.imu_controller.accel_is_calibrated() and self.imu_controller.magnet_is_calibrated()

    def _is_facing_north(self):
        if not self.magnet_declination_is_set:
            raise RuntimeError("Magnetic declination has not been set. True north cannot be found.")

        return 359 <= (self.imu_controller.get_magnetic_heading() + self.magnetic_declination) % 360 <= 1

    def _is_level(self):
        def value_is_in_margin(acceptable_error, expected_value, real_value):
            return (expected_value - acceptable_error) <= real_value <= (expected_value + acceptable_error)

        gravity_vector = self.imu_controller.get_gravity()
        error = .3
        return value_is_in_margin(error, 0, gravity_vector[0]) and value_is_in_margin(error, 0, gravity_vector[1]) and value_is_in_margin(error, 9.8, gravity_vector[2])

    def set_magnetic_declination(self, declination):
        self.magnetic_declination = declination
        self.magnet_declination_is_set = True



