package core.util;

import core.env.Environment;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;

public class DateTimeUtil {

    private static final String ZONE_ID = Environment.TIME_ZONE_ID;
    private static final String format = "yyyy/MM/dd";

    public static String getTimeStamp(String format) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        ZoneId zoneId = ZoneId.of(ZONE_ID);
        return Instant.now().atZone(zoneId).format(dtf);
    }

    private static String endOfNextMonth(String date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        LocalDate convertedDate = LocalDate.parse(date, dtf).plusMonths(1);
        convertedDate = convertedDate.withDayOfMonth(convertedDate.getMonth().length(convertedDate.isLeapYear()));
        return convertedDate.format(dtf);
    }

    private static String firstDayOfCurrentMonthLastYear(String date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        LocalDate convertedDate = LocalDate.parse(date, dtf).minusYears(1);
        convertedDate = convertedDate.withDayOfMonth(1);
        return convertedDate.format(dtf);
    }

    public static String plusDays(String date, long daysToAdd) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        LocalDate convertedDate = LocalDate.parse(date, dtf).plusDays(daysToAdd);
        return convertedDate.format(dtf);
    }

    public static String minusDays(String date, long daysToSubtract) {
        return plusDays(date, -daysToSubtract);
    }

    private static String after(String date) {
        return plusDays(date, 1);
    }

    private static String before(String date) {
        return plusDays(date, -1);
    }

    public static String get(String type) {
        switch (type.toLowerCase()) {
            case "current":
                return getTimeStamp(format);
            case "endofnextmonth":
                return endOfNextMonth(getTimeStamp(format));
            case "firstdayofcurrentmonthlastyear":
                return firstDayOfCurrentMonthLastYear(getTimeStamp(format));
            case "time":
                String formatTime = "yyyy/MM/dd HH:mm:ss";
                return getTimeStamp(formatTime);
            case "after":
                return after(getTimeStamp(format));
            case "before":
                return before(getTimeStamp(format));
            default:
                throw new IllegalArgumentException(type + " is invalid!");
        }
    }

    public static long isBetween(String format, String dateTime1, String dateTime2, TemporalUnit temporalUnit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime expDateTime = LocalDateTime.parse(dateTime1, formatter);
        LocalDateTime actDateTime = LocalDateTime.parse(dateTime2, formatter);
        return temporalUnit.between(expDateTime, actDateTime);
    }
}
