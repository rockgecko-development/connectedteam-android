package au.com.connectedteam.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.method.TransformationMethod;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import au.com.connectedteam.R;
import au.com.connectedteam.application.ConnectedApp;

import org.w3c.dom.Text;


public class UIUtil {
	private static Typeface robotoTypeFace;
	private static String robotoTypeFaceFontName="";
	private static HashMap<String, Typeface> typeFaceCache = new HashMap<String, Typeface>(6);
	
	public static final String ROBOTO_LIGHT = "fonts/Roboto-Light.ttf";
	public static final String ROBOTO_REGULAR = "fonts/Roboto-Regular.ttf";
	public static final String ROBOTO_THIN = "fonts/Roboto-Thin.ttf";
	public static final String ROBOTO_MEDIUM = "fonts/Roboto-Medium.ttf";
	public static final String ROBOTO_BOLD = "fonts/Roboto-Bold.ttf";
	
	/*private static Paint mPaint;
	private static final int TEXT_SIZE_DIP = 12;
	private static final int TEXT_COLOUR = Color.WHITE;*/
	public static  float density;
	public static int densityDpi;
	
	static{		
		try{
		density = ConnectedApp.getContextStatic().getResources().getDisplayMetrics().density;
		densityDpi = ConnectedApp.getContextStatic().getResources().getDisplayMetrics().densityDpi;
		}
		catch(Exception e){
			density = 1;
			densityDpi = DisplayMetrics.DENSITY_DEFAULT;
			 
		}
	

	}
	
	public static  boolean setSelectedAdapterItem(AdapterView<?> spinner, Object selection){
		Adapter adapter = spinner.getAdapter();
		for(int i=0;i<adapter.getCount(); i++){
			Object item = adapter.getItem(i);
			if (selection.equals(item)){
				spinner.setSelection(i);
				return true;
			}
			else if (item!=null && selection!=null && item.toString().equals(selection.toString())){
				spinner.setSelection(i);
				return true;
			}
		}
		return false;
		
	}

	public static String generateGravatarHash(String email){
		if(email==null) return null;
		return md5Hex(email.toLowerCase().trim());
	}

