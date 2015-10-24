package au.com.connectedteam.activity.settings;

import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import au.com.connectedteam.BuildConfig;
import au.com.connectedteam.R;
import au.com.connectedteam.application.Session;
import au.com.connectedteam.application.ConnectedApp;
import au.com.connectedteam.config.AppConfig;
import au.com.connectedteam.config.AppLocale;
import au.com.connectedteam.config.IAppLocaleProvider;
import au.com.connectedteam.util.Reflect;
import au.com.connectedteam.util.StringUtils;


@SuppressWarnings("deprecation")
public class UserSettingsActivity extends PreferenceActivity implements OnPreferenceClickListener, OnSharedPreferenceChangeListener{

	final static String ACTION_PREFS_GENERAL = "au.com.thedogs.prefs.General";
	//final static String EXTRA_PREFERENCE_RESOURCE_ID = "preferenceResource";
	final static String PREF_KEY_ABOUT = "pref_key_about";
	final static String PREF_KEY_SUPPORT = "pref_key_support";
	final static String PREF_KEY_APP_LOCALE = "pref_key_app_locale";
	final static String PREF_KEY_CLEAR_RECENT_SEARCHES = "pref_key_clear_recents";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {	    	   
	    super.onCreate(savedInstanceState);
	        // Load the legacy preferences headers
	       addPreferencesFromResource(R.xml.user_settings);
	    
	    
	}

	
	
	protected boolean intentEquals(Class<?> activityClass, Intent intent){
		if (intent!=null){
			return intent.getComponent()!=null && activityClass.getName().equals(intent.getComponent().getClassName());
		}
		return false;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onResume(){
		super.onResume();
		
			refreshSummariesInner(getPreferenceScreen());
			getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

	}
	
	
	@Override
	public void onPause(){
		super.onPause();

		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

	}
	
	
	public void refreshSummariesInner(Preference preference){
		if(preference instanceof PreferenceGroup){
			PreferenceGroup prefGroup = (PreferenceGroup) preference;
			for(int i=0;i<prefGroup.getPreferenceCount(); i++){
				refreshSummariesInner(prefGroup.getPreference(i));
			}
		}
		else if (preference!=null){
			preference.setOnPreferenceClickListener(this);
			if(preference instanceof EditTextPreference){
				preference.setSummary(preference.getSharedPreferences().getString(preference.getKey(), ""));
			}
			if(PREF_KEY_ABOUT.equals(preference.getKey())){
				String versionStr="";
				try{
					PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
					versionStr = pi.versionName;
				}
				catch(NameNotFoundException e){					
				}

				String summary;
                if(ConnectedApp.DEBUG){
                    summary=String.format("%s v:%s build %d %s", getString(R.string.app_name), versionStr,
                            BuildConfig.VERSION_CODE,
                            StringUtils.formatDateTimeLeft(ConnectedApp.getBuildDate()));
                }
                else {
                    summary=String.format("%s v:%s %s", getString(R.string.app_name), versionStr, getString(R.string.app_version_date));
                }
				preference.setSummary(summary);
			}
			if(PREF_KEY_APP_LOCALE.equals(preference.getKey())){

                if(ConnectedApp.DEBUG){
                    String summary = ConnectedApp.getWebserviceUrl();
					preference.setSummary(summary);
                }
                else{
                    getPreferenceScreen().removePreference(preference);
                }
			}
			
		}
		
		
	}
	


	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Preference preference = findPreference(key);
		if(preference!=null){
			refreshSummariesInner(preference);
		}
		
	}
	

	@Override
	public boolean onPreferenceClick(Preference preference) {
		
		if(PREF_KEY_ABOUT.equals(preference.getKey())){
			onAboutClicked();
			return true;
		}

		if(PREF_KEY_APP_LOCALE.equals(preference.getKey())){
			if(ConnectedApp.DEBUG) doDebugLocaleDialog();
			else doProductionLocaleDialog();
			return true;
		}

		return false;
	}
	

