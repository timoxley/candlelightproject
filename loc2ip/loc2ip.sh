#!/bin/bash
# printf "Incoming Arguments: %s %s %s\n"  $1 $2 $3

if [ $# != 3 ]; then
    echo "Usage: bash $0 latitude longitude altitude";
    printf "Example: bash %s %s %s %s\n" $0 40.439167 -79.976667 40;
    exit -1;
fi

# Assume the max precision for long/lat is 6
long="$1"
lat="$2"
alt="$3"

# Normalise the arugments (0-360)
if [ $(echo "$long < 0" | bc -l) -eq 1 ]; then
    long=$(echo "360 + $long" | bc -l);
fi

# Normalise the arguments( 0-180)
lat=$(echo "90 + $lat" | bc -l);

#printf "The value of long: %.6f\n\tlat: %.6f\n\talt: %d\n" $long $lat $alt 

# Convert to microseconds
long=$(echo "$long * 360 * 100 * 100" | bc -l);
lat=$(echo "$lat * 360 * 100 * 100" | bc -l);

printf "The value in milliseconds is: \nlong: %.6f\nlat: %.6f\n" $long $lat

# convert to integer values
long=$(echo "scale=0; $long/1.0" | bc);
lat=$(echo "scale=0; $lat/1.0" | bc);

printf "Values:\nlong: %d\nlat: %d\n" $long $lat

longstring=$(printf "%.8x" $long);
latstring=$(printf "%.8x" $lat);

printf "%.8x\n" $long;
printf "%.8x\n" $lat;

longstring1=$(echo ${longstring:0:4});
longstring2=$(echo ${longstring:4:4});

latstring1=$(echo ${latstring:0:4});
latstring2=$(echo ${latstring:4:4});

printf "3ff3:%s:%s:%s:%s:%.4x::\n" $longstring1 $longstring2 $latstring1 $latstring2 $alt
