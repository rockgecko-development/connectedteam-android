package au.com.connectedteam.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;

import au.com.connectedteam.R;
import au.com.connectedteam.application.ConnectedApp;


public class StringUtils {

    public static final double T_DIVIDEND = -1;
    public static final double T_EMPTY = -2;

    public static final String NUM_STANDARD = "%.2f";
    public static final String NUM_XX = NUM_STANDARD;
    public static final String NUM_XXXX = "%.4f";


    public static final String DATE_USA_DOB = "MM/dd/yyyy";

    public static final String DATE_LONG = "dd MMM yyyy";
    public static final String DATE_AND_TIME_LONG = "dd/MM/yyyy hh:mm:ss a";
    public static final String DATE_STANDARD = "dd/MM/yyyy";
    public static final String DATE_SHORT = "dd/MM/yy";
    public static final String DATE_SHORT_TIME = "EEE dd/MM HH:mm";
    public static final String DATE_GB = "yyyy/MM/dd HH:mm:ss";
    public static final String DATE_WEEKDAY = "EEE HH:mm";
    public static final String DATE_MONTHYEAR = "MM/yyyy";
    public static final String DATE_AND_TIME_STANDARD = "dd/MM/yyyy HH:mm";
    public static final String DATE_TIMELEFT = "dd/MM/yyyy HH:mm";

    public static final String DATE_DOTNET_DATETIME_NOZONE = "yyyy-MM-dd'T'HH:mm:ss";

    public static final String SPDASH = " - ";
    public static final String SEP = " | ";
    public static final String TRADEMARK = new String(new char[]{'\u2122'});
    public static final String ELIPSIS = new String(new char[]{'\u2026'});
    public static final String TICK = new String(new char[]{'\u2713'});
    public static final String BULLET = new String(new char[]{'\u2022'});
    public static final String EMDASH = new String(new char[]{'\u2014'});


    /**
     * Warning you don't want to change these, they are used to create bet selection strings!
     */
    public static final String COMMA = ",";
    public static final String COLON = ":";
    public static final String SEMICOLON = ";";
    public static final String DASH = "-";
    public static final String EMPTY = "";
    public static final String FIELD = "F";


    private static DecimalFormat MONEY_FORMAT;
    private static DecimalFormat MONEY_WHOLE_DOLLARS_FORMAT;

    static{
        MONEY_FORMAT = new DecimalFormat("$#,##0.00");
        MONEY_FORMAT.setRoundingMode(RoundingMode.HALF_UP);
        MONEY_WHOLE_DOLLARS_FORMAT = new DecimalFormat("$#,###");
        MONEY_WHOLE_DOLLARS_FORMAT.setRoundingMode(RoundingMode.HALF_UP);
    }



    /**
     * Format a number by specified format pattern, in the US locale
     * (ensures numbers always use a decimal point, never a comma as per European locales)
     *
     * @param number
     *            The number to get the format string.
     * @param pattern
     *            Format pattern.
     * @return String of the formatted number.
     */
    public static String formatNumber(double number, String pattern) {
        pattern = isNullOrEmpty(pattern, NUM_STANDARD);
        String strValue;
        if (number==0){
            strValue=SPDASH;
        }
        else{
            strValue= String.format(Locale.US, pattern, number);
        }
        return strValue;
    }

    public static String formatNumberXX(double number) {

        return formatNumber(number, NUM_XX);
    }

    public static String formatNumberXXKeep0(double number) {
        if(number==0) return "0.00";

        return formatNumber(number, NUM_XX);
    }

    public static String formatNumberXXXX(double number) {

        return formatNumber(number, NUM_XXXX);
    }

    public static String formatNumberXXXXKeep0(double number) {
        if(number==0) return "0.0000";

        return formatNumber(number, NUM_XXXX);
    }

    public static String formatMoney(Number number){
        return formatMoney(number, true);
    }
    public static String formatMoney(Number number, boolean dashIfZero){
        if (dashIfZero && (isNullOrEmpty(number))){
            return SPDASH;
        }
        return MONEY_FORMAT.format(number);
    }

