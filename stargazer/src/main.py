import threading
from NetworkHandler import maintain_network_connection, get_internet_connection_status


wifi_thread = threading.Thread(target=maintain_network_connection, args=('stargazer', '123456789'), daemon=True)
wifi_thread.start()

print("hopefully I'm connected now!")