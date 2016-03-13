package com.arjankleene.astro;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.*;

/**
 * Several tests for the calculations for sunrise and sunset in different parts of the world
 *
 * The sunrise and sunset values are similar to those on http://www.timeanddate.com/
 * and to those in other programming languages (most notably: the sunrise and sunset functions of php)
 * Differences are generally less than one minute, or 0.0167 hour.
 */
public class SolarInfoTest {

    public static final double AUCKLAND_LATITUDE = -36.85;
    public static final double AUCKLAND_LONGITUDE = 174.7644727;

    public static final double BERLIN_LATITUDE = 52.5166667;
    public static final double BERLIN_LONGITUDE = 13.3811393;

    public static final double ENSCHEDE_LATITUDE = 52.2406295;
    public static final double ENSCHEDE_LONGITUDE = 6.8854202;

    public static final double HONOLULU_LATITUDE = 21.3166667;
    public static final double HONOLULU_LONGITUDE = -157.852194;

    public static final double LONGYEARBYEN_LATITUDE = 78.2166667;
    public static final double LONGYEARBYEN_LONGITUDE = 15.6311393;

    @org.junit.Test
    public void testGetSunRise() throws Exception {
        GregorianCalendar date = new GregorianCalendar(2016, Calendar.MARCH, 9);

        SolarInfo info = new SolarInfo(date, ENSCHEDE_LATITUDE, ENSCHEDE_LONGITUDE);

        GregorianCalendar expected = new GregorianCalendar(2016, Calendar.MARCH, 9, 6, 59, 21);

        GregorianCalendar actual = info.getSunRise();

        assertFalse(info.isDownAllDay());
        assertFalse(info.isUpAllDay());
        assertEquals("expected: " + expected.getTime() + " actual: " + actual.getTime(), expected, actual);
    }

    @org.junit.Test
    public void testGetSunRise2() throws Exception {
        TimeZone timeZone = TimeZone.getTimeZone("Europe/London");
        GregorianCalendar date = new GregorianCalendar(1998, Calendar.OCTOBER, 25);
        date.setTimeZone(timeZone);
        SolarInfo info = new SolarInfo(date, 52.5, -1.9167);

        GregorianCalendar expected = new GregorianCalendar(1998, Calendar.OCTOBER, 25, 6, 50, 20);
        expected.setTimeZone(timeZone);

        GregorianCalendar actual = info.getSunRise();

        assertEquals("expected: " + expected.getTime() + " actual: " + actual.getTime(), expected, actual);
    }

    @org.junit.Test
    public void testGetSunRiseAuckland() throws Exception {
        TimeZone timeZone = TimeZone.getTimeZone("Pacific/Auckland");

        GregorianCalendar date = new GregorianCalendar(timeZone);
        date.set(2016, Calendar.MARCH, 13);
        SolarInfo info = new SolarInfo(date, AUCKLAND_LATITUDE, AUCKLAND_LONGITUDE);

        GregorianCalendar expected = new GregorianCalendar(timeZone);
        expected.set(2016, Calendar.MARCH, 13, 7, 17, 49);
        expected.set(Calendar.MILLISECOND, 0);

        GregorianCalendar actual = info.getSunRise();

        assertEquals("expected: " + expected.getTime() + " actual: " + actual.getTime(), expected, actual);
    }

    @org.junit.Test
    public void testGetSunRiseHonolulu() throws Exception {
        TimeZone timeZone = TimeZone.getTimeZone("Pacific/Honolulu");

        GregorianCalendar date = new GregorianCalendar(timeZone);
        date.set(2016, Calendar.MARCH, 13);
        SolarInfo info = new SolarInfo(date, HONOLULU_LATITUDE, HONOLULU_LONGITUDE);

        GregorianCalendar expected = new GregorianCalendar(timeZone);
        expected.set(2016, Calendar.MARCH, 13, 6, 40, 59);
        expected.set(Calendar.MILLISECOND, 0);

        GregorianCalendar actual = info.getSunRise();

        assertEquals("expected: " + expected.getTime() + " actual: " + actual.getTime(), expected, actual);
    }

    @org.junit.Test
    public void testGetSunRiseBerlin() throws Exception {
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Berlin");

        GregorianCalendar date = new GregorianCalendar(timeZone);
        date.set(2016, Calendar.MARCH, 13);
//        GregorianCalendar date = new GregorianCalendar(2016, Calendar.MARCH, 13);
        SolarInfo info = new SolarInfo(date, BERLIN_LATITUDE, BERLIN_LONGITUDE);

        GregorianCalendar expected = new GregorianCalendar(timeZone);
        expected.set(2016, Calendar.MARCH, 13, 6, 24, 18);
        expected.set(Calendar.MILLISECOND, 0);

        GregorianCalendar actual = info.getSunRise();

        assertEquals("expected: " + expected.getTime() + ", actual: " + actual.getTime(), expected, actual);
    }

    @org.junit.Test
    public void testGetSunRiseAtDstBorder() throws Exception {
        GregorianCalendar date = new GregorianCalendar(2016, Calendar.MARCH, 27);
        SolarInfo info = new SolarInfo(date, ENSCHEDE_LATITUDE, ENSCHEDE_LONGITUDE);

        GregorianCalendar expected = new GregorianCalendar(2016, Calendar.MARCH, 27, 7, 17, 27);

        GregorianCalendar actual = info.getSunRise();

        assertEquals("expected: " + expected.getTime() + " actual: " + actual.getTime(), expected, actual);
        assertEquals(0, date.get(Calendar.DST_OFFSET));
        assertEquals(3600000, actual.get(Calendar.DST_OFFSET));
    }

