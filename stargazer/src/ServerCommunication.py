import random
from time import sleep
import socket
import json

from CalibOrientWarning import CalibOrientWarning

from ServoLaserController import ServoLaserController


def generate_three_digit_session_id():
    random_digit = random.randint(1, 10)
    return random_digit


def _get_server_message(sock):
    message = sock.makefile().readline()
    return json.loads(message)


def _send_message_to_server(sock, message):
    message = json.dumps(message) + "\n"
    bytes_sent = sock.send(message.encode())
    if bytes_sent == 0:
        raise ConnectionError


def run():
    while True:
        main_server_communication()


def main_server_communication():
    first_digit = generate_three_digit_session_id()
    second_digit = generate_three_digit_session_id()
    third_digit = generate_three_digit_session_id()
    pairing_id = str(first_digit) + str(second_digit) + str(third_digit)
    laser_controller = ServoLaserController()
    calib_orient_warning = CalibOrientWarning()

    session_id = -1
    connected = False

    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    while not connected:
        try:
            s.connect(("localhost", 5000))
        except socket.error:
            continue

        server_message = _get_server_message(s)

        if server_message["instruction"] == "GIVE_ID":
            msg = {
                "sessionId": pairing_id,
                "messageType": "solicited",
                "message": pairing_id,
                "instructionId": server_message["instructionId"]
            }

            try:
                _send_message_to_server(s, msg)
            except ConnectionError:
                return

            server_message = _get_server_message(s)

            while not server_message["instruction"] == "PROCEED":
                try:
                    _send_message_to_server(s, msg)
                except ConnectionError:
                    return
                server_message = _get_server_message(s)

            connected = True

    while session_id == -1:
        laser_controller.sideways(first_digit)
        laser_controller.up_down(second_digit)
        laser_controller.blink_laser(third_digit)
        session_id_msg = _get_server_message(s)
        if session_id_msg["instruction"] == "CHANGE_SESSION":
            session_id = session_id_msg["instructionData"]
            pairing_success_msg = {
                "sessionId": session_id,
                "messageType": "SUCCESS",
                "message": "",
                "instructionId": session_id_msg["instructionId"]
            }
            _send_message_to_server(s, pairing_success_msg)

    while True:
        calibration_ok = False
        level_ok = False
        orientation_ok = False
        while not calibration_ok or not level_ok or not orientation_ok:
            if not calib_orient_warning.is_calibrated():
                warning_msg = {
                    "sessionId": session_id,
                    "messageType": "CALIBRATION_WARNING",
                    "message": ""
                }
                _send_message_to_server(s, warning_msg)
            elif not calibration_ok:
                ok_msg = {
                    "sessionId": session_id,
                    "messageType": "CALIBRATION_OK",
                    "message": "",
                    "instructionId": ""
                }
                _send_message_to_server(s, ok_msg)
                calibration_ok = True

            if not calib_orient_warning.is_level():
                warning_msg = {
                    "sessionId": session_id,
                    "messageType": "LEVEL_WARNING",
                    "message": "",
                    "instructionId": ""
                }
                _send_message_to_server(s, warning_msg)
            elif not level_ok:
                ok_msg = {
                    "sessionId": session_id,
                    "messageType": "LEVEL_OK",
                    "message": "",
                    "instructionId": ""
                }
                _send_message_to_server(s, ok_msg)
                level_ok = True

            if not calib_orient_warning.is_facing_north():
                warning_msg = {
                    "sessionId": session_id,
                    "messageType": "ORIENTATION_WARNING",
                    "message": "",
                    "instructionId": ""
                }
                _send_message_to_server(s, warning_msg)
            elif not orientation_ok:
                ok_msg = {
                    "sessionId": session_id,
                    "messageType": "ORIENTATION_OK",
                    "message": "",
                    "instructionId": ""
                }
                _send_message_to_server(s, ok_msg)
                orientation_ok = True
            sleep(2)

        instruction = _get_server_message(s)

        if instruction["instruction"] == "LASER_ON":
            try:
                laser_controller.turn_laser_on(5)
            except:
                _send_message_to_server(s, {
                    "messageType": "LASER_ON_FAILURE",
                    "sessionId": session_id,
                    "message": "",
                    "instructionId": instruction["instructionId"]
                })

            _send_message_to_server(s, {
                "messageType": "SUCCESS",
                "sessionId": session_id,
                "message": "",
                "instructionId": instruction["instructionId"]
            })
        elif instruction["instruction"] == "LASER_OFF":
            try:
                laser_controller.turn_laser_off()
            except:
                _send_message_to_server(s, {
                    "messageType": "LASER_OFF_FAILURE",
                    "sessionId": session_id,
                    "message": "",
                    "instructionId": instruction["instructionId"]
                })

            _send_message_to_server(s, {
                "messageType": "SUCCESS",
                "sessionId": session_id,
                "message": "",
                "instructionId": instruction["instructionId"]
            })
        elif instruction["instruction"] == "POINT":
            coords = json.loads(instruction["instructionData"])

            try:
                laser_controller.point_to_coords(coords["azimuth"], coords["altitude"])
            except:
                _send_message_to_server(s, {
                    "messageType": "POINT_TO_FAILURE",
                    "sessionId": session_id,
                    "message": "",
                    "instructionId": instruction["instructionId"]
                })

            _send_message_to_server(s, {
                "messageType": "SUCCESS",
                "sessionId": session_id,
                "message": "",
                "instructionId": instruction["instructionId"]
            })
