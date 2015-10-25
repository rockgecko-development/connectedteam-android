package au.com.connectedteam.activity;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import au.com.connectedteam.application.ConnectedApp;
import au.com.connectedteam.application.Session;


public abstract class BaseFragment extends Fragment{
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		if (!(activity instanceof BaseActivity)) 
			throw new ClassCastException(activity.toString() + " must extend from BaseActivity");
	}
	public BaseActivity getBaseActivity(){
		return (BaseActivity)getActivity();
	}
	
	public abstract boolean isRequesting();
	
	public abstract boolean onRefreshButtonClicked();
	
	/**
	 * Called by the Base Activity when something in the session changes.
	 * Default implementation does nothing.
	 * Called in the UI thread.
	 * @param whatChanged
	 */
	public void onSessionChanged(Session.WhatChanged whatChanged){
		
	}
	
	/**
	 * Called when back key pressed in BaseActivity. Return true if you want BaseActivity to popBackStack,
	 * false for nothing to happen.
	 * @return
	 */
	public abstract boolean onBackKeyPressed();



}