	private void doProductionLocaleDialog(){
		//
	}
	private void doDebugLocaleDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        IAppLocaleProvider provider = ConnectedApp.getAppLocaleProvider();
		final List<AppLocale> locales = provider.getAvailableDevLocales();
		final String[] items = new String[locales.size()];
        int selectedIndex=-1;
        AppLocale currentLocale = ConnectedApp.getAppLocale();
		for(int i=0;i<locales.size();i++){
            AppLocale locale = locales.get(i);
			items[i]=String.format("%s %s (%s)", locale.getCountryCode(), locale.getName(), locale.getWebserviceUrl());
            if(locale.equals(currentLocale)) selectedIndex=i;
		}
		builder.setTitle("Choose API").setSingleChoiceItems(items, selectedIndex, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int position) {
				SharedPreferences prefs = getSharedPreferences(AppConfig.PREFS_FILE_APPPREFS, Context.MODE_PRIVATE);
                AppLocale selectedLocale = locales.get(position);
                Editor editor = prefs.edit();
				editor.putString(AppConfig.APPLICATION_APPLOCALE_COUNTRY_CODE, selectedLocale.getCountryCode());
				editor.putString(AppConfig.APPLICATION_APPLOCALE_DEV_WEBSERVICE_URL, selectedLocale.getWebserviceUrl());
				editor.putString(AppConfig.APPLICATION_APPLOCALE_DEV_NAME, selectedLocale.getName());
                editor.putBoolean(AppConfig.APPLICATION_APPLOCALE_HAS_USER_CHOSEN, true);
                editor.commit();

                //clear recent hotel searches when switching APIs
              //  prefs = getSharedPreferences(HotelSearchActivity.PREFS_FILE_HOTELSEARCH, Context.MODE_PRIVATE);
              //  prefs.edit().clear().commit();
				ConnectedApp.invalidateLocale();
                dialog.dismiss();
				refreshSummariesInner(getPreferenceScreen());
			}
			
		}).show();
	}


	
	//@SuppressLint("NewApi")
	@SuppressWarnings("ResourceType")
    private void onAboutClicked() {
		AlertDialog dialog;
		Context c;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            dialog= new AlertDialog(this, android.R.style.Theme_Material_Light_Dialog){

            };
            c=dialog.getContext();
        }
		else if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
			 dialog= new AlertDialog(this, AlertDialog.THEME_HOLO_DARK){
				 
			 };
			 c=dialog.getContext();			
		}
		else{
			dialog = new AlertDialog(this){
				
			};
			dialog.setIcon(null);
			c=new ContextThemeWrapper(this, android.R.style.Theme_Dialog);
		}
		LinearLayout root = new LinearLayout(c);
		int padding = c.getResources().getDimensionPixelSize(R.dimen.small_margin);
		root.setPadding(padding, padding, padding, padding);
		root.setOrientation(LinearLayout.VERTICAL);
		ImageView logo = new ImageView(c);
		logo.setImageResource(R.drawable.ic_launcher);
		root.addView(logo, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		TextView text = new TextView(c);
		String versionStr="";
		int build=0;
		try{
			PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionStr = pi.versionName;
			build = pi.versionCode;
		}
		catch(NameNotFoundException e){					
		}
		Date buildDate = ConnectedApp.getBuildDate();
		String summary="";

		AppLocale appLocale = ConnectedApp.getAppLocale();
		if(ConnectedApp.DEBUG){
		summary= "Dev info:\n"+getString(R.string.app_name) + " v:"+versionStr+" build "+build + " date: "+StringUtils.formatDateTimeLeft(buildDate)
				+"\nAPI: "+appLocale.getWebserviceUrl()
				+"\nRegion: " +appLocale.getCultureName()
				+"\n----------\n";
		}
		summary += String.format("v:%s %s\n%s\nDeveloped By ConnectedTeam\nRelease Notes:\n%s",
				versionStr, 
				getString(R.string.app_version_date),
				getString(R.string.home_copyright, StringUtils.formatDate(new Date(), "yyyy")),
				getString(R.string.release_notes));
		text.setText(summary);
		root.addView(text, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ScrollView sv = new ScrollView(c);
		sv.addView(root);
		dialog.setView(sv);
		dialog.setTitle(R.string.app_name);
		dialog.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
			}
		});
		dialog.show();		
		
		
	}
}
