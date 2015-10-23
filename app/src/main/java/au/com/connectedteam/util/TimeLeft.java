package au.com.connectedteam.util;

import java.util.Calendar;
import java.util.Date;

import android.graphics.Color;
import android.util.Log;


public class TimeLeft {

	// return TimeMode
	// 0 - Normal
	// 1 - Less than 10 minutes
	// 2 - Less than 1 hour
	// 3 - closed more than a day ago
	// 4 - open for longer than 2 days
	// 5 - open for longer than 30 days
	public static final int MODE_NORMAL = 0;
	public static final int MODE_LESS1HR = 1;
	public static final int MODE_LESS10M = 2;
	public static final int MODE_LONG_CLOSED = 3;
	public static final int MODE_LONG_OPEN = 4;
	public static final int MODE_VERY_LONG_OPEN = 5;

	//"burnt orange"
	public static final int COLOR_1HR = 0xFFCC5500;
	//public static final int COLOR_1HR = 5;
	public static final int COLOR_10M = 0xffcc0000;
	public static final int COLOR_NORMAL = 0xFF555555;

	public static final String ONEHOUR = "1hr";
	public static final String TENMINUTES = "10m";

	// appendDateString:
	// 0 - Auto (default), for the event date is 1 month later
	// 1 - Always
	// 2 - Never
	public static final int APPEND_AUTO = 0;
	public static final int APPEND_ALWAYS = 1;
	public static final int APPEND_NEVER = 2;
	public static final int APPEND_HOURS_MINS_ONLY = 3;
	public static final int APPEND_DAYS_ONLY_AFTER_30 = 4;

	//private static final String FMT = "dd/MM/yyyy HH:mm";

	private String timeLeft = "";
	private int color = COLOR_NORMAL;
	private int mode = MODE_NORMAL;

	private static TimeLeft NORMAL;
	private static TimeLeft LESS1HR;
	private static TimeLeft LESS10M;
	private static TimeLeft LONG_CLOSED;
	private static TimeLeft LONG_OPEN;
	private static TimeLeft VERY_LONG_OPEN;
	
	static final long SECONDINMILLIS = 1000;
	static final long MINUTEINMILLIS = 60 * SECONDINMILLIS;
	static final long HOURINMILLIS = 60 * MINUTEINMILLIS;
	static final long DAYINMILLIS = 24 * HOURINMILLIS;
	
	static final long LONG_TIME_AGO = (long) (-20*365.25d*DAYINMILLIS);
	
	static final String PAST_PREFIX = "- ";

	static {
		NORMAL = create(null, MODE_NORMAL, COLOR_NORMAL);
		LESS1HR = create(null, MODE_LESS1HR, COLOR_1HR);
		LESS10M = create(null, MODE_LESS10M, COLOR_10M);
		LONG_CLOSED = create(null, MODE_LONG_CLOSED, COLOR_NORMAL);
		LONG_OPEN = create(null, MODE_LONG_OPEN, Color.BLACK);
		VERY_LONG_OPEN = create(null, MODE_VERY_LONG_OPEN, Color.BLACK);
	}

	private static TimeLeft create(String timeLeft, int mode, int color) {
		TimeLeft obj = new TimeLeft();
		obj.timeLeft = timeLeft;
		obj.mode = mode;
		obj.color = color;
		return obj;
	}

	/**
	 * Returns a Time Left string.
	 * 
	 * @param timeSpan
	 * @param timeMode
	 * @param appendMode
	 * @param extraText
	 *            Only append when the appendMode is Auto or Always.
	 * @return
	 */
	public static TimeLeft get(final long timeSpan, int appendMode) {

		TimeLeft timeLeft;

		boolean finished = timeSpan <= -1000;
		String prex = finished ? PAST_PREFIX : "";

		
		long days, hours;
		if(appendMode==APPEND_HOURS_MINS_ONLY){
			days=0;
			hours = timeSpan/HOURINMILLIS;
		}
		else{
			days = timeSpan / DAYINMILLIS;
			hours = (timeSpan - days * DAYINMILLIS) / HOURINMILLIS;
		}
		long minutes = (timeSpan - days * DAYINMILLIS - hours * HOURINMILLIS)
				/ MINUTEINMILLIS;
		long seconds = (timeSpan - days * DAYINMILLIS - hours * HOURINMILLIS - minutes
				* MINUTEINMILLIS)
				/ SECONDINMILLIS;

		if (days >= 1) {
			String extraText;
			if(days>30)
				timeLeft = TimeLeft.VERY_LONG_OPEN;
			else if(days>=2)
				timeLeft= TimeLeft.LONG_OPEN;
			else
				timeLeft= TimeLeft.NORMAL;

			if((appendMode == APPEND_DAYS_ONLY_AFTER_30) && days>30){

				timeLeft.timeLeft = days+"d";
				return timeLeft;
			}
			
			if((appendMode == APPEND_AUTO || appendMode== APPEND_ALWAYS) && days>30){
				
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis() + timeSpan);
				timeLeft.timeLeft = StringUtils.formatDate(cal.getTime(), StringUtils.DATE_STANDARD);
				return timeLeft;
			}
			/*
			else if (appendMode == APPEND_ALWAYS
					|| (appendMode == APPEND_AUTO && days >= 2 )) {
				
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis() + timeSpan);
				extraText = " (" + StringUtils.formatDate(cal.getTime(), StringUtils.DATE_TIMELEFT) + ")";
			}*/ else {
				//APPEND_NEVER
				extraText = "";
			}

			timeLeft.timeLeft = days + "d " + hours + "h" + extraText;
			return timeLeft;

		}