    @org.junit.Test
    public void testGetSunSet() throws Exception {
        GregorianCalendar date = new GregorianCalendar(2016, Calendar.MARCH, 9);
        SolarInfo info = new SolarInfo(date, ENSCHEDE_LATITUDE, ENSCHEDE_LONGITUDE);

        GregorianCalendar expected = new GregorianCalendar(2016, Calendar.MARCH, 9, 18, 27, 25);

        GregorianCalendar actual = info.getSunSet();

        assertEquals("expected: " + expected.getTime() + " actual: " + actual.getTime(), expected, actual);
    }

    @org.junit.Test
    public void testGetSunSet2() throws Exception {
        GregorianCalendar date = new GregorianCalendar(2016, Calendar.AUGUST, 24, 12, 0);
        SolarInfo info = new SolarInfo(date, LONGYEARBYEN_LATITUDE, LONGYEARBYEN_LONGITUDE);

        GregorianCalendar expected = new GregorianCalendar(2016, Calendar.AUGUST, 25, 0, 12, 21);

        GregorianCalendar actual = info.getSunSet();

        assertEquals("expected: " + expected.getTime() + " actual: " + actual.getTime(), expected, actual);
    }

    @org.junit.Test
    public void testGetSunSet2a() throws Exception {
        GregorianCalendar date = new GregorianCalendar(2016, Calendar.AUGUST, 25, 12, 0);
        SolarInfo info = new SolarInfo(date, LONGYEARBYEN_LATITUDE, LONGYEARBYEN_LONGITUDE);

        GregorianCalendar expected = new GregorianCalendar(2016, Calendar.AUGUST, 25, 23, 45, 35);

        GregorianCalendar actual = info.getSunSet();

        assertEquals("expected: " + expected.getTime() + " actual: " + actual.getTime(), expected, actual);
    }

    @org.junit.Test
    public void testGetSunSetAuckland() throws Exception {
        TimeZone timeZone = TimeZone.getTimeZone("Pacific/Auckland");
        GregorianCalendar date = new GregorianCalendar(timeZone);
        date.set(2016, Calendar.MARCH, 13, 12, 0);

        SolarInfo info = new SolarInfo(date, AUCKLAND_LATITUDE, AUCKLAND_LONGITUDE);

        GregorianCalendar expected = new GregorianCalendar(timeZone);
        expected.set(2016, Calendar.MARCH, 13, 19, 42, 27);
        expected.set(Calendar.MILLISECOND, 0);

        GregorianCalendar actual = info.getSunSet();

        assertEquals("expected: " + expected.getTime() + ", actual: " + actual.getTime(), expected, actual);
    }

    @org.junit.Test
    public void testGetSunSetHonolulu() throws Exception {
        TimeZone timeZone = TimeZone.getTimeZone("Pacific/Honolulu");
        GregorianCalendar date = new GregorianCalendar(timeZone);
        date.set(2016, Calendar.MARCH, 13, 12, 0);

        SolarInfo info = new SolarInfo(date, HONOLULU_LATITUDE, HONOLULU_LONGITUDE);

        GregorianCalendar expected = new GregorianCalendar(timeZone);
        expected.set(2016, Calendar.MARCH, 13, 18, 40, 36);
        expected.set(Calendar.MILLISECOND, 0);

        GregorianCalendar actual = info.getSunSet();

        assertEquals("expected: " + expected.getTime() + ", actual: " + actual.getTime(), expected, actual);
    }

    @org.junit.Test
    public void testGetSunSetAtDstBorder() throws Exception {
        GregorianCalendar date = new GregorianCalendar(2016, Calendar.MARCH, 27);
        SolarInfo info = new SolarInfo(date, ENSCHEDE_LATITUDE, ENSCHEDE_LONGITUDE);

        GregorianCalendar expected = new GregorianCalendar(2016, Calendar.MARCH, 27, 19, 58, 55);

        GregorianCalendar actual = info.getSunSet();

        assertEquals("expected: " + expected.getTime() + " actual: " + actual.getTime(), expected, actual);
        assertEquals(0, date.get(Calendar.DST_OFFSET));
        assertEquals(3600000, actual.get(Calendar.DST_OFFSET));
    }

    @org.junit.Test
    public void testPolarNight() throws Exception {
        GregorianCalendar date = new GregorianCalendar(2016, Calendar.FEBRUARY, 1);
        SolarInfo info = new SolarInfo(date, LONGYEARBYEN_LATITUDE, LONGYEARBYEN_LONGITUDE);

        assertFalse(info.isUpAllDay());
        assertTrue(info.isDownAllDay());
    }

    @org.junit.Test
    public void testPolarDay() throws Exception {
        GregorianCalendar date = new GregorianCalendar(2016, Calendar.JULY, 1);
        SolarInfo info = new SolarInfo(date, LONGYEARBYEN_LATITUDE, LONGYEARBYEN_LONGITUDE);

        assertTrue(info.isUpAllDay());
        assertFalse(info.isDownAllDay());
    }
}