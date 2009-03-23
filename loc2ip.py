#!/usr/bin/python

import sys

def main():
    if len(sys.argv) != 4:
        sys.stderr.write ("Usage: python "+sys.argv[0]+" latitude longitude altitude\n")
        sys.stderr.write ("Example: python "+sys.argv[0]+" 40.439167 -79.976667 40\n")
        exit(1)

    try:
        lat = float(sys.argv[1])
        if lat > 90 or lat < -90:
            sys.stderr.write(sys.argv[0]+": Latitude out of bounds. [-90, 90]\n")
            exit(1)

        long = float(sys.argv[2])
        if long > 180 or long < -180:
            sys.stderr.write(sys.argv[0]+": Longitude out of bounds. [-180, 180]\n")
            exit(1)

        alt = float(sys.argv[3])
        if alt < -100 or alt >= 6453.6:
            sys.stderr.write(sys.argv[0]+": Altitudes of this magnitude are unsupported. [-100, 6453.6)\n")
            exit(1)
    except ValueError:
        sys.stderr.write(sys.argv[0]+": Arguments must be of type float.\n")
        exit(1)

    lat += 90
    lat /= 180
    lat *= (2**32)-1
    long += 180
    long /= 360
    long *= (2**32)-1
    alt += 100
    alt *= 10
    lat = int(lat)
    long = int(long)
    alt = int(alt)
    print "3ffe:%04x:%04x:%04x:%04x:%04x:dead:beef" % \
            ((lat&(0xffff<<16))>>16, lat&0xffff, \
            (long&(0xffff<<16))>>16, long&0xffff, \
            alt&0xffff)

main()
