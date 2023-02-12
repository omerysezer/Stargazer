import subprocess
from time import sleep


def _ensure_required_programs_are_running():
    enable_network_manager = ['systemctl', 'enable', 'NetworkManager']
    start_network_manager = ['systemctl', 'start', 'NetworkManager']
    start_dhcpcd = ['dhcpcd']

    subprocess.run(enable_network_manager, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    subprocess.run(start_network_manager, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)

    subprocess.run(start_dhcpcd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)


def _connect_to_wifi(ssid, password):
    scan_for_new_wifi_networks = ['nmcli', 'dev', 'wifi', 'rescan']
    connect_to_network = ['nmcli', 'dev', 'wifi', 'connect', ssid, 'password', password]

    while True:
        subprocess.run(scan_for_new_wifi_networks, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        result = subprocess.run(connect_to_network, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)

        if "Device 'wlan0' successfully activated" in result.stdout:
            subprocess.run(['dhclient', 'wlan0'], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)

            if get_internet_connection_status():
                break


def get_internet_connection_status():
    def addr_responds_to_ping(address):
        ping_command = ['ping', '-W', '1', '-c', '1', address]
        response = subprocess.run(ping_command, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        return '1 packets transmitted, 1 received' in response.stdout

    #              google    google   level3comm.   cloudfare
    addresses = ['8.8.4.4', '8.8.8.8', '4.2.2.2', '1.1.1.1']

    for addr in addresses:
        if addr_responds_to_ping(addr):
            return True

    return False


def maintain_network_connection(ssid, password):
    _ensure_required_programs_are_running()
    while True:
        if not get_internet_connection_status():
            _connect_to_wifi(ssid, password)

        sleep(5)