	public static String hex(byte[] array) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; ++i) {
			sb.append(Integer.toHexString((array[i]
					& 0xFF) | 0x100).substring(1,3));
		}
		return sb.toString();
	}
	public static String md5Hex (String message) {
		try {
			MessageDigest md =
					MessageDigest.getInstance("MD5");
			return hex (md.digest(message.getBytes("CP1252")));
		} catch (NoSuchAlgorithmException e) {
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}
	
	public static String formatImageNameByDensity(String name){
		if(densityDpi>DisplayMetrics.DENSITY_MEDIUM){
			return name.replace(".png", "@2x.png").replace(".PNG", "@2x.png");
		}
		return name;
	}
	
	/**
	 * Convert pixel to DP, based on device's screen density.
	 * @param in
	 * @return
	 */
	public static float unScale(float in){
		return in/density;
	}
	/**
	 * Convert DP to pixel, based on device's screen density.
	 * @param in
	 * @return
	 */
	public static float scale(float in){
		return density*in;
	}
	
	/**
	 * Convert DP to integer pixel, based on device's screen density.
	 * @param in
	 * @return
	 */
	public static int scaleLayoutParam(int in){		
		return (int) (in*density+0.5f);
	}
	
	/**
	 * Define an alpha value for deselected items. Used in event type filters.
	 * @param selected
	 * @return selected? 255:60
	 */
	public static int getSelectionAlpha(boolean selected){
		return selected? 255:60;
		
	}
	
	private static final int BRIGHTNESS_THRESHOLD = 130;
	
	 /**
     * Calculate whether a colour is light or dark, based on a commonly known
     * brightness formula.
     *
     * @see {@literal http://en.wikipedia.org/wiki/HSV_color_space%23Lightness}
     */
    public static boolean isColourDark(int color) {
        return ((30 * Color.red(color) +
                59 * Color.green(color) +
                11 * Color.blue(color)) / 100) <= BRIGHTNESS_THRESHOLD;
    }
	
	
	
	
	
	static final int SPACING_PX=6;

	public static SpannableStringBuilder addDrawableAndText(Drawable d, CharSequence t){
		int width = d.getIntrinsicWidth();
		int height = d.getIntrinsicHeight();
		d.setBounds(0, 0, width > 0 ? width : 0, height > 0 ? height : 0);
		SpannableStringBuilder b = new SpannableStringBuilder();
		StringUtils.appendSpan(b, " ", new ImageSpan(d, ImageSpan.ALIGN_BASELINE));
		b.append(" ").append(t);
		return b;
	}
	
	/**
	 * Makes any compound drawables associated with this textview draw closer to the text.
	 * @param button
	 */
	public static void setTightCompoundDrawables(final TextView button){		
		if(button!=null) button.post(new Runnable(){
			@Override
			public void run() {
				final Drawable[] existingDrawables = button.getCompoundDrawables();
				int width = button.getWidth();
				String text = button.getText().toString();
				
				TransformationMethod meth = button.getTransformationMethod();
				if (meth!=null ){
					//the text returned from getText() may be transformed before it is drawn. IE if textAllCaps is set in XML,
					//The text will be subsequently transformed, changing its width. So apply the transformation if available.
					text = meth.getTransformation(button.getText(), button).toString();
				}
				float textWidth = button.getPaint().measureText(text);
				float padding=-1;
				if(existingDrawables[0]!=null && existingDrawables[2]!=null){
					//d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());																																			
					padding=(width-textWidth-existingDrawables[0].getBounds().width()-existingDrawables[2].getBounds().width()-scale(SPACING_PX))/2f;
					
				}
				else if(existingDrawables[0]!=null ){
					//d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());																																			
					padding=(width-textWidth-existingDrawables[0].getBounds().width()-scale(SPACING_PX))/2f;
					
				}
				else if(existingDrawables[2]!=null ){
					//d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());																																			
					padding=(width-textWidth-existingDrawables[2].getBounds().width()-scale(SPACING_PX))/2f;
					
				}	
				
				
				//no drawables, or text too wide for view
				if(padding<=0 || padding>(width/2f)) padding = scale(SPACING_PX);

				//button.setCompoundDrawables(existingDrawables[0], existingDrawables[1], existingDrawables[2], existingDrawables[3]);
				button.setPadding((int)(padding+0.0), button.getPaddingTop(), (int)(padding+0.0), button.getPaddingBottom());				
				
			}

		});


	}
	
	
	static final int STROKE_ALPHA=100;	
	
	/**
	 * Call this after onLongClick to show a toast at the view.
	 * @param v toast is shown at the view's position on screen
	 * @param message can be null to use the view's contentDescription
	 * @return true if the toast is shown
	 */
	public static boolean showHintToastAtView(View v, CharSequence message){
		if(StringUtils.isNullOrEmpty(message)) message = v.getContentDescription();
		if(StringUtils.isNullOrEmpty(message)) return false;
		final int[] screenPos = new int[2];
        final Rect displayFrame = new Rect();
        v.getLocationOnScreen(screenPos);
        v.getWindowVisibleDisplayFrame(displayFrame);
		final int width = v.getWidth();
        final int height = v.getHeight();
        final int midy = screenPos[1] + height / 2;
        final int screenWidth = v.getResources().getDisplayMetrics().widthPixels;

        
		Toast cheatSheet = Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT);
        if (midy < displayFrame.height()) {
            // Show along the top; follow action buttons
            cheatSheet.setGravity(Gravity.TOP | Gravity.RIGHT,
                    screenWidth - screenPos[0] - width / 2, midy);
        } else {
            // Show along the bottom center
            cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
        }
        cheatSheet.show();
        return true;
	}
	
	/**
	 * Set text1 and text2 from R.string. The second text view is not explicitly hidden if the text is empty.
	 * @param root
	 * @param text1
	 * @param text2
	 */
	public static void doHeadingText(View root, int text1, int text2){
		TextView tv1 = (TextView) root.findViewById(R.id.text1);
		if(tv1!=null){
			tv1.setText(text1);
		}
		
		TextView tv2 = (TextView) root.findViewById(R.id.text2);
		if(tv2!=null){
			tv2.setText(text2);
		}
	}
	/**
	 * Set text1 and text2. If text2 is null or empty, the second text view is hidden.
	 * @param root
	 * @param text1
	 * @param text2
	 */
	public static void doHeadingText(View root, CharSequence text1, CharSequence text2){
		TextView tv1 = (TextView) root.findViewById(R.id.text1);
		if(tv1!=null){			
			tv1.setText(text1);
		}
		
		TextView tv2 = (TextView) root.findViewById(R.id.text2);
		if(tv2!=null){
			if(text2==null || text2.length()==0){
				tv2.setVisibility(View.GONE);
			}
			else{
				tv2.setText(text2);
				tv2.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * Update TabLayout tab title text from ViewPager adapter's getPageTitle.
	 * TabLayout must already be configured with setupWithViewPager
	 * @param tabLayout
	 * @param pager
	 */
	public static void updateTabTitles(TabLayout tabLayout, ViewPager pager){
		if(pager.getAdapter()!=null && tabLayout.getTabCount()==pager.getAdapter().getCount()){
			for(int i=0;i<tabLayout.getTabCount();i++){
				tabLayout.getTabAt(i).setText(pager.getAdapter().getPageTitle(i));
			}
		}
	}
	
	
	/**
	 * Update supplied TextView with any validation messages contained in model,
	 * or hide it if empty.
	 * @param model containing validation strings, or null to hide.
	 * @param v
	 *
	public static void displayValidationMessages(ValidationError model, TextView v){
		if(model!=null){
			displayValidationMessagesString(model.getReasons(), v);
		}
		else{
			displayValidationMessages((int[])null, v);
		}
	}*/
	public static void displayValidationMessagesString(String[] errors, TextView v){
		if (errors!=null && v!=null){
			String text=StringUtils.stringArrayToString(errors, "\n", false);
			if (text.length()>0){
				v.setText(text);
				v.setVisibility(View.VISIBLE);
			}
			else{
				v.setVisibility(View.GONE);
			}
		}
		else{
			if (v!=null) v.setVisibility(View.GONE);
		}
	}
	public static void displayValidationMessagesString(List<String> errors, TextView v){
		if (errors!=null && v!=null){
			String text=StringUtils.stringListToString(errors, "\n", false);
			if (text.length()>0){
				v.setText(text);
				v.setVisibility(View.VISIBLE);
			}
			else{
				v.setVisibility(View.GONE);
			}
		}
		else{
			if (v!=null) v.setVisibility(View.GONE);
		}
	}
	
	public static void displayValidationMessages(List<Integer> errors, TextView v){
		if(errors!=null){
			int[] errorsArray = new int[errors.size()];
			int i=0;
			for(int item : errors){
				errorsArray[i]=item;
				i++;
			}
			displayValidationMessages(errorsArray, v);
		}
		else{
			displayValidationMessages((int[])null, v);
		}
	}
	public static void displayValidationMessages(int[] errors, TextView v){		
		if (errors!=null && v!=null){
			String text=StringUtils.inflateStringResourceArray(errors, "\n");
			if (text.length()>0){
				v.setText(text);
				v.setVisibility(View.VISIBLE);
			}
			else{
				v.setVisibility(View.GONE);
			}
		}
		else{
			if (v!=null) v.setVisibility(View.GONE);
		}
		
	}
	
	static long lastShownToastTime=0;
	static final int TOAST_OVERLAP_TIME = 1000*6;
	/*
	 * Display a Toast (non-modal dialog-box mechanism used to display information to users)
	 * Which will notify the user that the application does not have an internet connection
	 */
	public static void displayNoInternetConnectionToast(Context context, String append) {
		long now = System.currentTimeMillis();
		if(now<(lastShownToastTime+TOAST_OVERLAP_TIME)) return;
		try{
		ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		String msg=null;
		if (connectivityManager.getActiveNetworkInfo() != null){
			msg=connectivityManager.getActiveNetworkInfo().getReason();
		}
		if (msg==null) {
			msg=context.getString(R.string.no_internet_available);
		}
		if(append!=null){
			msg = append+msg;
		}
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		lastShownToastTime=now;
		}
		catch(Exception e){
			//Can't access connectivity manager, or it's null
		}
		
	}	
	
	
	/**
	 * Checks whether there is an internet connection available
	 * 
	 * */
	public static boolean isInternetAvailable() {	
		try{
		ConnectivityManager	connectivityManager=(ConnectivityManager) ConnectedApp.getContextStatic().getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if (connectivityManager.getActiveNetworkInfo() != null) {
			return connectivityManager.getActiveNetworkInfo().isConnected();
		}
		else {
			return false;
		}
		}
		catch(Exception e){
			//Can't access connectivity manager, or it's null
			return true;
		}
	}	
	
	
	/**
	 * Set Roboto font.
	 * @param context
	 * @param view TextView or ViewGroup parent containing TextViews
	 * @param fontName one of ROBOTO_LIGHT, ROBOTO_REGULAR, ROBOTO_THIN
	 */
	public static void setRobotoFont (Context context, View view, String fontName){
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH) return;
		
		Typeface typeface = getTypeface(context, fontName);
		setFont(view, typeface);
	}
	
	/**
	 * Set Roboto font on a list of views.
	 * @param context
	 * @param fontName
	 * @param args
	 */
	public static void setRobotoFont (Context context, String fontName, View... args){
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH) return;
		
		Typeface typeface = getTypeface(context, fontName);
		for (View arg : args){
			setFont(arg, typeface);
		}
	}
	
	private static Typeface getTypeface(Context context, String fontName){
		if (robotoTypeFace == null || !robotoTypeFaceFontName.equals(fontName))
		{			
			boolean hit=true;
			robotoTypeFace = typeFaceCache.get(fontName);
			if(robotoTypeFace==null){
				robotoTypeFace = Typeface.createFromAsset(context.getAssets(), fontName);
				typeFaceCache.put(fontName, robotoTypeFace);
				
				hit=false;
			}
			
			if(ConnectedApp.DEBUG){
				Log.d("Roboto", "Hit: "+hit+" Changing from "+robotoTypeFaceFontName + " to "+fontName);
			}
			robotoTypeFaceFontName=fontName;
		}
		return robotoTypeFace;
	}
	private static void setFont (View view, Typeface typeFace)
	{
		if (view instanceof ViewGroup)
		{
			for (int i = 0; i < ((ViewGroup)view).getChildCount(); i++)
			{
				setFont(((ViewGroup)view).getChildAt(i), typeFace);
			}
		}
		else if (view instanceof TextView)
		{
			((TextView) view).setTypeface(typeFace);
			if(ConnectedApp.DEBUG){
				String name;
				try{
					name = view.getContext().getResources().getResourceEntryName(view.getId())
							+view.getContext().getResources().getResourcePackageName(view.getId()) + " "
							+view.getContext().getResources().getResourceTypeName(view.getId()) + " "
							+view.getContext().getResources().getResourceName(view.getId());
							
				}
				catch(Resources.NotFoundException e){
					name = ""+view.getId();
				}
				Log.d("Roboto", "setting typeface on view "+name+ " typeface: "+robotoTypeFaceFontName);
			}
		}
	}
	
	/**
	 * Set Roboto font excluding certain view ids.
	 * @param context
	 * @param view TextView or ViewGroup parent containing TextViews
	 * @param fontName one of ROBOTO_LIGHT, ROBOTO_REGULAR, ROBOTO_THIN
	 */
	public static void setRobotoFont(Context context, View view, String fontName, Set<Integer> excludingIds){		
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH) return;
		
		Typeface typeface = getTypeface(context, fontName);
		setFontExcludingIds(view, typeface, excludingIds);
	}
	
	private static void setFontExcludingIds (View view, Typeface typeFace, Set<Integer> excludingIds)
	{
		if (excludingIds.contains(view.getId())) return;
		if (view instanceof ViewGroup)
		{
			for (int i = 0; i < ((ViewGroup)view).getChildCount(); i++)
			{
				setFontExcludingIds(((ViewGroup)view).getChildAt(i), typeFace, excludingIds);
			}
		}
		else if (view instanceof TextView)
		{
			((TextView) view).setTypeface(typeFace);
			if(ConnectedApp.DEBUG){
				String name;
				try{
					name = view.getContext().getResources().getResourceEntryName(view.getId());
				}
				catch(Resources.NotFoundException e){
					name = ""+view.getId();
				}
				Log.d("Roboto", "setting typeface on id "+name+ " typeface: "+robotoTypeFaceFontName);
			}
		}
	}

}
