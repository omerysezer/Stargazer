import subprocess

from gpiozero import AngularServo, LED
from time import sleep
from gpiozero.pins.pigpio import PiGPIOFactory
import threading


def _ensure_pigpiod_is_running():
    pigpiod_status = ""
    while "active (running)" not in pigpiod_status:
        subprocess.run(['systemctl', 'start', 'pigpiod'], stdout=subprocess.PIPE, stdin=subprocess.PIPE)
        pigpiod_status = subprocess.run(['systemctl', 'status', 'pigpiod'], stdout=subprocess.PIPE, text=True).stdout


class ServoLaserController:
    def __init__(self):
        _ensure_pigpiod_is_running()
        factory = PiGPIOFactory()

        self.pan_servo = AngularServo(pin=16, pin_factory=factory, min_angle=0, max_angle=180, initial_angle=90,
                                      min_pulse_width=.5 / 1000,
                                      max_pulse_width=2.5 / 1000)
        self.tilt_servo = AngularServo(pin=12, pin_factory=factory, min_angle=0, max_angle=180, initial_angle=90,
                                       min_pulse_width=.5 / 1000,
                                       max_pulse_width=2.5 / 1000)
        self.laser = LED(pin=18, active_high=True, initial_value=False)

        self.pan_servo.min()
        self.tilt_servo.min()
        sleep(3)
        self.laser.off()
        self.continue_displaying_pairing_number = False

    def turn_laser_on(self, time):
        def laser_on():
            self.laser.on()
            sleep(time)
            self.laser.off()

        thread = threading.Thread(target=laser_on)
        thread.start()

    def turn_laser_off(self):
        self.laser.off()

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

    def blink_laser(self, num_blinks):
        for i in range(num_blinks):
            if not self.continue_displaying_pairing_number:
                break
            self.laser.off()
            sleep(1)

            if not self.continue_displaying_pairing_number:
                break
            self.laser.on()
            sleep(1)
        self.laser.off()

    def sideways(self, num_turns):
        dirs = [self.pan_servo.max, self.pan_servo.min]
        for i in range(num_turns):
            if not self.continue_displaying_pairing_number:
                self.pan_servo.min()
                break
            dirs[i % 2]()
            sleep(1)

    def up_down(self, num_turns):
        dirs = [self.tilt_servo.max, self.tilt_servo.min]
        for i in range(num_turns):
            if not self.continue_displaying_pairing_number:
                self.tilt_servo.min()
                break

            dirs[i % 2]()
            sleep(1)

    def display_pairing_number(self, firstDigit, secondDigit, thirdDigit):
        self.continue_displaying_pairing_number = True

        def display():
            while self.continue_displaying_pairing_number:
                self.sideways(firstDigit)
                sleep(2)
                self.up_down(secondDigit)
                sleep(2)
                self.blink_laser(thirdDigit)
                sleep(2)

            self.laser.off()
            self.pan_servo.min()
            self.tilt_servo.min()

        display_thread = threading.Thread(target=display, daemon=False)
        display_thread.start()

        def stop_displaying():
            self.continue_displaying_pairing_number = False
            display_thread.join()

        return stop_displaying
