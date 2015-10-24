package au.com.connectedteam.activity.home;

import android.os.Bundle;

import au.com.connectedteam.R;
import au.com.connectedteam.activity.BaseActivity;

/**
 * Created by bramleyt on 24/10/2015.
 */
public class PreferencesActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        if(getSupportFragmentManager().findFragmentById(R.id.fragment_container_main)==null){
            transactTo(0, new PreferencesFragment(), PreferencesFragment.TAG);
        }
    }
}
