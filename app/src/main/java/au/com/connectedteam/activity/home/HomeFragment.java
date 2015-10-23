package au.com.connectedteam.activity.home;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import au.com.connectedteam.BuildConfig;
import au.com.connectedteam.R;
import au.com.connectedteam.activity.BaseFragment;
import au.com.connectedteam.activity.settings.UserSettingsActivity;
import au.com.connectedteam.application.ConnectedApp;
import au.com.connectedteam.application.Session;
import au.com.connectedteam.util.StringUtils;

import java.util.Date;

/**
 * Created by bramleyt on 17/07/2015.
 */
public class HomeFragment extends BaseFragment{
    public static final String TAG = "HomeFragment";
    private AQuery aq;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        aq=new AQuery(view);
        aq.id(R.id.login_button).clicked(this, "onLoginSignupClicked");
        aq.id(R.id.signup_button).clicked(this, "onLoginSignupClicked");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        doDebugText();
        doLoginSignupButtons();
    }
    @Override
    public void onSessionChanged(Session.WhatChanged whatChanged){
        switch(whatChanged){
            case LOG_IN_OUT:
            case RE_AUTH:
            case RE_AUTH_FAIL:
            case USER_HEADER:
                doLoginSignupButtons();
                getBaseActivity().notifyRefreshing(this, isRequesting());
                break;
            default: break;
        }
    }



    public void onLoginSignupClicked(View v){
        switch(v.getId()){
            case R.id.login_button:
                ((HomeActivity)getActivity()).navigateLogin(null);
                break;
            case R.id.signup_button:
                ((HomeActivity)getActivity()).navigateSignup();
                break;
        }
    }

    private void doLoginSignupButtons(){
        boolean loggingIn = Session.getInstance().isLoggedIn() || Session.getInstance().isExecutingAutoLogin();
        aq.id(R.id.login_button).enabled(!loggingIn);
        aq.id(R.id.signup_button).enabled(!loggingIn);

    }
    private void doDebugText(){
        TextView textView = (TextView) getView().findViewById(R.id.debug_text);
        if(ConnectedApp.DEBUG){
            String versionStr="";
            try{
                PackageInfo pi = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                versionStr = String.format("v%s build %d", pi.versionName, pi.versionCode);
            }
            catch(PackageManager.NameNotFoundException e){
            }
            String debugText = String.format("%s %s %s\nAPI: %s",
                    getString(R.string.app_name),
                    versionStr,
                    StringUtils.formatDate(ConnectedApp.getBuildDate(), StringUtils.DATE_AND_TIME_STANDARD),
                    ConnectedApp.getWebserviceUrl()
            );
            textView.setText(debugText);
            textView.setBackgroundResource(R.drawable.button_transparent);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UserSettingsActivity.class);
                    getActivity().startActivity(intent);
                }
            });
        }
        else{
            textView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean isRequesting() {
        return Session.getInstance().isExecutingAutoLogin();
    }

    @Override
    public boolean onRefreshButtonClicked() {
        return false;
    }

    @Override
    public boolean onBackKeyPressed() {
        return true;
    }
}
