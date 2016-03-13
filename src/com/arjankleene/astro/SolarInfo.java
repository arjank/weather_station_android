/**
 * Source for the calculations: http://williams.best.vwh.net/sunrise_sunset_algorithm.htm
 */

package com.arjankleene.astro;

import android.location.Location;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Calculate sunrise and sunset.
 *
 * Note that the result time is always given as a GregorianCalendar object.
 * Because it does not make sense to calculate time to the millisecond, that value
 * is explicitly set to 0.
 */
public class SolarInfo {

    public static final double ZENITH_OFFICIAL = 90 + 5.0/6;
    public static final double ZENITH_CIVIL = 96;
    public static final double ZENITH_NAUTICAL = 102;
    public static final double ZENITH_ASTRONOMICAL = 108;

    private static final double DEGRAD = Math.PI / 180.0;
    private static final double RADEG = 180.0 / Math.PI;

    private GregorianCalendar date;
    private double latitude;
    private double longitude;
    private double zenith;

    private Boolean isUpAllDay;
    private Boolean isDownAllDay;
    private GregorianCalendar sunrise;
    private GregorianCalendar sunset;

    public SolarInfo(GregorianCalendar date, Location latlng) {
        this(date, latlng.getLatitude(), latlng.getLongitude(), ZENITH_OFFICIAL);
    }

    public SolarInfo(GregorianCalendar date, Location latlng, double zenith) {
        this(date, latlng.getLatitude(), latlng.getLongitude(), zenith);
    }

    public SolarInfo(GregorianCalendar date, double latitude, double longitude) {
        this(date, latitude, longitude, ZENITH_OFFICIAL);
    }

    public SolarInfo(GregorianCalendar date, double latitude, double longitude, double zenith) {
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zenith = zenith;

        findSunRise();
        findSunSet();
    }

    public GregorianCalendar getSunRise() {
        return sunrise;
    }

    public GregorianCalendar getSunSet() {
        return sunset;
    }

    public boolean isUpAllDay() {
        return isUpAllDay;
    }

    public boolean isDownAllDay() {
        return isDownAllDay;
    }

    private void findSunRise() {
        int dayOfYear = date.get(Calendar.DAY_OF_YEAR);

        // convert longitude to hour value and calculate an approximate time
        double lngHour = longitude / 15;
        double t = dayOfYear + ((6 - lngHour) / 24);

        // calculate the Sun's true longitude
        double sunLongitude = getSunLongitude(t);

        // calculate the Sun's declination
        double cosH = getCosH(sunLongitude);
        if (isUpOrDownAllDay(cosH)) return;

        // finish calculating H and convert into hours
        double h = 360.0 - acosd(cosH);
        h = h / 15.0;

        // calculate the Sun's right ascension
        double rightAscension = getRightAscension(sunLongitude);

        // calculate local mean time of rising/setting (based on geographical position)
        t = h + rightAscension - (0.06571 * t) - 6.622;

        // adjust back to UTC
        double utc = (t - lngHour);

        sunrise = (GregorianCalendar) date.clone();

        updateEventDateWithTime(sunrise, utc);
    }

    private void findSunSet() {
        int dayOfYear = date.get(Calendar.DAY_OF_YEAR);

        // convert longitude to hour value and calculate an approximate time
        double lngHour = longitude / 15;
        double t = dayOfYear + ((18 - lngHour) / 24);

        // calculate the Sun's true longitude
        double sunLongitude = getSunLongitude(t);

        // calculate the Sun's declination
        double cosH = getCosH(sunLongitude);
        if (isUpOrDownAllDay(cosH)) return;

        // finish calculating H and convert into hours
        double h = acosd(cosH);
        h = h / 15;

        // calculate the Sun's right ascension
        double rightAscension = getRightAscension(sunLongitude);

        // calculate local mean time of rising/setting based on geographical position
        t = h + rightAscension - (0.06571 * t) - 6.622;

        // adjust back to UTC
        double utc = (t - lngHour);

        sunset = (GregorianCalendar) date.clone();

        updateEventDateWithTime(sunset, utc);
    }

    /**
     * Set the event date to the given utc time
     *
     * Takes DST into account if that is applicable at the given time for the timezone
     * that is specified in the GregorianCalendar date object.
     */
    private void updateEventDateWithTime(GregorianCalendar date, double utc) {
        double localTime = convertToLocalTime(utc);

        int hours = (int) localTime;
        localTime = (localTime - hours) * 60;

        int minutes = (int) localTime;
        localTime = (localTime - minutes) * 60;

        int seconds = (int) localTime;

        date.set(Calendar.HOUR_OF_DAY, hours);
        date.set(Calendar.MINUTE, minutes);
        date.set(Calendar.SECOND, seconds);
        date.set(Calendar.MILLISECOND, 0);

        // The DST offset may cause the hours value to move to the next day
        hours += date.get(Calendar.DST_OFFSET) / 3600000;
        date.set(Calendar.HOUR_OF_DAY, hours);
    }

    /**
     * Convert given universal time to local time, keeping in [0, 24) range.
     *
     * The utc time does not have to be in the output range.
     */
    private double convertToLocalTime(double utcTime) {
        double localTime = utcTime + date.get(Calendar.ZONE_OFFSET) / 3600000;
        localTime = (localTime + 24) % 24;
        return localTime;
    }

    private double getSunLongitude(double t) {
        // calculate the Sun's mean anomaly
        double m = (0.9856 * t) - 3.289;

        // calculate the Sun's true longitude
        return (m + (1.916 * sind(m)) + (0.020 * sind(2 * m)) + 282.634) % 360;
    }

    private double getRightAscension(double l) {
        // calculate the Sun's right ascension
        double ra = atand(0.91764 * tand(l));

        // right ascension value needs to be in the same quadrant as L
        ra = ra + ((Math.floor(l / 90) * 90) - (Math.floor(ra / 90) * 90));

        // right ascension value needs to be converted into hours
        ra = ra / 15;

        return ra;
    }

    private double getCosH(double l) {
        double sinDec = 0.39782 * sind(l);
        double cosDec = cosd(asind(sinDec));

        // calculate the Sun's local hour angle
        return (cosd(zenith) - (sinDec * sind(latitude))) / (cosDec * cosd(latitude));
    }

    private boolean isUpOrDownAllDay(double cosH) {
        if (cosH > 1) {
            // Sun never rises on this location on the specified date
            isDownAllDay = true;
            isUpAllDay = false;
            return true;
        } else if (cosH < -1) {
            // Sun never sets on this location on the specified date
            isDownAllDay = false;
            isUpAllDay = true;
            return true;
        } else {
            isDownAllDay = false;
            isUpAllDay = false;
        }
        return false;
    }

    private double sind(double num) {
        return Math.sin(num * DEGRAD);
    }
    private double cosd(double num) {
        return Math.cos(num * DEGRAD);
    }
    private double tand(double num) {
        return Math.tan(num * DEGRAD);
    }
    private double asind(double num) {
        return RADEG * Math.asin(num);
    }
    private double acosd(double num) {
        return RADEG * Math.acos(num);
    }
    private double atand(double num) {
        return RADEG * Math.atan(num);
    }
}
