/etc/init.d/NetworkManager stop
iwconfig wlan0 essid "candlelit"
iwconfig wlan0 channel "3"
iwconfig wlan0 mode "ad-hoc"
ifconfig wlan0 192.168.1.4 up