    public static String formatWholeDollars(Number number){
        return formatWholeDollars(number, true);
    }
    public static String formatWholeDollars(Number number, boolean dashIfZero){
        if (dashIfZero && (isNullOrEmpty(number))){
            return SPDASH;
        }
        return MONEY_WHOLE_DOLLARS_FORMAT.format(number);
    }

    /**
     * Uses the default standard format pattern to convert a number to string.
     *
     * @param number
     * @return
     */
    public static String toStandard(double number) {
        return toStandard(number, null);
    }

    public static String toStandard(double number, String replace) {
        String res = formatNumber(number, null);
        if (res.length() == 0)
            return replace;
        return res;
    }

    /**
     * Get a formatted string from a date time string in JSON format (e.g.
     * /Date(2311124231)/).
     *
     * @param jsonDate
     *            JSON format date time string
     * @param pattern
     *            null or format pattern. If it's null, the Australia standard
     *            format will be used.
     * @return
     */
    public static String formatDateFromJson(String jsonDate, String pattern) {

        pattern = isNullOrEmpty(pattern, DATE_AND_TIME_LONG);
        Date date = getDateFromJson(jsonDate);

        return formatDate(date, pattern);

    }

    public static String getJsonStringFromDate(Date value) {
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTime(value);
        long millis = calendar.getTimeInMillis();
        return "/Date(" + millis + ")/";
    }

    public static Date getDateFromJson(String jsonDate) {

        // Remove prefix and suffix extra string information
        String dateString = jsonDate.replace("/Date(", "").replace(")/", "");

        // Split date and timezone parts
        String[] dateParts = dateString.split("[+-]");

        // The date must be in milliseconds since January 1, 1970 00:00:00 UTC
        // We want to be sure that it is a valid date and time, aka the use of
        // Calendar
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTimeInMillis(Long.parseLong(dateParts[0]));

        // If you want to play with time zone:
        if (dateParts.length > 1) {
            calendar.setTimeZone(TimeZone.getTimeZone(dateParts[1]));
        } else {
            // Note: When the user select
            // "Automatic - Use network-provided values" for their Date Time
            // settings in system. The getDefault() will be always return GMT+0
            // rather than the real time zone from providers.
            // To use the correct time zone, user need to unselect the
            // "Automatic" and
            // set their time zone correctly.
            calendar.setTimeZone(TimeZone.getDefault());
        }

        // Read back and look at it, it must be the same
        // long timeinmilliseconds = calendar.getTimeInMillis();

        // Convert it to a Date() object now:
        Date date = calendar.getTime();

        return date;


    }

    public static int getDaysBetween(Calendar from, Calendar to){
        boolean swap = false;
        Calendar fromClone, toClone;
        if(to.before(from)){
            fromClone = (Calendar) to.clone();
            toClone=(Calendar) from.clone();
            swap =true;
        }
        else{
            fromClone = (Calendar) from.clone();
            toClone=(Calendar) to.clone();
        }
        int daysBetween=0;
        while(fromClone.before(toClone)){
            fromClone.add(Calendar.DAY_OF_YEAR, 1);
            daysBetween++;
        }
        return swap?-daysBetween: daysBetween;

    }
	
