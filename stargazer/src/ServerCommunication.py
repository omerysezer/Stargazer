import random
from threading import Thread
from time import sleep
from random import randint
import socket
import json

# from stargazer.src.CalibOrientWarning import CalibOrientWarning
# from stargazer.src.ServoLaserController import sideways, up_down, blink_laser, turn_laser_on, turn_laser_off

'''
message should include sessionid, type, message

GIVE_ID

PROCEED

FIX_INVALID_MESSAGE 

POINT 

LASER_ON

LASER_OFF

CHANGE_SESSION
'''


def generate_three_digit_session_id():
    random_digit = random.randint(1, 10)
    return random_digit


def _get_server_message(sock):
    message = sock.recv(1024).decode()
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
                "message": pairing_id
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
        # sideways(first_digit)
        # up_down(second_digit)
        # blink_laser(third_digit)
        print(pairing_id)
        session_id_msg = _get_server_message(s)
        if session_id_msg["instruction"] == "CHANGE_SESSION":
            session_id = session_id_msg["instructionData"]
            print("NEW ID: ", session_id)
    # caliborientwarning = CalibOrientWarning()
    # while True:
    #
    #     if not caliborientwarning.is_calibrated():
    #         msg = {
    #             "session_id": session_id,
    #             "type": "warning",
    #             "message": "Bad Calibration"
    #         }
    #         json_object = json.dumps(msg, indent=4)
    #         s.send(json_object.encode())
    #     elif not caliborientwarning.is_correctly_orientated():
    #         message = ''
    #
    #         if not caliborientwarning.is_level():
    #             message += 'Device is not level\n'
    #         if not caliborientwarning.is_facing_north():
    #             message += 'Device is not facing north\n'
    #
    #         msg = {
    #             "session_id": session_id,
    #             "type": "warning",
    #             "message": message
    #         }
    #         json_object = json.dumps(msg, indent=4)
    #         s.send(json_object.encode())
    #     else:
    #         new_msg = s.recv(1024)
    #         decoded_msg = new_msg.decode()
    #         if decoded_msg == "Laser On":
    #             turn_laser_on(5)
    #         if decoded_msg == "Laser Off":
    #             turn_laser_off()
    #         # if()


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
