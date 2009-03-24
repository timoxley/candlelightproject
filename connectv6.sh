usage() {
    echo "connectv6.sh - Connects to candlelight network ad-hoc"
    echo "Usage: sudo connectv6.sh eric";
}

if [ ! -n "$1" ]; then 
    echo 'Please provide a name.';
    exit 1; 
fi

ip=$(python loc2ip/loc2ip.py < ./static-locs/coord.$1);
interface="wlan0";

if [ ! -n "$2"]; then
    if [ "$2" -eq "-i" ]; then
        exit 4
        if [ ! -n "$3" ]; then
            interface="$3";
        else
            echo '-i supplied but, like not really.';    
        fi
    fi
fi 

if [ "$1" = 'shin' ]; then
    interface="eth1";
fi
echo "Your Ipv6 address is $ip";
sh connect.sh
ifconfig $interface add $ip/128;
ifconfig wlan0 up
