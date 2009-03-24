#!/usr/bin/python

import sys

def main():

    try:
        #sys.stdout.write("Enter Latitude in degrees [-90, 90]: ")
        lat = sys.stdin.readline()
        lat = float(lat)
        if lat > 90 or lat < -90:
            sys.stderr.write(sys.argv[0]+": Latitude out of bounds. [-90, 90]\n")
            print("0000:0000:0000:0000:0000:0000:0000:0000")
            exit(1)

        #sys.stdout.write("Enter Longitude in degrees [-180, 180]: ")
        long = sys.stdin.readline()
        long = float(long)
        if long > 180 or long < -180:
            sys.stderr.write(sys.argv[0]+": Longitude out of bounds. [-180, 180]\n")
            print("0000:0000:0000:0000:0000:0000:0000:0000")
            exit(1)

        #sys.stdout.write("Enter Altitude in metres [-100, 6453.6): ")
        alt = sys.stdin.readline()
        alt = float(alt)
        if alt < -100 or alt >= 6453.6:
            sys.stderr.write(sys.argv[0]+": Altitudes of this magnitude are unsupported. [-100, 6453.6)\n")
            print("0000:0000:0000:0000:0000:0000:0000:0000")
            exit(1)
    except ValueError:
        sys.stderr.write(sys.argv[0]+": Arguments must be of type float.\n")
        print("0000:0000:0000:0000:0000:0000:0000:0000")
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
