package au.com.connectedteam.application;

import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;

import au.com.connectedteam.BuildConfig;
import au.com.connectedteam.R;
import au.com.connectedteam.config.AppConfig;
import au.com.connectedteam.config.AppLocale;
import au.com.connectedteam.config.IAppLocaleBuilder;
import au.com.connectedteam.config.IAppLocaleProvider;
import au.com.connectedteam.util.StringUtils;
import au.com.connectedteam.util.UIUtil;

import net.servicestack.client.LogProvider;


public class ConnectedApp extends Application{

	public static final boolean DEBUG = true;
	public static final boolean GTM_ENABLED = false;
	public static final boolean APPVERSION_CHECK_ENABLED = false;

	
	private static ConnectedApp instance;

	public static final String SESSION_DIRECTORY = "session";
	public static final long MAX_SESSION_RESTORE_AGE = DateUtils.DAY_IN_MILLIS;

	private ErrorReporter mErrorReporter;
	/**
	 * app mocks:
	 * https://projects.invisionapp.com/share/ER2ZRWE7S#/screens
	 */
	
	 @Override
	public void onCreate() {
		 super.onCreate();
		 instance=this;
		 doAppUpdates();
		 mErrorReporter = new ErrorReporter(this);
		 net.servicestack.client.Log.setInstance(new LogProvider("SS", DEBUG));

		 Session.Instantiate(getApplicationContext());
//

	 }

	public static ErrorReporter getErrorReporter(){
		return instance.mErrorReporter;
	}
	 
	 public boolean isXLarge(){
	    	//return false;
	    	//BoxTabsWithBetSelector uses API9+
	    	//SCREENLAYOUT_SIZE_XLARGE is only in 9+ anyway
	    	//TwitterRequestInterceptor requires API9+
	    	if(Build.VERSION.SDK_INT<9) return false;
	    	
	    	if( ((getResources().getConfiguration().screenLayout & 
				    Configuration.SCREENLAYOUT_SIZE_MASK) == 
				        Configuration.SCREENLAYOUT_SIZE_XLARGE)) return true;
	    	DisplayMetrics dm = getResources().getDisplayMetrics(); //was 595dp
	    	int width = Math.max(dm.widthPixels, dm.heightPixels);
	    	int height = Math.min(dm.widthPixels, dm.heightPixels);
	    	//in landscape:
			int minWidth = UIUtil.scaleLayoutParam(660);
			int minHeight = UIUtil.scaleLayoutParam(520);
			return width>=minWidth && height>=minHeight;
	    }
	 
	 public static Context getContextStatic(){
	    	return instance.getApplicationContext();
	    }

	public static IAppLocaleProvider getAppLocaleProvider(){
		try {
			Class<?> clazz = Class.forName(instance.getString(R.string.IAppLocaleProvider_impl));
			return((IAppLocaleProvider) clazz.getConstructor(Context.class).newInstance(getContextStatic()));
		} catch (Exception e) {
			throw new RuntimeException("Error instantiating IAppLocaleProvider class", e);
		}
	}

	private AppLocale mAppLocale;
	public static void invalidateLocale(){
		Session.getInstance().clearAll();
		instance.mAppLocale=null;
	}
	public static AppLocale getAppLocale(){
		if(instance.mAppLocale==null){
			SharedPreferences sp = getContextStatic().getSharedPreferences(AppConfig.PREFS_FILE_APPPREFS, MODE_PRIVATE);

			IAppLocaleProvider provider = getAppLocaleProvider();
			String savedCountryCode = sp.getString(AppConfig.APPLICATION_APPLOCALE_COUNTRY_CODE, null);
			boolean updateSP = savedCountryCode==null;
			IAppLocaleBuilder builder = provider.getAppLocaleBuilder(savedCountryCode);

			if(DEBUG){
				String devUrl = sp.getString(AppConfig.APPLICATION_APPLOCALE_DEV_WEBSERVICE_URL, null);
				String devName = sp.getString(AppConfig.APPLICATION_APPLOCALE_DEV_NAME, null);
				if(StringUtils.isAnyNullOrEmpty(devUrl, devName)) updateSP=true;
				else{
					builder.withDevWebserviceUrl(devUrl).withName(devName);
				}
			}
			instance.mAppLocale = builder.build();

			if(updateSP){
				SharedPreferences.Editor editor = sp.edit();
				editor.putString(AppConfig.APPLICATION_APPLOCALE_COUNTRY_CODE, instance.mAppLocale.getCountryCode());
				if(DEBUG){
					editor.putString(AppConfig.APPLICATION_APPLOCALE_DEV_WEBSERVICE_URL, instance.mAppLocale.getWebserviceUrl());
					editor.putString(AppConfig.APPLICATION_APPLOCALE_DEV_NAME, instance.mAppLocale.getName());
				}
				editor.commit();
			}

		}
		return instance.mAppLocale;
	}

	public static String getWebserviceUrl(){
		return getAppLocale().getWebserviceUrl();
	}

	private void doAppUpdates() {
		// if(DEBUG) return;
		SharedPreferences appPrefs = getSharedPreferences(AppConfig.PREFS_FILE_APPPREFS, 0);
		int lastVersionCode = appPrefs.getInt(AppConfig.APP_LAST_VERSION_CODE, -1);
		int currentVersionCode = BuildConfig.VERSION_CODE;
        /*
        if(lastVersionCode==-1){
            //Clean install

        }*/
		if (currentVersionCode > lastVersionCode) {
			SharedPreferences.Editor appPrefsEditor = appPrefs.edit();
			appPrefsEditor.putInt(AppConfig.APP_LAST_VERSION_CODE, currentVersionCode);
			appPrefsEditor.commit();
		}
	}

    public static Date getBuildDate(){
        try{
            ApplicationInfo ai = getContextStatic().getPackageManager().getApplicationInfo(getContextStatic().getPackageName(), 0);
            ZipFile zf = new ZipFile(ai.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            Date date = new Date(ze.getTime());
            zf.close();
            return date;

        }catch(Exception e){
            return new Date(0);
        }
    }
}
