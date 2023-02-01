import gpiozero
from gpiozero import AngularServo, LED
from time import sleep
from gpiozero.pins.pigpio import PiGPIOFactory
import threading

factory = PiGPIOFactory()

pan_servo = AngularServo(pin_factory=factory, min_angle=0, max_angle=180, initial_angle=90, min_pulse_width=2.5 / 1000,
                         max_pulse_width=.5 / 1000)
tilt_servo = AngularServo(pin_factory=factory, min_angle=0, max_angle=180, initial_angle=90, min_pulse_width=2.5 / 1000,
                          max_pulse_width=.5 / 1000)
laser = LED(pin=18, active_high=True, initial_value=False)


def turn_laser_on(time):
    def laser_on():
        laser.on()
        sleep(time)
        laser.off()

    thread = threading.Thread(target=laser_on)
    thread.start()


def turn_laser_off():
    laser.off()


def blink_laser(num_blinks):
    laser.blink(1, 1, num_blinks)

def sideways(num_blinks):


def up_down(num_blinks):



def point_to_coords(azimuth, altitude):
    pan_angle = None
    tilt_angle = None
    if azimuth in range(180, 361):
        pan_angle = 360 - azimuth
        tilt_angle = altitude
    else:
        pan_angle = 180 - azimuth
        tilt_angle = 180 - altitude

    pan_servo.angle = pan_angle
    tilt_servo.angle = tilt_angle
