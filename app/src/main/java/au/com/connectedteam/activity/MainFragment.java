package au.com.connectedteam.activity;


import com.androidquery.AQuery;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.connectedteam.BuildConfig;
import au.com.connectedteam.R;
import au.com.connectedteam.application.ConnectedApp;
import au.com.connectedteam.util.StringUtils;
import au.com.connectedteam.util.UIUtil;

import java.util.Date;

public class MainFragment extends BaseFragment{

	AQuery aq;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup base, Bundle savedInstanceState){
		View root = inflater.inflate(R.layout.fragment_main, base, false);
		aq = new AQuery(root);

		UIUtil.setTightCompoundDrawables(aq.id(R.id.button_styled).getButton());
		return root;
	}
	@Override
	public boolean isRequesting() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onRefreshButtonClicked() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onBackKeyPressed() {
		// TODO Auto-generated method stub
		return true;
	}
    @Override
    public void onResume() {
        super.onResume();
        doFooterText();
    }

    private void doFooterText(){
        if(ConnectedApp.DEBUG){



            String versionStr = String.format("v%s build %d", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);


            String debugText = String.format("%s %s %s %s\nAPI: %s",
                    getString(R.string.app_name),
                    versionStr,
                    StringUtils.formatDate(ConnectedApp.getBuildDate(), StringUtils.DATE_AND_TIME_STANDARD),
                    BuildConfig.FLAVOR,
                    ConnectedApp.getWebserviceUrl());

            aq.id(R.id.debug_text).text(debugText);
        }
        else{
            aq.id(R.id.debug_text).gone();
        }
    }
}
