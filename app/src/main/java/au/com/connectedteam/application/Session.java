package au.com.connectedteam.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Observable;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.koushikdutta.ion.builder.Builders;
import com.parse.Parse;
import com.parse.ParseUser;

import au.com.connectedteam.appsapi.generated.dto;
import au.com.connectedteam.config.AppConfig;
import au.com.connectedteam.models.AppVersion;
import au.com.connectedteam.models.UserHeader;
import au.com.connectedteam.network.IonHelper;
import au.com.connectedteam.util.StringUtils;

/**
 * Singleton session for for the application.
 * Holds ...
 * @author bramleyt
 *
 */
public class Session extends Observable{




	public static enum WhatChanged{
		
		LOG_IN_OUT,
		RE_AUTH,
		RE_AUTH_FAIL,
		APP_VERSION,
		USER_HEADER,

		
	}
	
	private static Session instance;
	
	public static final String TAG = "Session";
	///

    private AppVersion mAppVersion;
	
	private String mCookie;
	private String mEmail;
	private String mPassword;
	private UserHeader mCustomerHeader;


	private Session(Context context){
		
		setUpSession(context);
		
	}
	
	private void setUpSession(Context context){
		deleteObservers();
		
		SharedPreferences settings = context.getSharedPreferences(AppConfig.PREFS_FILE_USERPREFS, Context.MODE_PRIVATE);
		mEmail = settings.getString(AppConfig.LOGIN_REMEMBERME_EMAIL, null);
		mPassword = settings.getString(AppConfig.LOGIN_REMEMBERME_PASSWORD, null);
		executeGetAppVersionRequest(context);
		if(mPassword!=null && mCustomerHeader ==null){
			executeAutoLogin(mEmail, mPassword);
		}


	}