		if(Math.abs(minutes)>62){
			Log.d("TimeLeft", "min?? "+minutes);
		}
		if(Math.abs(hours)>25 ){
			Log.d("TimeLeft", "hr?? "+hours);
		}

		if (days == 0 && hours == 0) {

			if (minutes < 10 && minutes>-10) {

				timeLeft = TimeLeft.LESS10M;
				
				timeLeft.timeLeft = prex + Math.abs(minutes) + "m "
						+ Math.abs(seconds) + "s";
				
				return timeLeft;

			}

			if (minutes < 60 && minutes>-10) {

				timeLeft = TimeLeft.LESS1HR;
				timeLeft.timeLeft = minutes + "m";
				return timeLeft;

			}

		}
		if (timeSpan<LONG_TIME_AGO){
			//so long ago that we probably don't care
			timeLeft=TimeLeft.LONG_CLOSED;
			timeLeft.timeLeft="";
			return timeLeft;
		}
		//if (timeSpan<=-20L*minuteInMillis){
		if (days<=-1){
			timeLeft=TimeLeft.LONG_CLOSED;
			if(appendMode==APPEND_ALWAYS || appendMode==APPEND_AUTO){
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis() + timeSpan);
				timeLeft.timeLeft = StringUtils.formatDate(cal.getTime(), StringUtils.DATE_TIMELEFT);
			}
			else{				
				timeLeft.timeLeft="Expired";
			}			
			return timeLeft;
		}

		timeLeft = finished ? TimeLeft.LESS10M : TimeLeft.NORMAL;
		if(hours!=0 && minutes!=0){
			timeLeft.timeLeft = prex + Math.abs(hours) + "h " + Math.abs(minutes)
				+ "m";
		}
		else if (hours!=0){
			timeLeft.timeLeft = prex + Math.abs(hours) + "h";
		}
		else{
			timeLeft.timeLeft = prex + Math.abs(minutes) + "m";
		}
		return timeLeft;

	}
	
	/**
	 * Formats timeleft into 'Xm Ys ago' for times in the past, 'in Xm Ys' for times in the future, or
	 * just the date for times long in the past or long in the future
	 * @param localDate
	 * @return
	 */
	public static TimeLeft getPast(Date localDate) {

		TimeLeft result = get(localDate, null, APPEND_ALWAYS);
		String text = result.getThisText();
		if(StringUtils.isNullOrEmpty(text)){
			text="";
		}
		else if(text.startsWith(PAST_PREFIX)){
			text = text.replaceFirst(PAST_PREFIX, "") + " ago";
		}
		else if (result.getThisMode()==MODE_VERY_LONG_OPEN || result.getThisMode()==MODE_LONG_CLOSED){
			//just the date
		}
		else {
			text = "in "+text;
		}
		result.timeLeft=text;
		return result;
	}

	public static TimeLeft get(Date localDate, Date serverDate) {

		return get(localDate, serverDate, APPEND_AUTO);
	}

	public static TimeLeft get(Date localDate, Date serverDate, int appendMode) {
		long timespan;
		//if (serverDate == null) {
			//serverDate = new Date();
		//}
		if(localDate.getTime()<=1){
			timespan=Long.MIN_VALUE;
		}
		else{
			long server;
			if(serverDate==null){
				server=System.currentTimeMillis();
			}
			else server = serverDate.getTime();
			timespan = localDate.getTime() - server;
		}

		/*
		String extDate = "";

		SimpleDateFormat format = new SimpleDateFormat(FMT);
		format.setTimeZone(TimeZone.getDefault());
		extDate = " (" + format.format(localDate) + ")";
		*/
		
		return get(timespan, appendMode);
	}

	/*
	public int getThisBackgroundResource(boolean button){
		switch(this.color){
		case COLOR_10M:
			return button?R.drawable.timeleft_background_10m_button:R.drawable.timeleft_background_10m;
		case COLOR_1HR:
			return button?R.drawable.timeleft_background_1hr_button:R.drawable.timeleft_background_1hr;			
		case COLOR_NORMAL:
		case Color.BLACK:
			default:
				
				return button?R.drawable.timeleft_background_normal_button: R.drawable.timeleft_background_normal;
		}
	}*/
	public int getThisColour(){
		return this.color;
	}
	
	public String getThisText(){
		return this.timeLeft;
	}
	
	public int getThisMode(){
		return this.mode;
	}
	@Override
	public String toString(){
		return this.timeLeft;
	}
	
	/**
	 * Checks a date is from the last ~20 years
	 * @param date
	 * @return
	 */
	public static boolean isSane(Date date){
		long timeSpan = date.getTime()-System.currentTimeMillis();
		return timeSpan<LONG_TIME_AGO;
			//so long ago that we probably don't care
	}

}
