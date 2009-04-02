/etc/init.d/NetworkManager stop
iwconfig wlan0 essid "candlelit"
iwconfig wlan0 channel "3"
iwconfig wlan0 mode "ad-hoc"
if [ ! -n "$1" ]; then
    echo $1
    exit 3
    if [ "$1" -eq "-i" ]; then
        interface="$2"
    else
        interface="wlan0"
    fi
    ifconfig "$interface" "$1" up
fi
