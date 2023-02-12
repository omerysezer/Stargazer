import threading
from NetworkHandler import maintain_network_connection, get_internet_connection_status
from ServerCommunication import main_server_communication
from time import sleep

wifi_thread = threading.Thread(target=maintain_network_connection, args=('stargazer', '123456789'), daemon=True)
wifi_thread.start()

while not get_internet_connection_status():
    sleep(1)

main_server_communication()
