import threading
from NetworkHandler import maintain_network_connection, get_internet_connection_status
from stargazer.src.ServerCommunication import main_server_communication

# wifi_thread = threading.Thread(target=maintain_network_connection, args=('stargazer', '123456789'), daemon=True)
# wifi_thread.start()

print("hopefully I'm connected now!")

main_server_communication()
'''
  
    once connected to wifi, open servercommunication in a thread
    
    
'''