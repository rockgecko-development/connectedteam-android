package au.com.connectedteam.activity.home;

import android.os.Bundle;
import android.view.Window;

import au.com.connectedteam.R;
import au.com.connectedteam.activity.BaseActivity;

/**
 * Created by bramleyt on 25/10/2015.
 */
public class CreateEventActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeatureBaseActivity(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        if(getSupportFragmentManager().findFragmentById(R.id.fragment_container_main)==null){
            transactTo(0, new CreateEventFragment(), CreateEventFragment.TAG);
        }
    }
}
