#include <stdio.h>
#include <stdlib.h>
#include <tgmath.h>

#define SCRIPTNAME "loc2ip"

void usage() {
    fprintf(stderr, "%s: Generates an IPv6 address, based on geolocation coordinates.\n", SCRIPTNAME);
    fprintf(stderr, "Usage: %s latitude longitude altitude\n", SCRIPTNAME);
    fprintf(stderr, "Example: %s %f %f %d\n", SCRIPTNAME, 40.439167, -79.976667, 40);
}

void invalid_argument(char *arg) {
    printf("Argument invalid\n");

}

int main(int argc, char **argv) {

if (argc != 4) {
    usage();
    exit(1);
}

long longitude = atof(argv[1]);
long latitude = atof(argv[2]);
long altitude = atof(argv[3]);

/* Normalise longitude (0-360) */
if(longitude < 0) {
    longitude += 360;
}

/* Normalise latitude (0-180) */
latitude += 90; 

/* Convert to microseconds */
longitude *=  (360 * 100 * 100);
latitude *= (360 * 100 * 100);


latitude += 90;
latitude /= 180;
latitude *= (2&0xffffffff);
longitude += 180;
longitude /= 360;
longitude *= (2&0xffffffff);
altitude += 100;
altitude *= 10;

/* Convert to integer values */
long longitudeInt = (long) longitude;
long latitudeInt = (long) latitude;
long altitudeInt = (long) latitude;

/*print "3ffe:%04x:%04x:%04x:%04x:%04x:dead:beef" % \
        ((lat&(0xffff<<16))>>16, lat&0xffff, \
        (long&(0xffff<<16))>>16, long&0xffff, \
        alt&0xffff)
*/

/* Output IP Address */
printf("3ffe::%04x::%04x::%04x::%04x::%04x\n", longitudeInt >> 16, longitudeInt & 0xffff,
 latitudeInt >> 16, latitudeInt & 0xffff, (long) altitude);

return 0;

}