	/*
	 * <xs:element name="duration" nillable="true" type="tns3:duration"/>
  <xs:simpleType name="duration">
    <xs:restriction base="xs:duration">
      <xs:pattern value="\-?P(\d*D)?(T(\d*H)?(\d*M)?(\d*(\.\d*)?S)?)?"/>
      <xs:minInclusive value="-P10675199DT2H48M5.4775808S"/>
      <xs:maxInclusive value="P10675199DT2H48M5.4775807S"/>
    </xs:restriction>
  </xs:simpleType>
	 */
    /**
     * Eg "PxDTxHxMxS", "PT8H15M"
     * @param timeSpan
     * @return
     */
    public static long parseTimeSpan(String timeSpan){
        Pattern pattern = Pattern.compile("-?P(\\d*D)?(T(\\d*H)?(\\d*M)?(\\d*(\\.\\d*)?S)?)?");
        Matcher matcher = pattern.matcher(timeSpan);

        if(matcher.matches()){
            long time = 0;
            boolean negative = timeSpan.startsWith("-P");
            if(!isNullOrEmpty(matcher.group(1))){
                // (\\d*D)
                long days = Long.parseLong(sliceString(matcher.group(1), 0 , -1));
                time+=DateUtils.DAY_IN_MILLIS*days;
            }
            if(!isNullOrEmpty(matcher.group(3))){
                // (\\d*H)
                long hours = Long.parseLong(sliceString(matcher.group(3), 0, -1));
                time+=DateUtils.HOUR_IN_MILLIS*hours;
            }
            if(!isNullOrEmpty(matcher.group(4))){
                // (\\d*M)
                long minutes = Long.parseLong(sliceString(matcher.group(4), 0, -1));
                time+=DateUtils.MINUTE_IN_MILLIS*minutes;
            }
            if(!isNullOrEmpty(matcher.group(5))){
                // (\\d*(\\.\\d*)?S)
                double seconds = Double.parseDouble(sliceString(matcher.group(5), 0, -1));
                time+=DateUtils.SECOND_IN_MILLIS*seconds;
            }

            return negative?-time:time;
        }
        return 0;
    }

