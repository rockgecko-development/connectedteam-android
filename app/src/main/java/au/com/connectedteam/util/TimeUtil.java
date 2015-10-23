package au.com.connectedteam.util;

import android.text.format.Time;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by bramleyt on 30/06/2015.
 */
public class TimeUtil {

    public static final String PICKCHAMPS_TIMEZONE = "America/New_York";

    public static boolean sameDayPC(Date lhs, Date rhs){
        return sameDayTZ(lhs, rhs, TimeZone.getTimeZone(PICKCHAMPS_TIMEZONE));
    }
    public static boolean sameDayLocal(Date lhs, Date rhs){
        return sameDayTZ(lhs, rhs, TimeZone.getDefault());
    }
    public static boolean sameDayUTC(Date lhs, Date rhs){
        return sameDayTZ(lhs, rhs, TimeZone.getTimeZone("UTC"));
    }
    public static boolean sameDayTZ(Date lhs, Date rhs, TimeZone tz){
        if(lhs==null || rhs==null)return false;
        Calendar lhsCal = Calendar.getInstance();
        lhsCal.setTimeZone(tz);
        lhsCal.setTime(lhs);
        Calendar rhsCal = Calendar.getInstance();
        rhsCal.setTimeZone(tz);
        rhsCal.setTime(rhs);
        return lhsCal.get(Calendar.DAY_OF_YEAR)==rhsCal.get(Calendar.DAY_OF_YEAR)
                && lhsCal.get(Calendar.YEAR)==rhsCal.get(Calendar.YEAR);

    }

    public static Calendar getPCCalendar(){
        return Calendar.getInstance(TimeZone.getTimeZone(PICKCHAMPS_TIMEZONE), Locale.US);
    }

    public static boolean sameDay(Time lhs, Time rhs){
        if(lhs==null || rhs==null)return false;
        return lhs.yearDay==rhs.yearDay && lhs.year==rhs.year;
    }

    /** Clears out the hours/minutes/seconds/millis of a Calendar. */
    public static void setMidnight(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    public static Calendar timeToCalendarLocal(Time time){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time.toMillis(true));
        return cal;
    }
}
