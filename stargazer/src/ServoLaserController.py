import gpiozero
from gpiozero import AngularServo, LED
from time import sleep
from gpiozero.pins.pigpio import PiGPIOFactory
import threading


class ServoLaserController:
    def __init__(self):
        factory = PiGPIOFactory()

        self.pan_servo = AngularServo(pin_factory=factory, min_angle=0, max_angle=180, initial_angle=90,
                                      min_pulse_width=2.5 / 1000,
                                      max_pulse_width=.5 / 1000)
        self.tilt_servo = AngularServo(pin_factory=factory, min_angle=0, max_angle=180, initial_angle=90,
                                       min_pulse_width=2.5 / 1000,
                                       max_pulse_width=.5 / 1000)
        self.laser = LED(pin=18, active_high=True, initial_value=False)

    def turn_laser_on(self, time):
        def laser_on():
            self.laser.on()
            sleep(time)
            self.laser.off()

        thread = threading.Thread(target=laser_on)
        thread.start()

    def turn_laser_off(self):
        self.laser.off()

    def blink_laser(self, num_blinks):
        self.laser.blink(1, 1, num_blinks)

    def sideways(self, num_blinks):
        return

    def up_down(self, num_blinks):
        return

    def point_to_coords(self, azimuth, altitude):
        pan_angle = None
        tilt_angle = None
        if 180 <= azimuth <= 360:
            pan_angle = 360 - azimuth
            tilt_angle = altitude
        else:
            pan_angle = 180 - azimuth
            tilt_angle = 180 - altitude

        self.pan_servo.angle = pan_angle
        self.tilt_servo.angle = tilt_angle