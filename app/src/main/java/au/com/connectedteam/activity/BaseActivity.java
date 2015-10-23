package au.com.connectedteam.activity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import au.com.connectedteam.R;
import au.com.connectedteam.activity.home.HomeActivity;
import au.com.connectedteam.application.ConnectedApp;
import au.com.connectedteam.application.Session;
import au.com.connectedteam.appsapi.generated.dto;
import au.com.connectedteam.config.AppConfig;
import au.com.connectedteam.models.AppVersion;
import au.com.connectedteam.models.UserHeader;
import au.com.connectedteam.util.ListUtils;
import au.com.connectedteam.util.StringUtils;
import au.com.connectedteam.util.UIUtil;


public abstract class BaseActivity extends AppCompatActivity implements Observer{

	protected String mFirstFragmentTag;
    private boolean mNavigationDisabled;
	protected Menu mOptionsMenu;
	private Boolean mPendingMenuRefreshState;
	
	private static final String TAG  = "BaseActivity";
	
	public static final int DIALOG_ID_LOADING = R.string.requesting;
	public static final int DIALOG_ID_SUBMITTING = R.string.submitting;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

	}

	@Override
	public void update(Observable observable, Object data) {
		if(isFinishing()) return;
		final Session.WhatChanged whatChanged = (Session.WhatChanged)data;
		Log.d(TAG, "Session changed: " + whatChanged);
		if(Session.getInstance()==observable)
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					switch(whatChanged){
						case USER_HEADER:
							onUserHeader();
							break;
						case RE_AUTH:
							onReAuth();
							break;
						case LOG_IN_OUT:
							updateUIOnLoginOut();
							break;
						case APP_VERSION:

							final AppVersion appVersion = Session.getInstance().getAppVersion();
							if(shouldShowUpdateDialog(appVersion)){
								//Need to update
								String msg;

								DialogInterface.OnClickListener dlUpdateListener = new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(appVersion.getAppDownloadURL()));
										startActivity(intent);
										finish();
									}
								};
								AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this).setCancelable(false).setIcon(R.drawable.ic_launcher)
										.setTitle(R.string.app_update_available_heading)
										.setPositiveButton(R.string.ok, dlUpdateListener);
								View view;
								Context c;
								if(Build.VERSION.SDK_INT>=11){
									c=builder.getContext();
								}
								else c = BaseActivity.this;
								view = LayoutInflater.from(c).inflate(R.layout.dialog_scrollview_msg, null);
								TextView textView = (TextView) view.findViewById(R.id.text1);
								if(appVersion.forceUpdate()){
									int stringRes =R.string.app_force_update_store;
									msg = getString(stringRes, appVersion.getLatestClientVersion(), appVersion.getVersionUpdateDate(), appVersion.getWhatsNew());
									builder.setCancelable(false);

									setNavigationDisabled(true);
								}
								else{
									//Version, date, changes
									int stringRes = R.string.app_update_available_store;
									msg = getString(stringRes, appVersion.getLatestClientVersion(), appVersion.getVersionUpdateDate(), appVersion.getWhatsNew());
									builder.setNegativeButton(R.string.cancel, null);

								}
								textView.setText(Html.fromHtml(msg));
								textView.setMovementMethod(LinkMovementMethod.getInstance());
								builder.setView(view);
								builder.show();



							}
							break;

					}
					for(BaseFragment activeFragment : ListUtils.ofType(getActiveFragments(), BaseFragment.class)){
						activeFragment.onSessionChanged(whatChanged);
					}
				}});

	}

	public boolean shouldShowUpdateDialog(AppVersion appVersion){
		if(!ConnectedApp.APPVERSION_CHECK_ENABLED) return false;
		if (!appVersion.updateAvailable()) return false;
		if (appVersion.forceUpdate()) return true;
		SharedPreferences settings = getSharedPreferences(AppConfig.PREFS_FILE_USERPREFS, Context.MODE_PRIVATE);
		long updateReminderTime = settings.getLong(AppConfig.HOME_UPDATEREMINDER_TIME, 0);
		long now = System.currentTimeMillis();
		if((updateReminderTime+UPDATE_REMINDER_INTERVAL)<now){
			SharedPreferences.Editor editor = settings.edit();
			editor.putLong(AppConfig.HOME_UPDATEREMINDER_TIME, now);
			editor.commit();
			return true;
		}
		return false;

	}
	/**
	 * UI hook when a user logs in or out
	 */
	protected void updateUIOnLoginOut(){
		onUserHeader();
		if(!Session.getInstance().isLoggedIn() && !Session.getInstance().isExecutingAutoLogin())
			onReAuth();
	}
	protected void onReAuth(){

		Toast.makeText(this, R.string.request_fail_re_auth, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra(HomeActivity.ARG_ON_UNAUTHORISED, true);
		startActivity(intent);

	}

	public void onLogoutClicked(){
		Session.getInstance().executeLogout();
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	public void onStop(){
		super.onStop();
		Session.getInstance().deleteObserver(this);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		Session.getInstance().addObserver(this);
		updateUIOnLoginOut();
		AppVersion appVersion = Session.getInstance().getAppVersion();
		if(ConnectedApp.APPVERSION_CHECK_ENABLED  && appVersion!=null && appVersion.forceUpdate()) setNavigationDisabled(true);
	}

	protected List<Fragment> getActiveFragments(){
		ArrayList<Fragment> result = new ArrayList<Fragment>();
		Fragment mainFragment = getMainFragment();
		if(mainFragment!=null){
			result.add(mainFragment);
		}
		
		//Fragment activeFragment;
		/*
		if(hasDualPaneLayout() 
				&& (activeFragment=getSupportFragmentManager().findFragmentById(R.id.fragment_container2))!=null 
				&& activeFragment.isVisible()){
			result.add((BaseFragment) activeFragment);
		}
		if(hasPopupContainerLayout() 
				&& (activeFragment=getSupportFragmentManager().findFragmentById(R.id.fragment_container_floating_right))!=null 
				&& activeFragment.isResumed()){
			result.add((BaseFragment) activeFragment);
		}*/
		return result;
	}
	
	protected Fragment getMainFragment() {
		Fragment fragment =  getSupportFragmentManager().findFragmentById(getMainFragmentContainerId());
		if(fragment!=null && fragment.isAdded()) return fragment;
		//if(fragment!=null && SportingbetApplication.DEBUG) throw new RuntimeException("MainFragment not visible: "+fragment.getTag());
		return null;
	}
	
	public boolean hasTwoPaneLayout(){
		return findViewById(R.id.fragment_container_detail)!=null;
	}

	/**
	 * Show a new fragment, add it to the backStack if required, and report the transaction to {@link #pushFragmentTransactionToGTM(Fragment, String)}
	 * @param containerId id of the container you want to put the Fragment in. Defaults to {@link #getMainFragmentContainerId()}
	 * @param fragment the new Fragment
	 * @param tag tag for the Fragment transaction, can be used later with {@link FragmentManager#findFragmentByTag(String)}
	 * @param addToBackStack
	 * @return Returns the identifier of this transaction's back stack entry, if <code>addToBackStack<code> is true. Otherwise, returns a negative number.
	 */
	public int transactTo(int containerId, Fragment fragment, String tag, boolean addToBackStack){
		return transactTo(containerId, fragment, tag, addToBackStack, 0, 0, 0, 0);
	}
	/**
	 * Show a new fragment, add it to the backStack if required, and report the transaction to {@link #pushFragmentTransactionToGTM(Fragment, String)}
	 * @param containerId id of the container you want to put the Fragment in. Defaults to {@link #getMainFragmentContainerId()}
	 * @param fragment the new Fragment
	 * @param tag tag for the Fragment transaction, can be used later with {@link FragmentManager#findFragmentByTag(String)}
	 * @return Returns the identifier of this transaction's back stack entry
	 */
	public int transactTo(int containerId, Fragment fragment, String tag){
		return transactTo(containerId, fragment, tag, true, 0, 0, 0, 0);
	}
	/**
	 * Show a new fragment, add it to the backStack if required, and report the transaction to {@link #pushFragmentTransactionToGTM(Fragment, String)}
	 * @param containerId id of the container you want to put the Fragment in. Defaults to {@link #getMainFragmentContainerId()}
	 * @param fragment the new Fragment
	 * @param tag tag for the Fragment transaction, can be used later with {@link FragmentManager#findFragmentByTag(String)}
	 * @param addToBackStack
	 * @param animEnter
	 * @param animExit
	 * @param animPopEnter
	 * @param animPopExit
	 * @return Returns the identifier of this transaction's back stack entry, if <code>addToBackStack<code> is true. Otherwise, returns a negative number.
	 */
	public int transactTo(int containerId, Fragment fragment, String tag, boolean addToBackStack, int animEnter, int animExit, int animPopEnter, int animPopExit){
		if(mNavigationDisabled) return -1;
		if (containerId==0) containerId = getMainFragmentContainerId();
		FragmentManager fragmentManager = getSupportFragmentManager();

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


		if(animEnter!=0 && animExit!=0 && animPopEnter!=0 && animPopExit!=0){
			fragmentTransaction.setCustomAnimations(animEnter, animExit, animPopEnter, animPopExit);
		}
		else if(animEnter!=0 || animExit!=0){
			fragmentTransaction.setCustomAnimations(animEnter, animExit);
		}
		else if(containerId==getMainFragmentContainerId()) fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fragmentTransaction.replace(containerId, fragment, tag);



		if(mFirstFragmentTag==null){
			//first transaction
			mFirstFragmentTag = tag;
			Log.d(getClass().getName(), "not adding " + tag + " to backStack, setting mFirstFragmentTag");
		}
		else if (addToBackStack){
			Log.d(getClass().getName(), "adding "+tag+" to backStack");
			fragmentTransaction.addToBackStack(tag);
		}
		else{
			Log.d(getClass().getName(), "not adding "+tag+" to backStack, mFirstFragmentTag is "+mFirstFragmentTag);
		}

		int backStackID = fragmentTransaction.commit();
		if(ConnectedApp.GTM_ENABLED) pushFragmentTransactionToGTM(fragment, tag);

		return backStackID;
	}
	protected void pushFragmentTransactionToGTM(Fragment fragment, String tag){
		//TagManagerUtil.DataLayerValuesBuilder builder = new TagManagerUtil.DataLayerValuesBuilder();
		//builder.addScreenName(this, fragment);
		//TagManagerUtil.pushEvent(TagManagerUtil.EVENT_ONLOAD_SCREENVIEW, builder.getMap());
	}
	public void clearTop(){
		FragmentManager fm = getSupportFragmentManager();
		if(fm.getBackStackEntryCount()>0) try{
			fm.popBackStackImmediate(fm.getBackStackEntryAt(0).getId(),FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
		catch(IllegalStateException e){
			//Random error clearing to top. Not critical if this did not succeed, no need to crash.
			if(ConnectedApp.DEBUG) throw e;
		}
	}
	
	
	
	

	public int getMainFragmentContainerId() {		
		return R.id.fragment_container_main;
	}
	
	public boolean tryDismissDialog(int id){
		try{
			dismissDialog(id);
			return true;
		}
		catch(IllegalArgumentException e){
			return false;
		}
	}
	
	@Override
	public Dialog onCreateDialog(int id, Bundle args){
		
		if(id== DIALOG_ID_LOADING || id==DIALOG_ID_SUBMITTING){
			ProgressDialog progressDialog= new ProgressDialog(BaseActivity.this);
			progressDialog.setCancelable(false);
			progressDialog.setMessage(getString(id));
			return progressDialog;
		}
		return super.onCreateDialog(id);
	}
	
	
	
	public void notifyRefreshing(Fragment fragment, boolean refreshing) {
		//Log.d("DogsRefreshNotify", fragment.getTag()+": "+refreshing);

		if(fragment instanceof BaseFragment){
			//if(((BaseFragment) fragment).isRequesting()) setRefreshActionButtonState(true);
			//refreshingFragments.put(fragment.getTag(), ((BaseFragment) fragment).isRequesting());			
			refreshingFragments.put(fragment.getTag(), refreshing);			
			updateRefreshActionButtonState();
		}
	}
	
	/**
	 * a hashmap of refreshing fragments didn't work because fragments don't call handlereceivedresult
	 * unless it is visible. So fragments offscreen in a viewpager could finish their request but not receive it,
	 * so it would keep the spinner refreshing.
	 */
	private HashMap<String, Boolean> refreshingFragments = new HashMap<String, Boolean>();
	
	@Override
	public void onAttachFragment(Fragment fragment){		
		if(fragment instanceof BaseFragment){
			//if(((BaseFragment) fragment).isRequesting()) setRefreshActionButtonState(true);
			
			//Log.d("DogsRefreshAttach", fragment.getTag()+": "+((BaseFragment) fragment).isRequesting());
			refreshingFragments.put(fragment.getTag(), ((BaseFragment) fragment).isRequesting());			
			updateRefreshActionButtonState();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(mHasFeatureIndeterminateProgress){
			getMenuInflater().inflate(R.menu.fragment_refresh, menu);
			menu.findItem(R.id.menu_refresh).setEnabled(false).setVisible(false);
		}

		return true; 
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		super.onPrepareOptionsMenu(menu);
		mOptionsMenu = menu;
		//if(mPendingMenuRefreshState!=null) setRefreshActionButtonState(mPendingMenuRefreshState);
		updateRefreshActionButtonState();
		updateUserHeader();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_user_header: {
				//TODO
				return true;
			}
			case R.id.menu_refresh:
				Fragment fragment = getMainFragment();
				if(fragment instanceof BaseFragment)
					return ((BaseFragment) fragment).onRefreshButtonClicked();
		}
		return super.onOptionsItemSelected(item);
	}

	protected void onUserHeader(){
		updateUserHeader();
	}
	protected void updateUserHeader(){
		final MenuItem userHeaderMenu=(mOptionsMenu!=null)?mOptionsMenu.findItem(R.id.menu_user_header):null;
		if(userHeaderMenu!=null){
			View userHeaderView = MenuItemCompat.getActionView(userHeaderMenu);
			if(userHeaderView==null){
				userHeaderView = getLayoutInflater().inflate(R.layout.actionbar_user_header, null, false);
				userHeaderView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onOptionsItemSelected(userHeaderMenu);
					}
				});
				MenuItemCompat.setShowAsAction(userHeaderMenu, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
				MenuItemCompat.setActionView(userHeaderMenu, userHeaderView);
			}
			UserHeader header = Session.getInstance().getCustomerHeader();
			if(header!=null){
				((TextView)userHeaderView.findViewById(R.id.text1)).setText("hi");



				userHeaderMenu.setTitle(header.userName.getUserNameDisplay());
				userHeaderMenu.setVisible(true);
			}
			else{
				userHeaderMenu.setTitle("Login");
				userHeaderMenu.setVisible(false);
			}
		}
	}


	
	protected void updateRefreshActionButtonState(){
		boolean any = refreshingFragments.containsValue(Boolean.TRUE);
		setRefreshActionButtonState(any);
	}
	private boolean mHasFeatureIndeterminateProgress;
	protected void setRefreshActionButtonState(boolean refreshing) {
		if (mOptionsMenu == null) {
			//	mPendingMenuRefreshState=refreshing;
			return;
		}

		final MenuItem refreshItem = mOptionsMenu.findItem(R.id.menu_refresh);

		if (refreshItem != null) {
			if (refreshing) {
				View actionView = MenuItemCompat.getActionView(refreshItem);
				if(actionView==null || actionView.findViewById(R.id.progress_circular_ab)==null){
					MenuItemCompat.setActionView(refreshItem, R.layout.actionbar_indeterminate_progress);
					actionView = MenuItemCompat.getActionView(refreshItem);
					if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
						//Framework bug https://code.google.com/p/android/issues/detail?id=162416
						ProgressBar progressBar = (ProgressBar) actionView.findViewById(R.id.progress_circular_ab);
						progressBar.setIndeterminate(true);
						progressBar.setIndeterminateTintMode(PorterDuff.Mode.SRC_IN);
						progressBar.setIndeterminateTintList(getResources().getColorStateList(R.color.action_bar_title));
						//progressBar.setBackgroundTintList(getResources().getColorStateList(R.color.action_bar_title));
					}
				}
				refreshItem.setVisible(true);
			} else {
				MenuItemCompat.setActionView(refreshItem, null);
				if(mHasFeatureIndeterminateProgress) refreshItem.setVisible(false);
			}
		}
		mPendingMenuRefreshState = null;
		//if(mHasFeatureIndeterminateProgress)
		//setSupportProgressBarIndeterminateVisibility(refreshing);
	}

	//@Override
	public boolean supportRequestWindowFeatureBaseActivity(int featureId){
		if(featureId== Window.FEATURE_INDETERMINATE_PROGRESS){
			mHasFeatureIndeterminateProgress=true;
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		Fragment fragment =getMainFragment();
		if(!(fragment instanceof BaseFragment)){
			super.onBackPressed();
		}

		else if (((BaseFragment)fragment).onBackKeyPressed()){
			super.onBackPressed();
		}
	}
	
	
	
	public boolean isXLarge(){
		return ((ConnectedApp)getApplication()).isXLarge();
		
	}
	
	/**
	 * 
	 * @return Configuration.ORIENTATION_SQUARE, portrait or landscape
	 */
	public int getScreenOrientation()
	{
	    Display getOrient = getWindowManager().getDefaultDisplay();
	    int orientation = Configuration.ORIENTATION_UNDEFINED;
	    if(getOrient.getWidth()==getOrient.getHeight()){
	        orientation = Configuration.ORIENTATION_SQUARE;
	    } else{ 
	        if(getOrient.getWidth() < getOrient.getHeight()){
	            orientation = Configuration.ORIENTATION_PORTRAIT;
	        }else { 
	             orientation = Configuration.ORIENTATION_LANDSCAPE;
	        }
	    }
	    return orientation;
	}
	

	

	
	/**
	 * If the result is an ErrorInfo, handle it (by displaying a dialog, checking for logintimeout etc), otherwise
	 * return a string with the error
	 * @param resultCode
	 * @param resultData
	 * @return string error Message if the result is an error, null otherwise
	 */
	public String handleUnknownResult(int resultCode, Bundle resultData){
		Log.d("BaseActivity", "handleUnknownResult code"+resultCode);
		
		return null;
	}
	
	/**
	 * If there's an update availabe, but no force update, then wait this long between showing the dialog.
	 */
	static final long UPDATE_REMINDER_INTERVAL = DateUtils.DAY_IN_MILLIS*3L;



    public void setNavigationDisabled(boolean navigationDisabled){
        mNavigationDisabled=navigationDisabled;
        View rootView;
        if(navigationDisabled && (rootView=findViewById(R.id.drawer_layout))!=null){
            rootView.setVisibility(View.INVISIBLE);
        }
    }
    protected boolean isNavigationDisabled(){
        return mNavigationDisabled;
    }
	
	
	 /**
     * Converts an intent into a {@link Bundle} suitable for use as fragment arguments.
     */
    protected static Bundle intentToFragmentArguments(Intent intent) {
        Bundle arguments = new Bundle();
        if (intent == null) {
            return arguments;
        }

        final Uri data = intent.getData();
        if (data != null) {
            arguments.putParcelable("_uri", data);
        }

        final Bundle extras = intent.getExtras();
        if (extras != null) {
            arguments.putAll(intent.getExtras());
        }

        return arguments;
    }

    /**
     * Converts a fragment arguments bundle into an intent.
     */
    public static Intent fragmentArgumentsToIntent(Bundle arguments) {
        Intent intent = new Intent();
        if (arguments == null) {
            return intent;
        }

        final Uri data = arguments.getParcelable("_uri");
        if (data != null) {
            intent.setData(data);
        }

        intent.putExtras(arguments);
        intent.removeExtra("_uri");
        return intent;
    }
}
