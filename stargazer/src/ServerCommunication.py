import random
from threading import Thread
from time import sleep
from random import randint
import socket
import json

from stargazer.src.CalibrationOrientationWarning import CalibOrientWarning
from stargazer.src.ServoLaserController import sideways, up_down, blink_laser, turn_laser_on, turn_laser_off

'''
message should include sessionid, type, message
'''


def generate_three_digit_session_id():
    random_digit = random.randint(1, 10)
    return random_digit


def main_server_communication():
    first_digit = generate_three_digit_session_id()
    second_digit = generate_three_digit_session_id()
    third_digit = generate_three_digit_session_id()
    session_id = str(first_digit) + " " + str(second_digit) + " " + str(third_digit)
    new_session_id = -1
    type_message = ""
    message = ""
    connected = False
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    while not connected:

        s.connect(("localhost", 5000))
        msg = {
            "session_id": session_id,
            "type": "solicited",
            "message": session_id
        }

        json_object = json.dumps(msg, indent=4)
        s.send(json_object.encode())
        msg = s.recv(1024)

        if msg.decode() == "SUCCESS":
            connected = True
        else:
            s.close()

    msg = s.recv(1024)
    print(msg.decode())

    while new_session_id == -1:
        sideways(first_digit)
        up_down(second_digit)
        blink_laser(third_digit)

        session_id_msg = s.recv(1024)
        if session_id_msg.decode().split(":", 1)[0] == "New Session Id":
            new_session_id = session_id_msg.decode().split(":")[1]

    caliborientwarning = CalibOrientWarning()
    while True:

        if not caliborientwarning.is_calibrated():
            msg = {
                "session_id": new_session_id,
                "type": "warning",
                "message": "Bad Calibration"
            }
            json_object = json.dumps(msg, indent=4)
            s.send(json_object.encode())
        elif not caliborientwarning._is_facing_north() or not caliborientwarning._is_level():
            msg = {
                "session_id": new_session_id,
                "type": "warning",
                "message": "Bad Orientation"
            }
            json_object = json.dumps(msg, indent=4)
            s.send(json_object.encode())
        else:
            new_msg = s.recv(1024)
            decoded_msg = new_msg.decode()
            if decoded_msg == "Laser On":
                turn_laser_on(5)
            if decoded_msg == "Laser Off":
                turn_laser_off()
            # if()


'''

    generate 3 digit sessionId

    start wifi thread
    once connected to wifi, start main function in servercommunication in a thread

    in the main function:
        create a socket
        connect to www.stargazer.ninja:5000

        send the sessionId
            if response is success continue "SUCCESS"

                now start moving servos in correspondence to sessinonId
                first digit = sideways
                second digit = up down
                third digit = on off

            otherwise "FAILURE"
                end socket
                retry previous steps

        wait for new message
            if message starts with New Session Id or something
            update session id

        from now on all messages have to have sessionId=384762346 at the end
        check calibration and orientation
            while bad:
                send message to server

        once good
            await instructions and handle each one


'''
