package net.mbeffects.adhan;

import java.util.*;
import net.rim.device.api.util.MathUtilities;

public class PrayerCalculator {

    private static final double LAT        = 32.85;
    private static final double LNG        = -6.58;
    private static final double ALTITUDE   = 765.0;
    private static final double TIMEZONE   = 0.0;
    private static final double FAJR_ANGLE = 18.0;
    private static final double ISHA_ANGLE = 17.0;

    private static final double CORR_FAJR    = -5.0;
    private static final double CORR_DHUHR   =  5.0;
    private static final double CORR_ASR     =  0.0;
    private static final double CORR_MAGHRIB =  5.0;
    private static final double CORR_ISHA    =  0.0;

    private static double toRad(double d) { return d * Math.PI / 180.0; }
    private static double toDeg(double r) { return r * 180.0 / Math.PI; }
    private static double fixH(double h) {
        h = h % 24.0;
        return h < 0.0 ? h + 24.0 : h;
    }

    private static double julianDay(int y, int m, int d) {
        if (m <= 2) { y--; m += 12; }
        double A = Math.floor(y / 100.0);
        double B = 2.0 - A + Math.floor(A / 4.0);
        return Math.floor(365.25 * (y + 4716.0))
             + Math.floor(30.6001 * (m + 1.0)) + d + B - 1524.5;
    }

    private static double[] sunPos(double jd) {
        double D  = jd - 2451545.0;
        double g  = toRad(357.529 + 0.98560028 * D);
        double q  = 280.459 + 0.98564736 * D;
        double L  = toRad(q + 1.915 * Math.sin(g) + 0.020 * Math.sin(2.0*g));
        double e  = toRad(23.439 - 0.00000036 * D);
        double RA = toDeg(MathUtilities.atan2(
            Math.cos(e) * Math.sin(L), Math.cos(L))) / 15.0;
        double dec = MathUtilities.asin(Math.sin(e) * Math.sin(L));
        double EqT = q / 15.0 - fixH(RA);
        return new double[]{ dec, EqT };
    }

    private static double AT(double noon, double dec,
                              double latR, double angle, int dir) {
        double v = (-Math.sin(toRad(angle))
                    - Math.sin(dec) * Math.sin(latR))
                  / (Math.cos(dec) * Math.cos(latR));
        if (v < -1.0) v = -1.0;
        if (v >  1.0) v =  1.0;
        return noon + dir * toDeg(MathUtilities.acos(v)) / 15.0;
    }

    private static String fmt(double h) {
        h = fixH(h);
        int hh = (int)h;
        int mm = (int)Math.floor((h - hh) * 60.0 + 0.5);
        if (mm == 60) { hh++; mm = 0; }
        return (hh < 10 ? "0" : "") + hh
             + ":" + (mm < 10 ? "0" : "") + mm;
    }

    public static String[] calculate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);

        double[] sp   = sunPos(julianDay(y, m, d));
        double dec    = sp[0];
        double EqT    = sp[1];
        double latR   = toRad(LAT);
        double ac     = (0.0347 * Math.sqrt(ALTITUDE)) / 60.0;
        double noon   = 12.0 + TIMEZONE - LNG / 15.0 - EqT;
        boolean hasSC = (m >= 4 && m <= 10) || (m == 11 && d <= 14);
        double sc     = hasSC ? -3.0 / 60.0 : 0.0;
        double asrAlt = toDeg(MathUtilities.atan(
            1.0 / (1.0 + Math.tan(Math.abs(latR - dec)))));

        return new String[]{
            fmt(AT(noon,dec,latR,FAJR_ANGLE,-1) + CORR_FAJR/60.0   + sc),
            fmt(AT(noon,dec,latR,0.833,     -1) - ac),
            fmt(noon + CORR_DHUHR/60.0),
            fmt(AT(noon,dec,latR,-asrAlt,   +1) + CORR_ASR/60.0),
            fmt(AT(noon,dec,latR,0.833,     +1) + ac + 2.0/60.0
                + CORR_MAGHRIB/60.0 + sc),
            fmt(AT(noon,dec,latR,ISHA_ANGLE,+1) + CORR_ISHA/60.0),
        };
    }
}