    /**
     *
     * @param date
     * @param pattern
     * @param tz timezone
     * @return
     */
    public static Date parseDateMR(String date, String pattern, TimeZone tz){
        pattern = isNullOrEmpty(pattern, DATE_STANDARD);

        SimpleDateFormat formatter = new SimpleDateFormat(pattern,
                Locale.US);
        formatter.setTimeZone(tz);
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            return new Date(0);
        }
    }

    /**
     *
     * @param date
     * @param pattern
     * @param tz timezone, or null for the user's current timezone
     * @return
     */
    public static Date parseDate(String date, String pattern, TimeZone tz){
        pattern = isNullOrEmpty(pattern, DATE_STANDARD);
        if (tz==null) tz = TimeZone.getDefault();
        SimpleDateFormat formatter = new SimpleDateFormat(pattern,
                Locale.getDefault());
        formatter.setTimeZone(tz);
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            return new Date(0);
        }
    }

    /**
     * Parse a date, using UTC timezone
     * @param date
     * @param pattern
     * @return
     */
    public static Date parseDateUTCMR(String date, String pattern){
        return parseDateMR(date, pattern, TimeZone.getTimeZone("UTC"));
		/*
		pattern = isNullOrEmpty(pattern, DATE_STANDARD);
		SimpleDateFormat formatter = new SimpleDateFormat(pattern,
				Locale.getDefault());
		try {
			return formatter.parse(date);
		} catch (ParseException e) {
			return new Date(0);
		}*/
    }
    public static String formatDatePC(Date date, String pattern) {
        return formatDateMR(date, pattern, TimeZone.getTimeZone(TimeUtil.PICKCHAMPS_TIMEZONE));
    }

    public static String formatDateUTCMR(Date date, String pattern) {
        return formatDateMR(date, pattern, TimeZone.getTimeZone("UTC"));
    }
    public static String formatDateMR(Date date, String pattern) {
        return formatDateMR(date, pattern, null);
    }
    public static String formatDateMR(Date date, String pattern, TimeZone tz) {

        pattern = isNullOrEmpty(pattern, DATE_STANDARD);

        if (date == null) {
            date = new Date();
        }
        if (tz==null) tz = TimeZone.getDefault();
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.US);
        formatter.setTimeZone(tz);

        return formatter.format(date);

    }
    public static String formatDate(Date date, String pattern) {

        pattern = isNullOrEmpty(pattern, DATE_STANDARD);

		/*
		Calendar calendar = Calendar.getInstance();
		if (date == null) {
			date = new Date();
			calendar.setTime(date);
		} else {
			calendar.setTime(date);
		}*/
        if (date == null) {
            date = new Date();
        }

        SimpleDateFormat formatter = new SimpleDateFormat(pattern,
                Locale.getDefault());
        return formatter.format(date);

    }

    /** Clears out the hours/minutes/seconds/millis of a Calendar. */
    public static void setMidnight(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    public static String toStandard(Date date) {
        return formatDateStandard(date);
    }

    public static String formatDateStandard(Date date) {
        return formatDate(date, null);
    }

    public static String formatDateMonthYear(Date date) {

        return formatDate(date, DATE_MONTHYEAR);
    }

    public static String formatDateLong(Date date) {

        return formatDate(date, DATE_LONG);
    }

    public static String formatDateGB(Date date) {
        return formatDate(date, DATE_GB);
    }

    public static String formatDateShort(Date date) {

        return formatDate(date, DATE_SHORT);
    }
    public static String formatDateShortTime(Date date) {

        return formatDate(date, DATE_SHORT_TIME);
    }

    public static String formatDateWeekday(Date date) {

        return formatDate(date, DATE_WEEKDAY);
    }

    public static String formatDateTimeLeft(Date date) {

        return formatDate(date, DATE_TIMELEFT);
    }

    public static String formatTimeOnlyLowercase(Date date){
        return formatDate(date, "h:mma").toLowerCase();
    }

    /**
     * Adds an HTML tag before and after the text string.
     *
     * @param text
     * @param tag
     * @return
     */
    public static String setHtmlTag(String text, String tag) {

        String tag1 = tag;
        int pos = -1;
        if ((pos = tag1.indexOf(" ")) > 0) {
            tag1 = tag1.substring(0, pos);
        }

        if (text == null) {
            text = "";
        }
        //else{
        //text=text.replace("<", "&lt;").replace(">", "&gt;");
        //}

        return "<" + tag + ">" + text + "</" + tag1 + ">";

    }
    public static String colourToHTML(int colour){
        return String.format(Locale.US, "#%06X", (0xFFFFFF & colour));
    }

    public static <T> T isNullOrEmpty(T obj, T replace) {

        if (obj == null)
            return replace;
        if ("null".equals(obj.toString()))
            return replace;

        if (obj.toString().length() == 0)
            return replace;

        return obj;

    }
    public static boolean isNullOrEmpty(CharSequence string) {
        return string == null || string.length() == 0;
    }
    public static boolean isAnyNullOrEmpty(String... strings) {
        if(strings==null || strings.length==0) return true;
        boolean isNull=false;
        for(String string : strings){
            if (string==null || string.length()==0){
                isNull=true;
                break;
            }
        }
        return isNull;
    }

    public static boolean isNullOrEmpty(List<?> list){
        return list == null || list.size() == 0;
    }
    public static boolean isNullOrEmpty(int[] list){
        return list == null || list.length == 0;
    }
    public static boolean isNullOrEmpty(Object[] list){
        return list == null || list.length == 0;
    }
    public static boolean isNullOrEmpty(Number number){
        return number==null || number==0 || number==0d || number.equals(BigDecimal.ZERO)
                || (number instanceof BigDecimal && BigDecimal.ZERO.compareTo((BigDecimal) number)==0)
                || number.toString().equals("0");

    }

    public static int getValueOrDefault(Integer in){
        return isNullOrEmpty(in)?0:in;
    }
    public static long getValueOrDefault(Long in){
        return isNullOrEmpty(in)?0L:in;
    }
    public static double getValueOrDefault(Double in){
        return isNullOrEmpty(in)?0d:in;
    }
    public static BigDecimal getValueOrDefault(BigDecimal in){
        return isNullOrEmpty(in)?BigDecimal.ZERO:in;
    }
    public static boolean getValueOrDefault(Boolean in){
        return Boolean.TRUE.equals(in);
    }

    public static String dashIfEmpty(String in){
        if(isNullOrEmpty(in))
            return SPDASH;
        else
            return in;
    }

    public static String sliceString(String in, int left, int right){

        int start, end;
        if(isNullOrEmpty(in) || left>in.length()) return "";
        else if(left<0) start = in.length()+left;
        else start = left;

        start = Math.max(0, start);

        if(right>in.length()) end = in.length();
        else if (right<0) end = in.length()+right;
        else end = right;

        end = Math.max(0, end);

        if(start>end) return "";

        return in.substring(start, end);
    }

    public static String truncate(String in, int maxLength){
        if(isNullOrEmpty(in)) return in;
        if(in.length()<=maxLength) return in;
        return in.trim().substring(0, maxLength-1)+ELIPSIS;

    }


    /**
     * Inflate a list of string resource IDs into one string, with tokens between strings
     * @param resources String resource IDs from R.string...
     * @param token eg "\n"
     * @return
     */
    public static String inflateStringResourceArray(int[] resources, String token){
        if (resources!=null){
            String returnStr = "";
            for (int element : resources){
                if(element!=0){
                    if (returnStr.length()==0) returnStr = getContext().getString(element);
                    else returnStr = returnStr + token + getContext().getString(element);
                }
            }
            return returnStr;
        }
        else return "";
    }

    private static Context getContext(){
        return ConnectedApp.getContextStatic();
    }

    public static String reverseChars(String in){
        if(StringUtils.isNullOrEmpty(in)) return "";
        int len = in.length();

        char[] out = new char[in.length()];
        for(int i=0;i<len;i++){
            out[len-i-1]=in.charAt(i);
        }
        return new String(out);
    }

    public static String stringArrayToString(int[] in, String token, boolean filterDuplicates){
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<in.length;i++){
            if(filterDuplicates && i>0 && in[i-1]==(in[i])){
                continue;
            }
            builder.append(in[i]);
            if(i<in.length-1){
                builder.append(token);
            }
        }
        String result = builder.toString();
        if (result.endsWith(token)){
            return result.substring(0, result.length()-token.length());
        }
        return result;
    }

    public static String stringArrayToString(Object[] in, String token, boolean filterDuplicates){
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<in.length;i++){
            if(filterDuplicates && i>0 && in[i-1].equals(in[i])){
                continue;
            }
            builder.append(in[i]);
            if(i<in.length-1){
                builder.append(token);
            }
        }
        String result = builder.toString();
        if (result.endsWith(token)){
            return result.substring(0, result.length()-token.length());
        }
        return result;
    }

    public static String stringListToString(List<?> in, String token, boolean filterDuplicates){
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<in.size();i++){
            if(filterDuplicates && i>0 && in.get(i-1).equals(in.get(i))){
                continue;
            }
            builder.append(in.get(i));
            if(i<in.size()-1){
                builder.append(token);
            }
        }
        String result = builder.toString();
        if (result.endsWith(token)){
            return result.substring(0, result.length()-token.length());
        }
        return result;
    }

    /**
     * Returns "Yesterday", "Today", "Tomorrow", "last DDDD", or the name of the day
     * from today (eg "Tuesday")
     * @param daysFromToday 0 for today
     * @return
     *
    public static String getDayAsString(int daysFromToday){
    if (daysFromToday==-1){
    return getContext().getString(R.string.yesterday);
    }
    else if (daysFromToday==0){
    return getContext().getString(R.string.today);
    }
    else if (daysFromToday==1){
    return getContext().getString(R.string.tomorrow);
    }
    else{
    Calendar cal = DateUtil.getApiLocalisedCalendar();
    cal.add(Calendar.DAY_OF_YEAR, daysFromToday);
    return formatDateStandard(cal.getTime());
    /*
    DateFormat df = new SimpleDateFormat("EEEE");
    String retStr = df.format(cal.getTime());
    if(daysFromToday<0){
    return "last "+retStr;
    }
    return retStr;*
    }

    }

    /**
     * Returns "Yesterday", "Today", "Tomorrow", "last DDDD", or the name of the day
     * from today (eg "Tuesday")
     * @param daysFromToday 0 for today
     * @return
     *
    public static String getDayAsStringAPIDate(int daysFromToday){

    Calendar cal = DateUtil.getApiLocalisedCalendar();
    cal.add(Calendar.DAY_OF_YEAR, daysFromToday);

    Calendar now = Calendar.getInstance();

    SimpleDateFormat formatter = new SimpleDateFormat(DATE_STANDARD,
    Locale.US);
    formatter.setCalendar(cal);
    String dateString = formatter.format(cal.getTime());

    if(now.get(Calendar.YEAR)!=cal.get(Calendar.YEAR)) return dateString;

    int localToday = now.get(Calendar.DAY_OF_YEAR);
    int serverDate = cal.get(Calendar.DAY_OF_YEAR);


    if(serverDate==localToday && daysFromToday==0){
    return getContext().getString(R.string.today);
    }
    else if(serverDate==localToday+1 && daysFromToday==1){
    return getContext().getString(R.string.tomorrow);
    }
    else if (serverDate==localToday-1 && daysFromToday==-1){
    return getContext().getString(R.string.yesterday);
    }


    return dateString;


    }
     */


    /**
     * Converts a number into its ordinal string representation, eg "1st", "2nd" etc
     * @param value
     * @return
     */
    public static String numberAsOrdinalString(int value){
        return value+getOrdinal(value);
    }
    public static SpannableStringBuilder numberAsOrdinalSuperScript(int value){
        SpannableStringBuilder sb = new SpannableStringBuilder(""+value);
        appendSpan(sb, getOrdinal(value), new SuperscriptSpan(), new RelativeSizeSpan(0.5f));
        return sb;
    }

    public static String getOrdinal(int value){
        if(value<=0){
            return "";
        }
        int hunRem = value % 100;
        int tenRem = value % 10;
        if (hunRem - tenRem == 10) {
            return "th";
        }
        switch (tenRem) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    /**
     * To Title Case.
     * @param input
     * @return
     */
    public static String toTitleCase(String input){
        if(StringUtils.isNullOrEmpty(input)) return "";
        String[] words = input.trim().split(" ");
        StringBuilder sb = new StringBuilder();
        if (words[0].length() > 0) {
            for (int i = 0; i < words.length; i++) {
                if(i>0)sb.append(" ");
                sb.append(Character.toUpperCase(words[i].charAt(0)) + words[i].subSequence(1, words[i].length()).toString().toLowerCase());
            }
        }
        String retVal = sb.toString();

        return retVal;
        //return sb.toString();
    }

    /**
     * To sentence case.
     * @param input
     * @return
     */
    public static String toSentenceCase(String input){
        if(StringUtils.isNullOrEmpty(input)) return "";
        String[] words = input.trim().split(" ");
        StringBuilder sb = new StringBuilder();
        if (words[0].length() > 0) {
            sb.append(Character.toUpperCase(words[0].charAt(0)) + words[0].subSequence(1, words[0].length()).toString().toLowerCase());
            for (int i = 1; i < words.length; i++) {
                sb.append(" ");
                sb.append(words[i].toLowerCase());
            }
        }
        return sb.toString();
    }

    public static String inputStreamToString(InputStream is) throws IOException  {

        final char[] buffer = new char[16];
        final StringBuilder out = new StringBuilder();
        try {
            final Reader in = new InputStreamReader(is, "UTF-8");
            try {
                while (true) {
                    int rsz = in.read(buffer, 0, buffer.length);
                    if (rsz < 0)
                        break;
                    out.append(buffer, 0, rsz);
                }
            }

            finally {
                try { in.close(); }
                catch (Exception ex) {  }
            }
        }
        catch (UnsupportedEncodingException ex) {

        }
        return out.toString();

    }



    /**
     * Appends the character sequence {@code text} and spans {@code what} over the appended part.
     * See {@link android.text.Spanned} for an explanation of what the flags mean.
     * @param text the character sequence to append.
     * @param what the object(s) to be spanned over the appended text.

     * @return this {@code SpannableStringBuilder}.
     */
    public static void appendSpan(SpannableStringBuilder builder, CharSequence text, Object... what) {
        int start = builder.length();
        builder.append(text);
        for(Object obj : what) {
            builder.setSpan(obj, start, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

    }

}