	public static Session getInstance(){
		return instance;
		/*
		if (instance!=null){
			return instance;
		}
		else throw new IllegalStateException("Session should first be created by au.com.connectedteam.application.ConnectedApp");*/
	}
	
	
	static Session Instantiate(Context context){
		if(instance!=null) throw new IllegalStateException("Session already created");
		long startTime = System.currentTimeMillis();
		File directory = context.getDir(ConnectedApp.SESSION_DIRECTORY, 0);

		File[] files = directory.listFiles();

		if (files==null || files.length == 0) {
			//No file to restore from
			instance = new Session(context);		
			return instance;
		}
		for(File file : files){
			if (file.getName().equals(DO_NOT_RESTORE_FLAG_FILENAME)){
				return cleanAndMakeNew(files, context);
			}
		}
		File lastModifiedFile = files[0];
		for (int i = 1; i < files.length; i++) {
			
			if (lastModifiedFile.lastModified() < files[i].lastModified()) {
				lastModifiedFile = files[i];
			}
		}
		if(lastModifiedFile.lastModified()<System.currentTimeMillis()-ConnectedApp.MAX_SESSION_RESTORE_AGE
				|| lastModifiedFile.lastModified()<ConnectedApp.getBuildDate().getTime()){
			//Too old (or made by a previous build), delete and make fresh
			
			return cleanAndMakeNew(files, context);
		}

		//Read
		try { 
			Session session; 
			Log.d("Session", "Restoring from file "+lastModifiedFile);
			FileInputStream fis = new FileInputStream(lastModifiedFile);
			ObjectInputStream ois = new ObjectInputStream(fis); 
			session = (Session)ois.readObject(); 
			ois.close(); 
			//System.out.println("object2: " + session.debugString());
			//Successfully restored, now delete all session files
			for (File oldFile : files){
				oldFile.delete();
			}
			session.setUpSession(context);
			instance=session;
			Log.d("Session", "Restoring finished after "+(System.currentTimeMillis()-startTime)+"ms");
			return instance;
		} 
		catch(Exception e) { 
			Log.e("Session","Exception during deserialization: ", e); 					

			return cleanAndMakeNew(files, context);
		} 

	}
	private static Session cleanAndMakeNew(File[] fileList, Context context){
		for (File file : fileList){
			file.delete();
		}
		instance = new Session(context);		
		return instance;
	}
	public static void cleanAsync(final Context context){
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				File directory = context.getDir(ConnectedApp.SESSION_DIRECTORY, 0);

				File[] files = directory.listFiles();
				if(files!=null) for (File file : files){
					file.delete();
				}
			}
		});
		thread.start();
	}
	static final String DO_NOT_RESTORE_FLAG_FILENAME = "donotrestore";
	public static void setDoNotRestoreFlag(final Context applicationContext){
		File directory = applicationContext.getDir(ConnectedApp.SESSION_DIRECTORY, 0);
		File file = new File(directory, DO_NOT_RESTORE_FLAG_FILENAME);
		try {
			file.createNewFile();
		} catch (IOException e) {
			Log.e("Session","Exception writing user exit flag: ", e); 
		}
	}
	public static void writeSessionToFile(final Context applicationContext){
		final long now = System.currentTimeMillis();
		
		
		Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				
				if(instance==null){
					Log.e("Session", "WriteSessionToFile: null session");
					return;
				}
				try {

					File directory = applicationContext.getDir(ConnectedApp.SESSION_DIRECTORY, 0);
					
					
					String filename = now + ".ser";
					File outputFile = new File(directory, filename);
					FileOutputStream fos = new FileOutputStream(outputFile);
					ObjectOutputStream oos = new ObjectOutputStream(fos); 
					oos.writeObject(instance); 
					oos.flush(); 
					oos.close();
					Log.d("Session", "Write finished after "+(System.currentTimeMillis()-now)+"ms");
				} 
				catch(Exception e) { 
					Log.e("Session","Exception during serialization: ", e); 

				} 
			}
		});
		thread.start();
		
	}
	
	void setChanged(WhatChanged what){
		super.setChanged();
		notifyObservers(what);
	}



    public AppVersion getAppVersion() {
        return mAppVersion;
    }
	
	public String getCookie(){
		return mCookie;
	}
	public void addCookie(String cookie){
		String splitCookie =cookie.split(";", 2)[0];
		if(mCookie==null) mCookie=splitCookie;
		else{
			
			this.mCookie+="; "+splitCookie;
		}
		//Log.d("Session", "Add-Cookie:"+cookie);
		//Log.d("Session", "Cookie:"+mCookie);
	}
	
	public boolean canAutoLogin(){
		return mEmail!=null && mPassword!=null ;
	}

	public UserHeader getCustomerHeader(){
		return mCustomerHeader;
	}
	public void setCustomerHeader(ParseUser customerData, String password){
		if(ParseUser.getCurrentUser()==customerData) {
			mEmail = customerData.getEmail();
			mPassword = password;

			SharedPreferences settings = ConnectedApp.getContextStatic().getSharedPreferences(AppConfig.PREFS_FILE_USERPREFS, Context.MODE_PRIVATE);
			Editor e = settings.edit();
			e.putString(AppConfig.LOGIN_REMEMBERME_EMAIL, mEmail);
			e.putString(AppConfig.LOGIN_REMEMBERME_PASSWORD, mPassword);
			//e.putInt(AppConfig.LOGIN_REMEMBERME_CUSTOMER_ID, customerData.getObjectId());
			e.commit();

			setChanged(WhatChanged.LOG_IN_OUT);
		}
	}
	public void setCustomerHeader(UserHeader customerData){
		if(customerData!= mCustomerHeader) {

			if(customerData ==null){
				mPassword=null;
				lastUserHeaderUpdateTime=0;
			}
			else{
				mEmail= customerData.userName.getEmail();
				lastUserHeaderUpdateTime=System.currentTimeMillis();
			}
			WhatChanged change = (customerData!=null&&mCustomerHeader!=null)?WhatChanged.USER_HEADER:WhatChanged.LOG_IN_OUT;
			mCustomerHeader = customerData;
			setChanged(change);
		}
	}
	public boolean isLoggedIn() {
		return ParseUser.getCurrentUser() !=null;
	}


	void clearAll(){
		clearLogin();
		//mLobbySession=new LobbySession();
		//mPickBuilderSession=new PickBuilderSession();

	}
	public void clearLogin(){
		mCustomerHeader =null;
		mEmail=null;
		mPassword=null;
		mCookie=null;
		Editor editor = ConnectedApp.getContextStatic().getSharedPreferences(AppConfig.PREFS_FILE_USERPREFS, Context.MODE_PRIVATE).edit();
		editor.remove(AppConfig.LOGIN_REMEMBERME_EMAIL);
		editor.remove(AppConfig.LOGIN_REMEMBERME_PASSWORD);
		editor.remove(AppConfig.LOGIN_REMEMBERME_CUSTOMER_ID);
		editor.commit();
		setChanged(WhatChanged.LOG_IN_OUT);
		if(isExecutingAutologin){
			shouldCancelAutologinRequest=true;
		}
		getIonHelper().getIon().getCookieMiddleware().clear();
	}
	
	private transient boolean isExecutingAutologin;
	private transient boolean shouldCancelAutologinRequest;
	public boolean isExecutingAutoLogin(){
		return isExecutingAutologin;
	}


    public void executeGetAppVersionRequest(Context context){
     //   throw new RuntimeException("AppVersion NYI");
/*
        Gson gson =  new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        new AQuery(context).transformer(new AQueryGsonTransformer(gson, AppVersion.class))
                .ajax(context.getString(R.string.server_appversion), AppVersion.class, new AjaxCallback<AppVersion>() {
                    @Override
                    public void callback(String url, AppVersion result, AjaxStatus status) {
                        if (result != null) {
                           mAppVersion=result;
                            setChanged(WhatChanged.APP_VERSION);

                        }
                    }
                });
*/
    }

	public void clearAutoLogin(){
		mPassword=null;
		SharedPreferences settings = ConnectedApp.getContextStatic().getSharedPreferences(AppConfig.PREFS_FILE_USERPREFS, Context.MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.putString(AppConfig.LOGIN_REMEMBERME_PASSWORD, null);
		editor.putInt(AppConfig.LOGIN_REMEMBERME_CUSTOMER_ID, 0);
		editor.commit();
		setCustomerHeader(null);
	}

	public void onUnauthorised(){
		if(canAutoLogin()){
			if(!isExecutingAutologin) {
				setChanged(WhatChanged.RE_AUTH);
				executeAutoLogin(mEmail, mPassword);
			}
		}
		else {
			setCustomerHeader(null);
		}
	}

	private transient IonHelper _ionHelper;
	private IonHelper getIonHelper(){
		if(_ionHelper==null)
			_ionHelper = new IonHelper(new IonHelper.IonHelperCallbacks() {

				@Override
				public void onIonRequestPreExecute(IonHelper.HelperRequest<?> request) {
					request.getBuilder().group(Session.this);
				}

				@Override
				public void onIonRequestStarting(IonHelper.HelperRequest<?> request) {

				}

				@Override
				public void onIonRequestFinished(IonHelper.HelperRequest<?> request, Response<?> response) {

				}
			});
		return _ionHelper;
	}

	private void executeAutoLogin(String email, String password) {

	}
	private long lastUserHeaderUpdateTime;
	private boolean isExecutingUserHeader;
	private static final long USER_HEADER_INTERVAL = DateUtils.SECOND_IN_MILLIS*30L;
	public boolean canExecuteGetUserHeader(){
		return isLoggedIn() && !isExecutingUserHeader && lastUserHeaderUpdateTime<System.currentTimeMillis()-USER_HEADER_INTERVAL;
	}
	public Future<Response<UserHeader>> executeGetUserHeader(){
		return executeGetUserHeader(false);
	}
	public Future<Response<UserHeader>> executeGetUserHeader(boolean forceRefresh){
		if(forceRefresh) lastUserHeaderUpdateTime=0;
		if(canExecuteGetUserHeader()) {
			isExecutingUserHeader=true;
			/*
			return getIonHelper().doGet(getIonHelper().getIon().build(ConnectedApp.getContextStatic()), new dto.GetCustomerHeader())
					.go().setCallback(new FutureCallback<Response<dto.CustomerHeader>>() {
						@Override
						public void onCompleted(Exception e, Response<dto.CustomerHeader> result) {
							isExecutingUserHeader=false;
							if (result != null && result.getResult() != null) {
								setCustomerHeader(result.getResult());
							}
							else if (result != null && result.getHeaders() != null && result.getHeaders().code() == 401)
								onUnauthorised();
						}
					}); */
		}
		return null;
	}

	public void executeLogout(){
		clearLogin();

	}

}
